package Requete;

import java.io.*;
import Message.*;
import Database.facility.BeanBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RequeteHOLICOP implements Serializable
{
    public static final int CHECK_RESERVATION = 1;
    public static final int CHECK_LOGIN = 2;

    private int type;
    private Message mes;
    private BeanBD bd;

    ResultSet rs;
    ResultSetMetaData rsmd;
    PreparedStatement pst;

    public int getType()
    {
        return type;
    }
    
    public Message getMessage() 
    {
        return mes;
    }
    
    public RequeteHOLICOP(int t, Message m, BeanBD BD)
    {
        type = t;
        mes = m;
        bd = BD;
    }
    
    public Object executeQueryHOLICOP()
    {
        Object ret = null;
        switch(type)
        {
            case CHECK_RESERVATION:
                ret = treatReservationCheck();
                break;
            case CHECK_LOGIN:
                ret = treatLoginCheck();
                break;
        }
        return ret;
    }

    private boolean treatReservationCheck() //va voir si la réservation existe ou non
    {
        MessageLogin m = (MessageLogin)mes;
        try 
        {
            pst = bd.getCon().prepareStatement("select idReferent from reservation where idReservation = ?");
            pst.setString(1, m.getUsername());

            System.out.println("Requete SQL = " + pst.toString());
            rs = pst.executeQuery();
            if(rs.first()) //trouve réservation
                return true;
            else //pas trouvé
                return false;
        } 
        catch (SQLException ex) 
        {
            System.err.println("RequeteHOLICOP - Erreur " + ex.getSQLState());
        }
        return false;
    }

    private String treatLoginCheck() //va aller chercher le mdp du user
    {
        MessageLogin m = (MessageLogin)mes;
        try 
        {
            pst = bd.getCon().prepareStatement("select password from login where user = ?");
            pst.setString(1, m.getUsername());

            System.out.println("Requete SQL = " + pst.toString());
            rs = pst.executeQuery();
            if(rs.first()) //user existant
            {
                String mdp =  rs.getString("password");
                System.out.println("mdp SQL recu = " + mdp);
                return mdp;
            }
            else //user non existant
                return "LOGIN ERROR";
        } 
        catch (SQLException ex) 
        {
            System.err.println("Erreur RequeteHOLICP: " + ex.getSQLState());
        }
        return "LOGIN ERROR";
    }
}
