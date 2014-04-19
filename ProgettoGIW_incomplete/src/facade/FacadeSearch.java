package facade;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.search.ScoreDoc;

import resource.*;

public class FacadeSearch implements Facade {
	private HttpServletRequest request;
	private int dim;

	public FacadeSearch(HttpServletRequest request) {
		this.request = request;
		this.dim = 5;
	}

	@Override
	public Object service() {
		Date date = new Date();
		System.out.println("PROVA DEL: "+date.getTime());
		
		try {
			Searcher search = new Searcher((this.request.getParameter("query")), this.dim);
			request.getSession().setAttribute("risultatoSearch", search.getReadableQuery());
			DocumentResult[] doc1 = search.search();
			for (int i=0; i<doc1.length; i++) {
				System.out.println("DOC1 PATH: "+doc1[i].getRelativePath());
			}
			DocumentResult[][] doc2 = new DocumentResult[doc1.length / dim][dim];
			for (int i = 0; i < doc1.length; i++) {
				doc2[i / dim][i % dim] = doc1[i];
				System.out.println(doc1[i].getTitle());
			}
			return doc2;
			
		} catch (Exception e) {
			return null;
		}
	}

}
