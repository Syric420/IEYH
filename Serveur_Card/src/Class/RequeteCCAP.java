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
import javax.crypto.SecretKey;

public class RequeteCCAP implements Requete, Serializable
{
    public static final int REQUEST_VERIF = 0;

    private int type;
    private Message message;

    public int getType()
    {
        return type;
    }
    
    
    public RequeteCCAP(int type)
    {
        this.type = type;
    }
    
    public RequeteCCAP(int type, Message m)
    {
        this.type = type;
        this.message = m;
    }
    
    @Override
    public Runnable createRunnable(Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD, Socket socketCard)
    {
        return new Runnable()
        {
            private ReponseCCAP rep;
            private RequeteCCAP req = new RequeteCCAP(type, message);
            
            private BeanBD BD = beanBD;
            private ResultSet rs;
            
            
            @Override
            public void run() 
            {
                boolean disconnected = false;
                while(!disconnected)
                {
                    switch(req.getType())
                    {
                        case REQUEST_VERIF:
                            //System.out.println("Req = REQUEST_LOGIN");
                            treatVerif();
                            break;
                    }
                    try 
                    {
                        rep= null;
                        req = (RequeteCCAP) ois.readObject();
                    } 
                    catch (ClassNotFoundException ex) 
                    {
                        Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                        disconnected = true;
                    } 
                    catch (IOException ex) 
                    {
                        System.out.println("Client socket closed");
                        disconnected = true;
                    }
                }
                
            }
            
            private void treatVerif() 
            {
                    MessageVerifCard m = (MessageVerifCard)req.message;
                    
                    /*PreparedStatement pst = null;
                    ReponseCCAP rep=null;
                    
                    if(state.equals("AUTHENTICATED"))
                    {
                        rep = new ReponseCCAP(ReponseCCAP.FAILED, "Déjà authentifié");
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
                                    rep = new ReponseCCAP(ReponseCCAP.SUCCESS, "Login réussi");
                                    state = "AUTHENTICATED";
                                    System.out.println("Les deux digests sont identique + début du handshake");
                                    
                                }
                                else
                                {
                                    rep = new ReponseCCAP(ReponseCCAP.FAILED, "Mauvais mot de passe");
                                    System.out.println("Erreur - les mots de passe ne correspondent pas");
                                }
                            }
                            else
                            {
                                rep = new ReponseCCAP(ReponseCCAP.FAILED, "L'utilisateur n'existe pas");
                                System.out.println("Erreur - L'utilisateur n'existe pas");
                            }
                            
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    
                        try {
                            if(rep != null && rep.getCode()== ReponseCCAP.SUCCESS)
                            {
                                oos.writeObject(rep);
                                //handshake();
                            }
                                

                        } catch (IOException ex) {
                            Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }*/
            }
            
            /*private void handshake()
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
                    System.out.println("Cle publique du certificat de ServeurPaiement.JCEKS recuperée = "+cléPublique.toString());
                    //-------------------
                    
                    //récupération de la clé publique du client
                    cléPubliqueClient = (PublicKey)ois.readObject();
                    System.out.println("Cle publique du client recuperée = "+cléPubliqueClient.toString());
                     
                    
                    //Une fois les deux clés publiques échangées il va falloir envoyer les deux clés symétriques au client en les cryptant avec RSA
                    //Récupération des clés dans le keystore
                    cléSym = (SecretKey) ks.getKey("paiementsym", "123".toCharArray());
                    cléSymHMAC = (SecretKey) ks.getKey("paiementsymhmac", "123".toCharArray());
                    //On les transforme en byte 
                    byte [] cléSymByte = cléSym.getEncoded();
                    byte [] cléSymHMACByte = cléSymHMAC.getEncoded();
                    
                    //On les crypte avec la clé publique du client
                    byte [] cléSymByteCrypted = BouncyClass.encryptRSA(cléPubliqueClient, cléSymByte);
                    byte [] cléSymHMACByteCrypted = BouncyClass.encryptRSA(cléPubliqueClient, cléSymHMACByte);
                    
                    //On les envoie au client
                    oos.writeObject(cléSymByteCrypted);oos.writeObject(cléSymHMACByteCrypted);
                    System.out.println("Envoie des deux clés sym cryptée avec RSA");
                    
                    /*Il y a deux clés symétriques à envoyer, une pour chiffrer les données et l'autre pour fabriquer un HMAC
                    /énoncé : Enfin, la clé symétrique (clé secrète) utilisée par un employé pour s'authentifier 
                    (HMAC) n'est pas la même que celle qui lui permet de chiffre/déchiffrer : tout employé
                    possède donc deux clés symétriques à usage différent.
                    
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException | CertificateException | ClassNotFoundException ex) {
                    Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);}
                    
            }*/
            
        };
    }
}
