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

@WebServlet("/prev")
public class ProcessaPrev extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    					throws IOException, ServletException {

		String prossimaPagina = "/mostraRisultati";
		ServletContext application  = getServletContext();
		request.getSession().setAttribute("numeroPagina",((int)request.getSession().getAttribute("paginaCorrente")-1));
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}
	
}
