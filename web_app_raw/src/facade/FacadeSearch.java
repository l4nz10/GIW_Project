package facade;

import javax.servlet.http.HttpServletRequest;
import resource.*;

public class FacadeSearch implements Facade {
	
	private HttpServletRequest request;
	private int dim;

	public FacadeSearch(HttpServletRequest request) {
		this.request = request;
		this.dim = 5;
	}

	@Override
	public ResultsOfSearch service() {
		try {
			String query = this.request.getParameter("query");
			Searcher searcher = new Searcher(query, this.dim);
			boolean forced = false;
			if (this.request.getParameter("forced") != null)
				forced = Boolean.parseBoolean(this.request.getParameter("forced"));
			return searcher.search(forced);			
		} catch (Exception e) {
			return null;
		}
	}

}
