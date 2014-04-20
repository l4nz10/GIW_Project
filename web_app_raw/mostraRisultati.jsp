<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
			<%@page import="javax.servlet.*, java.util.*, resource.*, java.net.*"					%>
			<% ResultsOfSearch ros = (ResultsOfSearch) session.getAttribute("risultato");			%>
			<% int numeroPagina = (Integer) session.getAttribute("paginaCorrente"); 				%>
			<div id="message-area" class="col-md-12">
				<div id="message" class="col-md-12">			
			<% 		if (ros.thereIsNoResult() || ros == null) { 													%>
					<h2>No results for: <%=ros.getQuery()%></h2>
			<%		} else  {																						%>
			<%			String queryString = ros.isSuggestedSearch() ? ros.getSuggestedQuery() : ros.getQuery();	%>
			<%			if (ros.isSuggestedSearch()) {																%>
			<%			String ref = "search?query="+URLEncoder.encode(ros.getQuery(),"UTF-8")+"&forced=true";		%>
						<h1>Maybe did you mean: <em><%=ros.getSuggestedQuery()%></em>. 
							<small>
								Or <em><a href=<%=ref%>><%=ros.getQuery() %></a></em> was correct?
							</small>
						</h1>
			<%			} else {	%>
						<h1>Search results for: <em><%=ros.getQuery()%></em></h1>
			<%			}			%>
				</div>
				<div id="results_area" class="col-md-7">
			<%			DocumentResult[][] docs = ros.getDocResArray();						%>
			<%			numeroPagina = numeroPagina > docs.length ? 0 : numeroPagina;		%>			
			<%					for (int i = 0; i < docs[numeroPagina].length ; i++) {		%>
			<%						if (docs[numeroPagina][i] != null) {					%>
										<div id="result">
											<div id="result-title">
												<a href="<%=docs[numeroPagina][i].getRelativePath()%>"><%=docs[numeroPagina][i].getTitle()%></a>
											</div>
											<div id="result-highlights">
												<%=docs[numeroPagina][i].getHighlights()%>
											</div>
										</div>
			<%						}																													%>
			<%					}																														%>
		<!--<%					if (numeroPagina != 0) { 																								%>
			<%						String prev = "search?query="+URLEncoder.encode(queryString)+"&forced="+ros.isForced()+"&page="+(numeroPagina-1);	%>
									<a href=<%=prev%>>Previous</a>
			<%					}	 																													%>
								<p><%=numeroPagina + 1%></p>
			<%					if (numeroPagina < docs.length-1) {																						%>
			<%						String next = "search?query="+URLEncoder.encode(queryString)+"&forced="+ros.isForced()+"&page="+(numeroPagina+1);	%>
									<a href=<%=next%>>Next</a>
			<%					}																														%>-->
								<img class="footer-paging" src="media/footer_paging_first_part.png">
			<%					for (int i = 0; i<docs.length; i++) {																					%>
			<%						String ref = "search?query="+URLEncoder.encode(queryString)+"&forced="+ros.isForced()+"&page="+i;					%>
									<a href=<%=ref%>>
			<%						if (i == numeroPagina) {																							%>				
										<img class="footer-paging" src="media/footer_paging_curr_page.png">
			<%						} else {																											%>
										<img class="footer-paging" src="media/footer_paging_other_pages.png">
			<%						}																													%>										</a>
			<%					}																														%>
								<img class="footer-paging" src="media/footer_paging_last_part.png">
			<%		}																																	%>
				</div>
		</div>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
</body>
</html>