package Thread;

import Database.facility.BeanBD;
import HOLICOP.*;
import Interfaces.ConsoleServeur;
import Message.*;
import Requete.RequeteHOLICOP;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class ThreadAccept extends Thread
{
    private int port_talk;
    private int port_chat;
    private String addresse_chat;
    private ServerSocket SSocket;
    private Socket CSocket;
    private ConsoleServeur cs;
    private JLabel label;
    private BeanBD BD; 

    public ThreadAccept(ConsoleServeur cs, JLabel label, BeanBD bd)
    {
        FileInputStream fis = null;
        try
        {
            Properties prop = new Properties();
            fis = new FileInputStream("src\\GUI\\config.properties");

            prop.load(fis);
            
            String s;
            s = "PORT_TALK";
            System.out.println("PORT_TALK: " + prop.getProperty(s));
            port_talk = Integer.parseInt(prop.getProperty(s));
            
            s = "PORT_CHAT";
            System.out.println("PORT_CHAT: " + prop.getProperty(s));
            port_chat = Integer.parseInt(prop.getProperty(s));
            
            s = "ADRESSE_CHAT";
            System.out.println("ADRESSE_CHAT: " + prop.getProperty(s));
            addresse_chat = prop.getProperty(s);
            
            this.cs = cs;
            this.label = label;
            this.BD = bd;
        }
        
        catch(IOException | NumberFormatException ex)
        {
            System.out.println("Serveur_Activites: Exception: " + ex.getMessage());
        }
    }

    @Override
    public void run() 
    {
        cs.TraceEvenements("serveur#Démarrage de le connexion#" + this.getClass());
        SSocket = null; CSocket = null;
        try
        {
            SSocket = new ServerSocket(port_talk);
            label.setText(String.valueOf(port_talk));
        }
        catch (IOException e)
        {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]");
            System.exit(1);
        }
        
        while(!isInterrupted())
        {
            cs.TraceEvenements("serveur#Serveur en attente#" + this.getClass());
            try
            {
                CSocket = SSocket.accept(); //attente de la connexion d'un client
                cs.TraceEvenements("Nouveau client détecté#"  + CSocket.getLocalSocketAddress() + "#" + this.getClass());
                
                boolean logged = false;
                ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                
                while(!logged)
                {
                    MessageLogin message = (MessageLogin)ois.readObject(); //on lit l'objet ayant été déposé sur le flux par l'appli client
                    if(message.getTypeMessage() == 1) //si voyageur
                    {
                        //on prépare une requête pour vérifier si la réservation du voyageur existe
                        RequeteHOLICOP req = new RequeteHOLICOP(RequeteHOLICOP.CHECK_RESERVATION, new MessageLogin(message.getUsername()), BD);
                        boolean reservFlag = (boolean)req.executeQueryHOLICOP();
                        
                        if(reservFlag) //si elle existe
                        {
                            logged=true;
                            message.setUsername("RESERVATION OK");
                            message.setAddresse_chat(addresse_chat);
                            message.setPort_chat(port_chat);
                            oos.writeObject(message);
                            break;
                        }
                        else
                        {
                            message.setUsername("RESERVATION NOT OK");
                            oos.writeObject(message);
                            logged=false;
                        }
                    }
                    
                    else //si employé
                    {
                        //on prépare une requête pour vérifier si le user est existant
                        RequeteHOLICOP req = new RequeteHOLICOP(RequeteHOLICOP.CHECK_LOGIN, new MessageLogin(message.getUsername()), BD);
                        String mdp = (String)req.executeQueryHOLICOP();
                        
                        if(!mdp.equals("LOGIN ERROR")) //si user existe
                        {
                            //on prépare un digest avec le username et le vrai mdp qu'on a été cherché dans la bd
                            Identify log = new Identify();
                            log.setMd(message.getUsername(), mdp, message.getMsgD().getTemps(), message.getMsgD().getAlea());
                            if(MessageDigest.isEqual(log.getMd(), message.getMsgD().getMd())) //on compare les digests
                            {
                                message.setUsername("PASSWORD OK");
                                message.setPort_chat(port_chat);
                                message.setAddresse_chat(addresse_chat);
                                logged=true;
                                oos.writeObject(message);
                                break;
                            }
                            else
                            {
                                message.setUsername("PASSWORD NOT OK");
                                oos.writeObject(message);
                                logged=false;
                            }
                        }
                        else
                        {
                            message.setUsername("USER NOT OK");
                            oos.writeObject(message);
                            logged=false;
                        }
                    }
                }
            }
            catch (SocketException e)
            {
                System.err.println("Accept interrompu ! ? [" + e + "]");
            }
            catch (IOException e)
            {
                System.err.println("Erreur d'accept ! ? [" + e + "]");
                System.exit(1);
            } 
            catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(ThreadAccept.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void finConnexion()
    {
        try 
        {
            SSocket.close();
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ThreadAccept.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
