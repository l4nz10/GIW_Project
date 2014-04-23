package action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import resource.*;
import facade.*;

public class SearchAction implements Action{
	
	@Override
	public String esegui(HttpServletRequest request) throws ServletException {
		Facade facadeSearch = new FacadeSearch(request);
		ResultsOfSearch risultatoService = (ResultsOfSearch) facadeSearch.service();
		
		if (risultatoService != null){
			request.getSession().setAttribute("risultato", (ResultsOfSearch) risultatoService);
			return "OK";
		} else {
			request.getSession().setAttribute("risultato", null);
			return "KO";
		}
	}
}
