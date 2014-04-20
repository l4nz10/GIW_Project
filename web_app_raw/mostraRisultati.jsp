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
		<title>Rooma 3 search engine</title>
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
		<link href="css/mostraRisultati.css" rel="stylesheet">
	</head>
	<body>
		<div id="results_area" class="col-md-7">
			<%@ page import="javax.servlet.*, java.util.*, resource.*"%>
			<% ResultsOfSearch ros = (ResultsOfSearch) session.getAttribute("risultato"); %>
			<% int numeroPagina = (Integer) session.getAttribute("paginaCorrente"); %>
			<% if (ros.thereIsNoResult()) { %>
				<h2>No results for: <%=ros.getQuery() %></h2>
			<% } else  { %>
			<%		if (ros.isSuggestedSearch()) {%>
			<div id="results-message" class="col-md-12">
			<%			String ref = "ProcessaQueryForced?query="+String2URLFriendly.transform(ros.getQuery());%>

						<h1>Maybe did you mean: <em><%=ros.getSuggestedQuery()%></em>. 
							<small>
								Or <em><a href=<%=ref%>><%=ros.getQuery() %></a></em> was correct?
							</small>
						</h1>
						<!-- <h3>instead of: <a href=<%=ref%>> <%=ros.getQuery() %> </a></h3> -->
					<%} else { %>
						<h1>Search results for: <em><%=ros.getQuery()%></em></h1>
			<% 		  } %>
			</div>
			<%	DocumentResult[][] docs = ros.getDocResArray(); %>				
					<%	for (int i = 0; i < docs[numeroPagina].length ; i++) {	%>
					<%		if (docs[numeroPagina][i] != null) {	%>
					<div id="result">
						<div id="result-title">
							<a href="<%=docs[numeroPagina][i].getRelativePath()%>"><%=docs[numeroPagina][i].getTitle()%></a></dt>
						</div>
						<div id="result-highlights">
							<%=docs[numeroPagina][i].getHighlights()%></dd>
						</div>
					</div>
					<%
							}
						}
					%>
				<%	if (numeroPagina != 0) { %>
				<a href="prev">Previous</a>
				<% } %>
				<p><%=numeroPagina + 1%></p>
				<%	if (numeroPagina < docs.length-1) {	%>
					<a href="next">Next</a>
				<% }%>
			<% } %>
		</div>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
</body>
</html>