package resurce;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

	static final String INDEX_PATH = "lucene\\index\\";
	static final String DICTIONARY_PATH = "lucene\\spellchecker\\";
	private final float SCORE_THRESHOLD = 0.5f;
	private final String FIELD = "contents";
	
	
	private String readableQuery = "";
	private int hitsPerPage;
	private  String query;
	private  StandardAnalyzer analyzer;
	private  IndexReader reader;
	private  IndexSearcher searcher;
	private  QueryParser parser;

	public Searcher(String query,int hitsPerPage) throws IOException {
		this.hitsPerPage = hitsPerPage;
		this.query = query;
		reader = DirectoryReader.open(FSDirectory.open(new File(INDEX_PATH)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, FIELD, analyzer);
	}

	
	public String getReadableQuery(){
		return this.readableQuery;
	}


	//questo main deve diventare il metodo Execute di ActionSearch, dalla sessione prenderà la linea
	//di ricerca che l'utente ha digitato e la butta in una variabile stringa
	public DocumentResult[] search() throws Exception{
		
		boolean suggestedSearch = false;
		String readableNewQuery = "";

		//la variabile stringa qui è "line"

		String line = query;//session.get(..)...
		try {
				//questo pezzo è standard, non c'è bisogno di modifica. Lucene fa tutto da solo.
				Query query = parser.parse(line);
				//readableQuery serve per mandare sulla pagina la frase che l'utente ha cercato ("Risultati per: pippo pluto...")
				readableQuery = query.toString(FIELD);
				//htmlFormatter serve per formattare il testo dell'highlighter in modo che
				//le frasi che matchano la ricerca siano marcate in <b>old
				SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
				Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

				TopDocs results = searcher.search(query, 5 * hitsPerPage);
				ScoreDoc[] hits = results.scoreDocs;
				//se non ci sono risultati o non sono soddisfacenti viene invocato "suggestQuery",
				//che sarebbe il metodo "Forse cercavi..." con una frase intera (lucene lo supporta nativamente
				//solo con una singola parola).
				//una volta che è stato tornata una query alternativa controlla se è ""
				if (results.scoreDocs.length == 0 || results.getMaxScore() <= SCORE_THRESHOLD) {
					Query newQuery = suggestQuery(query, FIELD);
					//
					TopDocs newResults = searcher.search(newQuery, 5 * hitsPerPage);
					//controlla se i risultati della nuova query sono migliori della vecchia,
					//se sì sostituisce i risultati della vecchia con quella nuova.
					if (results.totalHits < newResults.totalHits) {
						suggestedSearch = true;
						hits = newResults.scoreDocs;
						readableNewQuery = newQuery.toString(FIELD);
					}
				}

				//invece di stamparli, qui metti in sessione la frase che torna dopo la ricerca.
				if (suggestedSearch) {
					System.out.println("Forse intendevi: "+readableNewQuery+"\nanziché: "+readableQuery);
					readableQuery = readableNewQuery;
				
				}
				return this.createDocumentsResulted(hits,query);

			} catch (ParseException e) {
				return null;
			}
	}

		
	private TextFragment[] getHighlights(int docId, Highlighter highlighter) throws Exception {
		Document doc = searcher.doc(docId);
		String documentContent = doc.get(FIELD);
		TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, FIELD, analyzer);
		TextFragment[] fragments = highlighter.getBestTextFragments(tokenStream, documentContent, true, 10);
		return fragments;
	}

	// Suggest a possible more optimal query for the search,
	// trying to replace the words inside the query that are not relevant.
	private Query suggestQuery(Query query, String field) throws ParseException, IOException {

		StringTokenizer tokenizer = new StringTokenizer(query.toString(field));
		String word;
		StringBuilder builder = new StringBuilder();
		String[] modifiers = new String[]  { "+", "-", "\"" };
		
		while (tokenizer.hasMoreTokens()) {
			Character headModifier = null;
			Character tailQuotMark = null;
			word = tokenizer.nextToken();
			if (word.equals("AND") || word.equals("OR") || word.equals("NOT")) {
				builder.append(word);
			} else {
				if (StringUtils.startsWithAny(word, modifiers)) {
					headModifier = word.charAt(0);
					word = word.substring(1, word.length());
				}
				if (word.endsWith("\"")) {
					tailQuotMark = word.charAt(word.length() - 1);
					word = word.substring(0, word.length() - 1);
				}
				word = findMoreRelevantWord(word);
				if (headModifier != null)
					builder.append(headModifier);
				builder.append(word);
				if (tailQuotMark != null)
					builder.append(tailQuotMark);
				builder.append(" ");
			}
		}
		return parser.parse(builder.toString());
	}

	// finds a possible new word more relevant than the original.
	// In case no other word seems to be 'more right', the method returns
	// the original.
	private String findMoreRelevantWord(String word) throws IOException,
			ParseException {
		String[] suggestions = suggestWords(word, 5, 0.6f);
		if (suggestions.length != 0) {
			for (int i = 0; i < 2 && i < suggestions.length; i++) {
				String newWord = suggestions[i];
				TopDocs resOriginalWord = searcher.search(parser.parse(word),
						10);
				TopDocs resNewWord = searcher.search(parser.parse(newWord), 10);
				if (resOriginalWord.totalHits < resNewWord.totalHits)
					word = newWord;
			}
		}
		return word;
	}

	// suggest a list of words similar to the one passed as parameter.
	// Uses the basic dictionary of Lucene.
	private String[] suggestWords(String word, int numberOfSuggestions,
			float accuracy) {
		try {
			Directory dictionaryDir = FSDirectory
					.open(new File(DICTIONARY_PATH));
			SpellChecker spellChecker = new SpellChecker(dictionaryDir);
			String[] similarWords = spellChecker.suggestSimilar(word,
					numberOfSuggestions, accuracy);
			spellChecker.close();
			return similarWords;
		} catch (final Exception e) {
			return new String[0];
		}
	}

	// simple method to navigate the results in the console.
	// Also responsible for listing the results.
	private DocumentResult[] createDocumentsResulted(ScoreDoc[] hits, Query query) throws Exception {
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		DocumentResult[] docRes = new DocumentResult[hits.length];
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);				
			String path = doc.get("path");
			if (path != null) {
				docRes[i].setRelativePath(path);
				String title = doc.get("title");
				if (title != null) {
					docRes[i].setTitle(title);
				}
				StringBuilder builder = new StringBuilder();
				for (TextFragment frag : getHighlights(docId, highlighter))
					if ((frag != null) && (frag.getScore() > 0)) {
						String snippet = frag.toString().replaceAll("\\s+"," ");
				        builder.append(snippet).append("...");
					}
				if (builder.length() != 0)
					docRes[i].setHighlights(builder.toString());
			}
		}
		return docRes;
	}
}