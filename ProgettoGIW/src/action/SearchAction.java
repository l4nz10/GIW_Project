package action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import resurce.*;

import facade.*;

public class SearchAction implements Action{
	@Override
	public String esegui(HttpServletRequest request) throws ServletException {
		Facade facadeSearch = new FacadeSearch(request);
		Object risultatoService = facadeSearch.service();
		if (risultatoService!=null){
			request.getSession().setAttribute("risultato", (DocumentResult[][])risultatoService);
		return "OK";
		}else
			return "KO";
	}

}
