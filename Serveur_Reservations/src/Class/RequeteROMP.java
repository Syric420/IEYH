package Class;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Database.facility.BeanBD;
import Interfaces.ConsoleServeur;
import Interfaces.Requete;
import Message.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RequeteROMP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;

    private int type;
    private Message message;

    public int getType()
    {
        return type;
    }
    
    
    public RequeteROMP(int type)
    {
        this.type = type;
    }
    
    public RequeteROMP(int type, Message m)
    {
        this.type = type;
        this.message = m;
    }
    
    @Override
    public Runnable createRunnable(Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD)
    {
        return new Runnable()
        {
            private ReponseROMP rep;
            private RequeteROMP req = new RequeteROMP(type, message);
           
            
            private BeanBD BD = beanBD;
            private ResultSet rs;
            private ResultSetMetaData rsmd;
            
            private String state = "NON_AUTHENTICATED";
            
            @Override
            public void run() 
            {
                boolean disconnected = false;
                while(!disconnected)
                {
                    switch(req.getType())
                    {
                        case REQUEST_LOGIN:
                            treatLogin();
                            break;
                        case REQUEST_LOGOUT:
                            treatLogout();
                            break;
                    }
                    try 
                    {
                        if(rep!= null)
                            oos.writeObject(rep);
                        rep= null;
                        req = (RequeteROMP) ois.readObject();
                    } 
                    catch (ClassNotFoundException ex) 
                    {
                        Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                        disconnected = true;
                    } 
                    catch (IOException ex) 
                    {
                        System.out.println("Client socket closed");
                        disconnected = true;
                    }
                }
                
            }
            
            private void treatLogin() 
            {
                MessageLogin m = (MessageLogin)req.message;
                PreparedStatement pst = null;
                ReponseROMP rep;
                
                if(state == "AUTHENTICATED")
                {
                    rep = new ReponseROMP(ReponseROMP.FAILED, "Déjà authentifié");
                    return;
                }
                else
                {
                    try {
                        //Pas encore authentifié
                        pst = BD.getCon().prepareStatement("select password from login where user = ?");
                        
                        pst.setString(1, m.getUsername());
                        
                        System.out.println("Requete SQL = "+pst.toString());
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            String mdp =  rs.getString("password");
                            System.out.println("mdp SQL recu = "+mdp);
                            System.out.println("mdp requeteROMP = "+m.getPassword());
                            if(mdp.equals(m.getPassword()))
                            {
                                //Les deux mots de passe sont identiques
                                rep = new ReponseROMP(ReponseROMP.SUCCESS, "Login réussi");
                                state = "AUTHENTICATED";
                            }
                            else
                            {
                                rep = new ReponseROMP(ReponseROMP.FAILED, "Mauvais mot de passe");
                            }
                        }
                        else
                        {
                            rep = new ReponseROMP(ReponseROMP.FAILED, "L'utilisateur n'existe pas");
                        }
                        oos.writeObject(rep);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                        
                /*try 
                {

                    rs = BD.executeQuery("select password from login where username = '" + m.getUsername() + "'");
                    if(rs.next())
                    {
                        String pass = rs.getString("password");
                        if(comparaison)
                        {...}
                        else
                        {...}
                    }
                    rs.close();
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(ReponseFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
            
            private void treatLogout() 
            {
                /*if(state == "NON_AUTHENTICATED") 
                {
                    rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED,new MessageSimple("Vous n'êtes pas authentifié"));
                    cs.TraceEvenements("serveur#client non authentifié!#" + this.getClass());
                    return;
                }
                state = "NON_AUTHENTICATED";
                rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Déconnexion réussie!"));
                cs.TraceEvenements("serveur#client déconnecté#" + this.getClass());*/
            }
            
            
        };
    }
}
