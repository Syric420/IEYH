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
import Utilities.BouncyClass;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Date;

public class RequeteSPAYMAP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;
    public static final int REQUEST_LISTROOM = 2;
    public static final int REQUEST_BROOM = 3;
    public static final int REQUEST_LISTRESERV = 4;
    public static final int REQUEST_CROOM = 5;
    public static final int REQUEST_PROOM = 6;

    private int type;
    private Message message;

    public int getType()
    {
        return type;
    }
    
    
    public RequeteSPAYMAP(int type)
    {
        this.type = type;
    }
    
    public RequeteSPAYMAP(int type, Message m)
    {
        this.type = type;
        this.message = m;
    }
    
    @Override
    public Runnable createRunnable(Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD)
    {
        return new Runnable()
        {
            private ReponseSPAYMAP rep;
            private RequeteSPAYMAP req = new RequeteSPAYMAP(type, message);
            
            private PrivateKey cléPrivée;
            private PublicKey cléPublique;
            
            private BeanBD BD = beanBD;
            private ResultSet rs;
            
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
                            //System.out.println("Req = REQUEST_LOGIN");
                            treatLogin();
                            break;
                        case REQUEST_LOGOUT:
                            //System.out.println("Req = REQUEST_LOGOUT");
                            treatLogout();
                            break;
                        case REQUEST_LISTRESERV:
                            //System.out.println("Req = REQUEST_LISTRESERV");
                            treatListReserv();
                            break;
                    }
                    try 
                    {
                        rep= null;
                        req = (RequeteSPAYMAP) ois.readObject();
                    } 
                    catch (ClassNotFoundException ex) 
                    {
                        Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
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
                    MessageLoginDigest m = (MessageLoginDigest)req.message;
                    PreparedStatement pst = null;
                    ReponseSPAYMAP rep=null;
                    
                    if(state.equals("AUTHENTICATED"))
                    {
                        rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "Déjà authentifié");
                        //return;
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
                                
                                //Création du digest sur le mot de passe récupéré
                                byte [] digest = BouncyClass.prepareSaltDigest(mdp, m.getTemps(), m.getAlea());
                                
                                if(BouncyClass.isSaltDigestEqual(digest, m.getDigest()))
                                {
                                    //Les deux digests sont identiques
                                    rep = new ReponseSPAYMAP(ReponseSPAYMAP.SUCCESS, "Login réussi");
                                    state = "AUTHENTICATED";
                                    System.out.println("Les deux digests sont identique + début du handshake");
                                    handshake();
                                }
                                else
                                {
                                    rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "Mauvais mot de passe");
                                    System.out.println("Erreur - les mots de passe ne correspondent pas");
                                }
                            }
                            else
                            {
                                rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "L'utilisateur n'existe pas");
                                System.out.println("Erreur - L'utilisateur n'existe pas");
                            }
                            
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    
                        try {
                            if(rep!=null)
                                oos.writeObject(rep);

                        } catch (IOException ex) {
                            Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }
            
            private void handshake()
            {
                try {
                    //Lecture du keystore
                    KeyStore ks = null;
                    ks = KeyStore.getInstance("JCEKS");
                    ks.load(new FileInputStream("C:\\Users\\vhoog\\Documents\\Projets ecole\\IEYH\\Serveur_Paiements\\ServeurPaiement.JCEKS"),
                            "123".toCharArray());
                    cléPrivée = (PrivateKey) ks.getKey("paiementrsa", "123".toCharArray());
                    System.out.println("Cle privee recuperee : " + cléPrivée.toString());
                    
                    
                    X509Certificate certif = (X509Certificate)ks.getCertificate("paiementrsa");
                    cléPublique = certif.getPublicKey();
                    System.out.println("Cle publique recuperee = "+cléPublique.toString());
                    //-------------------
                    
                    //Envoie de la clé publique au client
                    oos.writeObject(cléPublique);
                    
                    //récupération de la clé publique du client
                    PublicKey cléPubliqueClient = (PublicKey)ois.readObject();
                    
                    
                    
                    /*Il y a deux clés symétriques à envoyer, une pour chiffrer les données et l'autre pour fabriquer un HMAC
                    /énoncé : Enfin, la clé symétrique (clé secrète) utilisée par un employé pour s'authentifier 
                    (HMAC) n'est pas la même que celle qui lui permet de chiffre/déchiffrer : tout employé
                    possède donc deux clés symétriques à usage différent.*/
                    
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException | CertificateException | ClassNotFoundException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);}
                    
            }
            private void treatLogout() 
            {
                try {
                    if(state.equals("NON_AUTHENTICATED"))
                    {
                        rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED,"Vous n'êtes pas authentifié");
                        cs.TraceEvenements("serveur#client non authentifié!#" + this.getClass());
                        //return;
                    }
                    else
                    {
                        state = "NON_AUTHENTICATED";
                        rep = new ReponseSPAYMAP(ReponseSPAYMAP.SUCCESS, "Déconnexion réussie!");
                        cs.TraceEvenements("serveur#client déconnecté#" + this.getClass());
                    }
                    
                    
                    oos.writeObject(rep);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void treatListReserv() {
                 try {
                    //Il faut juste lister les différentes reservations possibles
                    
                    //Récupération des différentes reservations dans la BD
                    ResultSet rs = BD.executeQuery("Select * from reservation");
                    //System.out.println("ListReserv");
                     
                    MessageListVector mlv = new MessageListVector();
                    //Ca sera une linkedlist de vector pour avoir plus facile à la mettre dans la jtable
                    while(rs.next())
                    {
                        Vector vecRoom = new Vector();
                        int id = rs.getInt(1);
                        
                        String typeReserv = rs.getString(2);
                        
                        Date dateDebut = rs.getDate(3);
                        Date dateFin = rs.getDate(4);
                        
                        float prixNet = rs.getFloat(5);
                        boolean paye = rs.getBoolean(6);
                        
                        vecRoom.add(id);
                        vecRoom.add(typeReserv);
                        vecRoom.add(dateDebut);
                        vecRoom.add(dateFin);
                        vecRoom.add(prixNet);
                        vecRoom.add(paye);
                        
                        mlv.getListVector().add(vecRoom);
                    }
                    
                    oos.writeObject(mlv);
                   
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        };
    }
}
