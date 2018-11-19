<%-- 
    Document   : JspInit
    Created on : 14-nov.-2018, 17:53:07
    Author     : Vince
--%>

<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="Database.facility.BeanBD"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core">
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Initialisation du caddie</title>
    </head>
    <% //Ici on va récupérer dans la BD la liste des chambres
        PreparedStatement pst = null;
        ResultSet rs;
        BeanBD BD = new BeanBD();
        int rowCount;
        
    BD.setTypeBD("mysql");
    BD.connect();
    
    pst = BD.getCon().prepareStatement("Select idReservation, typeReservation, dateDebut, dateFin, prixNet from reservation where idReferent = ? AND boolPaye = 0");
    session = request.getSession(true);
    pst.setInt(1, (int)session.getAttribute("identifiant"));
    rs = pst.executeQuery();
    ResultSetMetaData rsmd = rs.getMetaData();
    rowCount = rsmd.getColumnCount();
    %>
    <body>
        <h1>Bienvenue sur le site IEYH</h1>
       
        <table border="1" width="1">
            <thead>
                <tr>
                    <% for(int i=1; i<rowCount+1; i++)
                    {//On affiche les différentes colonnes 
                    %><th><%=rsmd.getColumnName(i)%></th>
                    <%}%>
                </tr>
            </thead>
            <tbody>
                <%
                    while(rs.next())//Tant qu'il y a des tuples on les affiche
                    {%>
                    <tr>
                            <td> <%=rs.getInt(1)%></td>
                            <td> <%=rs.getString(2)%></td>
                            <td> <%=rs.getDate(3)%></td>
                            <td> <%=rs.getDate(4)%></td>
                            <td> <%=rs.getFloat(5)%></td>
                        </tr>
                    <%}%> 
            </tbody>
        </table>
            <p> Voulez-vous annuler une réservation ? </p>
            <form name="btnAnnuler" action="MainServlet">
                <p> Numéro d'identifiant de la réservation à annuler :<input type="text" name="inputNumReservation" value="0" /> <input type="submit" value="annuler" /></p>
                <input type="hidden" name="action" value="initAnnuler">
            </form>
        <form name="btnCommander" action="MainServlet">
            <p>Vous voulez commander ? <input type="submit" value="Commander chambre" /></p>
            <input type="hidden" name="action" value="init">
        </form>
        <form name="btnPayer" action="MainServlet">
            <p> Vous voulez payer ? <input type="submit" value="Payer" /></p>
            <input type="hidden" name="action" value="initpayer">
        </form>
    </body>
</html>
