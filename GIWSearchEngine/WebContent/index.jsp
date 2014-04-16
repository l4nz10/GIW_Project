<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Benvenuto nel motore di ricerca</title>
</head>
<body>
	<center>
		<div style="margin: 0 auto; width: 75%;">
			<h1>Benvenuto!</h1>
			<h2>Inserisci cosa vuoi cercare:</h2>
			<form action="search.do" method="post">
				<p>
					<input type="text" name="query" />
				<p>
					<input type="submit" value="Cerca" name="Cerca" /> <input
						type="reset" value="Reimposta" />
			</form>
		</div>
	</center>
</body>
</html>