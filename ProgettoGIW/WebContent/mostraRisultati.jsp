<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Progetto GIW</title>
</head>
<body>
<div id="header">
	NOMEGRUPPO GIW
    	
    </div>
    <div id="menu">
    <%@ page import="javax.servlet.*, java.util.*, resurce.*" %>
		<%!DocumentResult[][] docs = (DocumentResult[][])session.getAttribute("risultato");%>
		<%!int numeroPagina = (int)session.getAttribute("paginaCorrente");%>
		<ul>
		<%for(int i=0;i<docs[numeroPagina].length;i++){%>
			<li>				
				<a href="<%=docs[numeroPagina][i].getRelativePath()%>"><%=docs[numeroPagina][i].getTitle() %>
				</a>
				<p><%=docs[numeroPagina][i].getHighlights()%></p>
			</li>
		<%}%>
		</ul>
		<%if(numeroPagina!=0){%>
			<a href="/prev">Previous</a>
		<%}%>
		<p><%=numeroPagina %></p>
		<%if(numeroPagina<docs.length){%>
			<a href="/next">Next</a>
		<%}%>
                            
                        
    
    </div>
    
    <div id="footer">
    FOOTER DEL GRUPPO GIW
		</div>
</body>
</html>