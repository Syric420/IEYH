<%-- 
    Document   : JspLogin
    Created on : 07-nov.-2018, 16:31:59
    Author     : Vince
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        
        <h1>Veuillez entrer votre login</h1>
        <% String msg= (String)request.getAttribute("msgErreur");
        if (msg!=null) out.println("<H2>" + msg + "</H2><p>");
        %>
        <form method="POST" action="MainServlet">
        <p>Nom d'utilisateur : <input type="text" name="user" value="" required /></p>
        
        <p>Mot de passe <input type="password" name="password" value="" required/></p>
        
        <p>Nouvel utilisateur : <input type="checkbox" name="newUser" value="ON" /></p>
        <input type="submit" value="Se connecter"/>
        <input type="hidden" name="action" value="login">
        </form>
    </body>
</html>
