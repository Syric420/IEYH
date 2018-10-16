package Requete;

import Reponse.ReponseFUCAMP;
import java.io.*;
import java.util.*;
import java.net.*;
import Message.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Database.facility.BeanBD;
import Interfaces.ConsoleServeur;
import Interfaces.Requete;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RequeteFUCAMP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;
    public static final int REQUEST_ACT_1DAY = 2;    
    public static final int REQUEST_ACT_NDAY = 3;
    public static final int REQUEST_LIST_ACT = 4;
    public static final int REQUEST_DESIST = 5;

    private int type;
    private Message message;

    public int getType()
    {
        return type;
    }
    
    public Message getMessage() 
    {
        return message;
    }
    
    public RequeteFUCAMP(int type, Message message)
    {
        this.type = type;
        this.message = message;
    }
    
    public RequeteFUCAMP(int type)
    {
        this.type = type;
    }
    
    @Override
    public Runnable createRunnable(Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD)
    {
        return new Runnable()
        {
            private ReponseFUCAMP rep;
            private RequeteFUCAMP req = new RequeteFUCAMP(type, message);
            
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
                        case REQUEST_ACT_1DAY:
                            treatAct1Day();
                            break;
                        case REQUEST_ACT_NDAY:
                            treatActNDay();
                            break;
                        case REQUEST_LIST_ACT:
                            getActivities();
                            break;
                        case REQUEST_DESIST:
                            treatActDesistement();
                            break;
                    }
                    try 
                    {
                        if(rep!= null)
                            oos.writeObject(rep);
                        rep= null;
                        req = (RequeteFUCAMP) ois.readObject();
                    } 
                    catch (ClassNotFoundException ex) 
                    {
                        Logger.getLogger(RequeteFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
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
                
                if(state == "AUTHENTICATED")
                {
                    rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Déjà authentifié"));
                    return;
                }
                else
                {
                        
                    if(m.getUsername().equals("mika") && m.getPassword().equals("1234"))
                    {
                        cs.TraceEvenements("serveur#" + m.getUsername() + " login ok#" + this.getClass());
                        state = "AUTHENTICATED";
                        rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Connexion réussie avec succès"));
                    }
                    else
                    {
                        cs.TraceEvenements("serveur#" + m.getUsername() + " login not ok#" + this.getClass());
                        rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Mot de passe incorrect"));
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
                if(state == "NON_AUTHENTICATED") 
                {
                    rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED,new MessageSimple("Vous n'êtes pas authentifié"));
                    cs.TraceEvenements("serveur#client non authentifié!#" + this.getClass());
                    return;
                }
                state = "NON_AUTHENTICATED";
                rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Déconnexion réussie!"));
                cs.TraceEvenements("serveur#client déconnecté#" + this.getClass());
            }
            
            private void treatAct1Day() 
            {
                
            }
            
            private void treatActNDay() 
            {
                
            }
            
            private void getActivities() 
            {
                try
                {
                    cs.TraceEvenements("serveur#récup des activités#" + this.getClass());
                    rs = BD.executeQuery("SELECT * FROM Activités");
                    rsmd = rs.getMetaData();
                    LinkedList<Vector> listActs = new LinkedList<Vector>();
                            
                    while(rs.next())
                    {
                        Vector act = new Vector();
                        for(int i=1; i<=rsmd.getColumnCount();i++)
                        {
                            switch(rsmd.getColumnClassName(i))
                            {
                                case "java.lang.Integer":
                                    //System.out.println("Integer "+rsmd.getColumnClassName(i));
                                    int j =  rs.getInt(i);
                                    act.add(j);
                                    break;
                                    
                                case "java.lang.String":
                                    //System.out.println("String "+rsmd.getColumnClassName(i));
                                    String s =  rs.getString(i);
                                    act.add(s);
                                    break;
                                    
                                case "java.lang.Long":
                                    //System.out.println("String "+rsmd.getColumnClassName(i));
                                    long l =  rs.getLong(i);
                                    act.add(l);
                                    break;
                                    
                                case "java.lang.Float":
                                    //System.out.println("String "+rsmd.getColumnClassName(i));
                                    float fl =  rs.getFloat(i);
                                    act.add(fl);
                                    break;
                                    
                                case "java.lang.Boolean":
                                    //System.out.println("String "+rsmd.getColumnClassName(i));
                                    boolean bool =  rs.getBoolean(i);
                                    act.add(bool);
                                    break;
                                    
                                case "java.sql.Date":
                                    Date date = rs.getDate(i);
                                    //System.out.println(date);
                                    act.add(date);
                                    break;
                                    
                                default:
                                    act.add("NULL");
                                    break;
                            }
                        }
                        listActs.add(act);
                    }
                    rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageListActs(listActs));
                    rs.close();
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(RequeteFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void treatActDesistement() 
            {
                
            }
        };
    }
}
