package controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import azione.Azione;

public class Controller extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Map<String, String> comando2azione;
	private Map<String, String> esito2pagina;
	Logger logger = Logger.getLogger("log");
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String prossimaPagina = null;
		String comando = this.leggiComando(request.getServletPath());
		String nomeAzione = this.comando2azione.get(comando);
		if (nomeAzione == null) {
			prossimaPagina = "/error.jsp";
		} else {
			Azione azione = null;
			try {
				azione = (Azione)Class.forName(nomeAzione).newInstance();
				String esitoAzione = azione.esegui(request);
				logger.info("Esito Azione: "+esitoAzione);
				prossimaPagina = this.esito2pagina.get(esitoAzione);
			} catch (InstantiationException e) {
				prossimaPagina = "/error.jsp";
			} catch (IllegalAccessException e) {
				prossimaPagina = "/error.jsp";
			} catch (ClassNotFoundException e) {
				prossimaPagina = "/error.jsp";
			}
		}
		ServletContext application = getServletContext();
		logger.info("Prossima pagina: "+prossimaPagina);
		RequestDispatcher rd = application.getRequestDispatcher(prossimaPagina);
		rd.forward(request, response);
	}
	private String leggiComando(String servletPath) {
		String comando;
		comando = servletPath.substring(1, (servletPath.length()-3));
		return comando;
	}
	public void init() {
		this.comando2azione = new HashMap<String, String>();
		this.comando2azione.put("search", "azione.AzioneSearch");
//		this.comando2azione.put("verificaCliente", "azione.AzioneVerificaCliente");
//		this.comando2azione.put("noleggio", "azione.AzioneNoleggio");
//		this.comando2azione.put("confermaNoleggio", "azione.AzioneConfermaNoleggio");
		
		this.esito2pagina = new HashMap<String, String>();
		this.esito2pagina.put("cercato", "/ris.jsp");
		this.esito2pagina.put("errore", "/error.jsp");

		
	}
}
