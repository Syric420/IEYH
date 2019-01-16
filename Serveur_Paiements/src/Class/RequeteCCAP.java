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
            
            private PrivateKey cléPrivée;
            private PublicKey cléPublique;
            
            
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
                PreparedStatement pst = null;
                String carteDeCredit, crypto, montantString, stringIdReservation;
                int idReservation;
                
                float montant;
                try {
                    MessageCryptedWithSignature m = (MessageCryptedWithSignature)req.message;
                    
                    //Ce serveur a 7 paires de clefs asymétrique, une chaque jour donc il faut récupérer la clef du bon jour
                    
                    KeyStore ks = null;
                    ks = KeyStore.getInstance("JCEKS");
                    ks.load(new FileInputStream("..\\Serveur_Card\\ServeurCard.JCEKS"), "123".toCharArray());
                    
                    cléPrivée = (PrivateKey) ks.getKey("lundirsa", "123".toCharArray());
                    System.out.println("Cle privee recuperee : " + cléPrivée.toString());
                    
                    
                    byte [] messageByte = BouncyClass.decryptRSA(cléPrivée, m.getTexteCrypted());
                    
                    String message = new String(messageByte);
                    //idReservation + ";" + carteCredit + ";" + crypto + ";" + montant;  ==> message du type
                    
                    String [] tableauString  = message.split(";");
                    stringIdReservation = tableauString[0];
                    carteDeCredit = tableauString[1];
                    crypto = tableauString[2];
                    montantString = tableauString[3];
                    montant = Float.parseFloat(montantString);
                    
                    idReservation = Integer.parseInt(stringIdReservation);
                    
                    
                    //On vérifie si la carte existe
                    pst = BD.getCon().prepareStatement("select * from bd_card.carte where numero = ? AND crypto = ?");
                    pst.setString(1, carteDeCredit);
                    pst.setString(2, crypto);
                     System.out.println("Requete SQL = "+pst.toString());
                    rs = pst.executeQuery();
                    if(rs.first())
                    {
                        //Si la carte existe, on va alors récupérer l'id du compte puis vérifier si la personne a assez.
                        int idCompte = rs.getInt("idCompte");
                        
                        pst = BD.getCon().prepareStatement("select * from bd_card.compte where idCompte = ?");
                        pst.setInt(1, idCompte);
                        System.out.println(pst.toString());
                        
                        rep=null;
                        rs = pst.executeQuery();
                        //On vérifie si la personne a assez
                        if(rs.first())
                        {
                            float montantDispo = rs.getFloat("montantdisponible");
                        
                            if(montantDispo >= montant)
                            {
                                //Il a assez donc on retire sur son compte
                                pst = BD.getCon().prepareStatement("UPDATE bd_card.compte SET montantdisponible = ? WHERE idcompte = ?");
                                
                                pst.setFloat(1, (montantDispo-montant));
                                pst.setInt(2, idCompte);
                                System.out.println(pst.toString());
                                pst.executeUpdate();
                                rep = new ReponseCCAP(ReponseCCAP.SUCCESS, "Transaction effectuée");

                            }
                            else
                            {
                                rep = new ReponseCCAP(ReponseCCAP.FAILED, "Solde insuffisant");
                                //Pas assez
                            }
                        }
                        else
                        {
                            rep = new ReponseCCAP(ReponseCCAP.FAILED, "Compte introuvable");
                        }
                        
                    }
                    else
                    {
                        //la carte n'existe pas
                        rep = new ReponseCCAP(ReponseCCAP.FAILED, "Carte inexistante");
                    }
                    if(rep!=null)
                        oos.writeObject(rep);
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException | SQLException ex) {
                    Logger.getLogger(RequeteCCAP.class.getName()).log(Level.SEVERE, null, ex);
                }
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
