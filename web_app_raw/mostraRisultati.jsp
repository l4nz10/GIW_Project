<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0);
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Progetto GIW</title>
</head>
<body>
	<div id="header">NOMEGRUPPO GIW</div>
	<div id="menu">
		<%@ page import="javax.servlet.*, java.util.*, resource.*"%>
		<%ResultsOfSearch ros = (ResultsOfSearch) session.getAttribute("risultato");%>
		<%int numeroPagina = (Integer) session.getAttribute("paginaCorrente");%>
		<%if (ros.thereIsNoResult()) { %>
			<h2>No results for: <%=ros.getQuery() %></h2>
		<% } else  { %>
		<%		if (ros.isSuggestedSearch()) {%>
					<h2>Maybe did you mean: <%=ros.getSuggestedQuery()%></h2>
					<h3>instead of: <%=ros.getQuery() %></h3>
				<%} else { %>
					<h2>Search results for: <%=ros.getQuery()%></h2>
		<% 		  } %>
		<%		DocumentResult[][] docs = ros.getDocResArray(); %>				
			<ul>
				<%
					for (int i = 0; i <docs[numeroPagina].length ; i++) {
						if (docs[numeroPagina][i] != null) {
				%>
				<li><a href="<%=docs[numeroPagina][i].getRelativePath()%>"><%=docs[numeroPagina][i].getTitle()%></a>
					<p><%=docs[numeroPagina][i].getHighlights()%></p></li>
				<%
						}
					}
				%>
			</ul>
			<%	if (numeroPagina != 0) { %>
			<a href="prev">Previous</a>
			<% } %>
			<p><%=numeroPagina + 1%></p>
			<%	if (numeroPagina < docs.length-1) {	%>
				<a href="next">Next</a>
			<% }%>
		<% } %>



	</div>

	<div id="footer">FOOTER DEL GRUPPO GIW</div>
</body>
</html>