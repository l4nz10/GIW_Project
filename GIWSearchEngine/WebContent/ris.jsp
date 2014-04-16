<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="org.apache.lucene.document.Document, java.util.*"%>
<%
	ArrayList<Document> docs = (ArrayList<Document>) session.getAttribute("docs");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Risultati</title>
</head>
<body>
	<h1>Trovati:</h1>
	<ol>
	<%for(Document d: docs){ %>
	<li><%= d.get("path")%>
	<%} %>
	</ol>
</body>
</html>