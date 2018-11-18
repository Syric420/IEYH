<%-- 
    Document   : JspCaddie
    Created on : 15-nov.-2018, 17:22:23
    Author     : Vince
--%>

<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="Database.facility.BeanBD"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Affichage des différentes chambres</title>
    </head>
    <% //Ici on va récupérer dans la BD la liste des chambres
        PreparedStatement pst = null;
        ResultSet rs;
        BeanBD BD = new BeanBD();
        int rowCount;
        
    BD.setTypeBD("mysql");
    BD.connect();
    
    pst = BD.getCon().prepareStatement("Select * from chambre");
    rs = pst.executeQuery();
    ResultSetMetaData rsmd = rs.getMetaData();
    rowCount = rsmd.getColumnCount();
    %>
                         
    <body>
        <% String msg= (String)request.getAttribute("msgErreur");
        if (msg!=null) out.println("<H2>" + msg + "</H2><p>");
        %>
        <table border="1">
            <thead>
                <tr>
                    
                    <% for(int i=1; i<rowCount+1; i++)
                    {//On affiche les différentes colonnes 
                    %><th><%=rsmd.getColumnName(i)%></th>
                    <%}%>
                </tr>
            </thead>
            <tbody>
                
                    <% while(rs.next())//Tant qu'il y a des tuples on les affiche
                    {
                      %><tr>
                            <td> <%=rs.getInt(1)%></td>
                            <td> <%=rs.getString(2)%></td>
                            <td> <%=rs.getString(3)%></td>
                            <td> <%=rs.getFloat(4)%></td>
                        </tr>
                    <%}%> 
                
            </tbody>
        </table>

                    <p> Quelle chambre voulez-vous commander ? </p>
                    <form method="POST" action="MainServlet">
                        
                        <p> Numéro de chambre : <select name="numChambre">
                            <% for(int i=1; i<rowCount; i++)
                            {//On ajoute les différentes options donc ici une liste de nombre correspondants au numéro de chambre
                            %><option><%=i%></option>
                            <%}%>
                        </select> </p>
                        <p> Date de début : <input type="text" name="dateDebut" value="" /> exemple : 01/02/1978</p> 
                        <p> Date de fin : <input type="text" name="dateFin" value="" /> exemple : 01/02/1978</p>  
                        <input type="submit" value="Réserver" name="btnReserver" />
                        <input type="hidden" name="action" value="reserverChambre">
                    </form>
                    
    </body>
</html>
