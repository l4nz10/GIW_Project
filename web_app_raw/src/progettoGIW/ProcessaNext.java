package progettoGIW;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.*;



@WebServlet("/next")
@SuppressWarnings("serial")
public class ProcessaNext extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String prossimaPagina = "/mostraRisultati.jsp";
		ServletContext application  = getServletContext();
		request.getSession().setAttribute("paginaCorrente",((int)request.getSession().getAttribute("paginaCorrente")+1));
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}	
}
