<%-- 
    Document   : JspInit
    Created on : 14-nov.-2018, 17:53:07
    Author     : Vince
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core">
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Initialisation du caddie</title>
    </head>
    <body>
        <h1>Bienvenue sur le site IEYH</h1>
        
        <table border="1" width="1">
            <thead>
                <tr>
                    <th>Test</th>
                    <th>Test</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Test2</td>
                    <td>Test2</td>
                </tr>
                <tr>
                    <td>Test3</td>
                    <td>Test3</td>
                </tr>
            </tbody>
        </table>
        <form name="btnCommander" action="MainServlet">
            <input type="submit" value="Commander chambre" />
            <input type="hidden" name="action" value="init">
        </form>
        <form name="btnPayer" action="MainServlet">
            <input type="submit" value="Payer" />
            <input type="hidden" name="action" value="initpayer">
        </form>
            
        
        
        
    </body>
</html>
