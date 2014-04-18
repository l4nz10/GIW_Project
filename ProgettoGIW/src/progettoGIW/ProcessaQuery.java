package progettoGIW;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.*;
import helper.*;
import action.*;

@WebServlet("/ProcessaQuery")
public class ProcessaQuery extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    					throws IOException, ServletException {

		String prossimaPagina = "/Fallimento.jsp";
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    					throws IOException, ServletException {
		String prossimaPagina = "/Fallimento.jsp";
		
		Helper help = new SearchHelper();
		if (help.convalida(request.getParameter("query"))){
			Action azione = new SearchAction();
			if (azione.esegui(request).equals("OK")){
				prossimaPagina = "/mostraRisultati.jsp";
				request.getSession().setAttribute("paginaCorrente", 0);
			}
		}
		
		ServletContext application  = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}
}
