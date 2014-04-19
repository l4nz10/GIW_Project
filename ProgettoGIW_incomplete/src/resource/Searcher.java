package resource;
import resource.DocumentResult;

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

	static final String INDEX_PATH = "D:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\ProgettoGIW\\lucene\\index\\";
	static final String DICTIONARY_PATH = "D:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\ProgettoGIW\\lucene\\spellchecker\\";
	private final float SCORE_THRESHOLD = 0.5f;
	private final String FIELD = "contents";
	
	private String readableQuery = "";
	private int hitsPerPage;
	private  String query;
	private  StandardAnalyzer analyzer;
	private  IndexReader reader;
	private  IndexSearcher searcher;
	private  QueryParser parser;

	public Searcher(String query, int hitsPerPage) throws IOException {
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

	public DocumentResult[] search() throws Exception{
		
		boolean suggestedSearch = false;
		String readableNewQuery = "";
		String line = query;
		
		try {
				Query query = parser.parse(line);
				readableQuery = query.toString(FIELD);

				TopDocs results = searcher.search(query, 5 * hitsPerPage);
				ScoreDoc[] hits = results.scoreDocs;
		
				if (results.scoreDocs.length == 0 || results.getMaxScore() <= SCORE_THRESHOLD) {
					Query newQuery = suggestQuery(query, FIELD);
					TopDocs newResults = searcher.search(newQuery, 5 * hitsPerPage);
					if (results.totalHits < newResults.totalHits) {
						suggestedSearch = true;
						hits = newResults.scoreDocs;
						readableNewQuery = newQuery.toString(FIELD);
					}
				}

				if (suggestedSearch) {
					System.out.println("Forse intendevi: "+readableNewQuery);
					System.out.println("anziché: "+readableQuery);
					readableQuery = readableNewQuery;
				
				}
				return createDocumentsResulted(hits,query);

			} catch (ParseException e) {
				return null;
			}
	}

		
	private TextFragment[] getHighlights(int docId, Highlighter highlighter) throws IOException, InvalidTokenOffsetsException{
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
	private String findMoreRelevantWord(String word) throws IOException, ParseException {
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
			Directory dictionaryDir = FSDirectory.open(new File(DICTIONARY_PATH));
			SpellChecker spellChecker = new SpellChecker(dictionaryDir);
			String[] similarWords = spellChecker.suggestSimilar(word, numberOfSuggestions, accuracy);
			spellChecker.close();
			return similarWords;
		} catch (final Exception e) {
			return new String[0];
		}
	}

	//creates an array of specific classes named [DocumentResult] that stores all the fields
	//we need to show on the results page for each document matched in the search.
	private DocumentResult[] createDocumentsResulted(ScoreDoc[] hits, Query query) throws IOException, InvalidTokenOffsetsException {
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		DocumentResult[] docResArray = new DocumentResult[hits.length];
		
		for (int i = 0; i < hits.length; i++) {
			DocumentResult docRes = new DocumentResult();
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);				
			String shortPath = doc.get("shortPath");
			if (shortPath != null) {
				docRes.setRelativePath(shortPath);
				String title = doc.get("title");
				if (title != null) {
					docRes.setTitle(title);
				}
				StringBuilder builder = new StringBuilder();
				for (TextFragment frag : getHighlights(docId, highlighter))
					if ((frag != null) && (frag.getScore() > 0)) {
						String snippet = frag.toString().replaceAll("\\s+"," ");
				        builder.append(snippet).append("...");
					}
				if (builder.length() != 0)
					docRes.setHighlights(builder.toString());
			}
			docResArray[i] = docRes;
		}
		return docResArray;
	}
}