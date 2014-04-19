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
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
</head>
<body>
	<div id="header">NOMEGRUPPO GIW</div>
	<div id="menu">
		FORM DI ACQUISIZIONE PAROLE QUERY

		<form method="post" action="ProcessaQuery">
			<fieldset class="textbox">
				<label class="query"> <span>Cosa vuoi cercare</span> <input
					id="query" name="query" value="" type="text" autocomplete="on"
					placeholder="query">
				</label>
					<button class="btn btn-large" type="submit">Cerca</button>
				<!-- <button class="submit button" type="submit">Ricerca</button> -->
				<!-- <input type="submit" value="Invia" name="submit" id="submit" /> -->
				<p>
					<a class="help" href="help.jsp">Guida su come utilizzare il
						motore di ricerca</a>
				</p>

			</fieldset>
		</form>

	</div>

	<div id="footer">FOOTER DEL GRUPPO GIW</div>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="bootstrap/js/bootstrap.min.js"></script>
</body>
</html>