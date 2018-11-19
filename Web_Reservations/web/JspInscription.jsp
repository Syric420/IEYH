<%-- 
    Document   : JspInscription
    Created on : 17-nov.-2018, 16:31:21
    Author     : Vince
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Inscription</title>
    </head>
    <body>
        <h1>Veuillez remplir tous les champs</h1>
        <form action="MainServlet">
            <p>Nom : <input type="text" name="Nom" value="" required="" /></p>
            <p>Prenom : <input type="text" name="Prenom" value="" required=""/></p>
            <p>Nationalité : <input type="text" name="Nationalite" value="" required=""/></p>
            <p>Date de naissance : <input type="text" name="Date" value="" required=""/> exemple : 01/02/1978</p>
            <p>Email : <input type="text" name="Email" value="" required=""/></p>
            <p>Adresse : <input type="text" name="Adresse" value="" required=""/></p>
            <p>Numéro : <input type="text" name="Numero" value="" required=""/></p>
            <p>Code postal : <input type="text" name="CodePostal" value="" required=""/></p>
            <p>Commune : <input type="text" name="Commune" value="" required=""/></p>
            <input type="hidden" name="action" value="inscription" />
            <input type="submit" value="S'inscrire" name="inscription" />
        </form>
    </body>
</html>
