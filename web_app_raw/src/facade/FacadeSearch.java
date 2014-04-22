package facade;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.search.ScoreDoc;

import resurce.*;

public class FacadeSearch implements Facade{
	private HttpServletRequest request;
	private int dim;
	public FacadeSearch(HttpServletRequest request){
		this.request = request;
		this.dim = 5;
	}
	@Override
	public Object service() {
		try{
		Searcher search = new Searcher((this.request.getParameter("query")),this.dim);
		request.getSession().setAttribute("risultatoSearch",search.getReadableQuery());
		DocumentResult[] doc1 = search.search();
		DocumentResult[][] doc2 = new DocumentResult[doc1.length/dim][dim];
		for(int i=0;i<doc1.length;i++){
			doc2[i/dim][i%dim]=doc1[i];
		}
		return doc2;
		}catch(Exception e){
			return null;
		}
	}
	
	

}
