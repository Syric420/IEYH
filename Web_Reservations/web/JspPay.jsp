<%-- 
    Document   : JspPay
    Created on : 19-nov.-2018, 18:47:37
    Author     : Vince
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Paiement</title>
    </head>
    <body>
        <h1>IEYH</h1>
        <p> La somme total à payer est <%=session.getAttribute("totalAPayer") %> €</p>
        <form name="btnPayer" action="MainServlet">
            <p>Numéro de carte : <input type="text" name="inputCarteCredit" value="" required=""/></p>
            <p>Cryptogramme : <input type="text" name="inputCryptogramme" value="" required=""/></p>
            <p>Montant : <input type="text" name="inputMontant" value="" required=""/></p>
            <p>Vous voulez payer ? <input type="submit" value="Payer" /></p>
            <input type="hidden" name="action" value="payPayer">
        </form>
        <form name="btnCommander" action="MainServlet">
            <p>Vous voulez annuler ? <input type="submit" value="Annuler" /></p>
            <input type="hidden" name="action" value="payAnnuler">
        </form>
    </body>
</html>
