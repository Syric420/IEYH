/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.facility;
import Utilities.ReadProperties;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Vince
 */
public class BeanBD {
    private String typeBD;
    private Connection con;
    private Statement instruc;
    private ResultSet rs;
    public BeanBD()
    {
        typeBD = "";
    }
    /**
     * @return the typeBD
     */
    public String getTypeBD() {
        return typeBD.toUpperCase();
    }

    /**
     * @param typeBD the typeBD to set
     */
    public void setTypeBD(String typeBD) {
        if(typeBD.equalsIgnoreCase("oracle") || typeBD.equalsIgnoreCase("mysql"))
            this.typeBD = typeBD;
        else
            javax.swing.JOptionPane.showMessageDialog(null, "Erreur - deux choix possible : \"Oracle\" ou \"MySQL\"");
    }
   
    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the instruc
     */
    public Statement getInstruc() {
        return instruc;
    }

    /**
     * @param instruc the instruc to set
     */
    public void setInstruc(Statement instruc) {
        this.instruc = instruc;
    }
    
    public int connect()
    {
        Class leDriver;
        if(getTypeBD().equals(""))
            return -1;
        else
        {
            System.out.println("Essai de connexion JDBC");
            ReadProperties rP ;
            try {
                rP = new ReadProperties("/database/utilities/Config.properties");
                String s;
                s = new String(getTypeBD()+"_DRIVER");
                leDriver = Class.forName(rP.getProp(s));
                String address,user,pwd;
                
                s = new String(getTypeBD()+"_ADDRESS");
                address = rP.getProp(s);
                
                s = new String(getTypeBD()+"_USER");
                user = rP.getProp(s);
                
                s = new String(getTypeBD()+"_PASSWORD");
                pwd = rP.getProp(s);
                setCon(DriverManager.getConnection(address,user,pwd));
                setInstruc(getCon().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
            }
            catch (ClassNotFoundException e)
            { 
                System.out.println("Driver adéquat non trouvable : " + e.getMessage()); 
            } catch (IOException ex) {
                Logger.getLogger(BeanBD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(BeanBD.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 0;
        }
    }

    /**
     * @return the rs
     */
    public ResultSet getRs() {
        return rs;
    }

    /**
     * @param rs the rs to set
     */
    public void setRs(ResultSet rs) {
        this.rs = rs;
    }
    
    
}
