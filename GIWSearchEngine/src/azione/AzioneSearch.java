package azione;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.lucene.queryparser.classic.ParseException;

import risorse.SearchFiles;

public class AzioneSearch extends Azione {

	@Override
	public String esegui(HttpServletRequest request) throws ServletException {
		String query = request.getParameter("query");
		HttpSession session = request.getSession();
		try {
			session.setAttribute("docs", SearchFiles.search(query));
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "errore";
		}
//		if (utente != null) {
//			session.setAttribute("utente", utente);
//			return "loginOK";
//		} else {
//			session.setAttribute("errore", "Nome utente o password errati.");
//			return "loginKO";
//		}
		return "cercato";
	}
}
