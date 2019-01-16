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

public class RequeteSPAYMAP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;
    public static final int REQUEST_LISTRESERV = 2;
    public static final int REQUEST_PAIEMENT = 3;

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
    public Runnable createRunnable(Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD, Socket socketCard)
    {
        return new Runnable()
        {
            private ReponseSPAYMAP rep;
            private RequeteSPAYMAP req = new RequeteSPAYMAP(type, message);
            
            private PrivateKey cléPrivée;
            private PublicKey cléPublique;
            private PublicKey cléPubliqueClient;
            private PublicKey cléPubliqueCard;
            
            private SecretKey cléSym;
            private SecretKey cléSymHMAC;
            
            private ObjectOutputStream oosCard = null;
            private ObjectInputStream oisCard = null;
            
            private BeanBD BD = beanBD;
            private ResultSet rs;
            
            private String state = "NON_AUTHENTICATED";
            private String userEmploye = null;
            
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
                            treatListReservNonPayees();
                            break;
                        case REQUEST_PAIEMENT:
                            treatDemandePaiement();
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
                        userEmploye=null;
                    } 
                    catch (IOException ex) 
                    {
                        System.out.println("Client socket closed");
                        userEmploye=null;
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
                                    
                                    //on place les flux sur le serveur card
                                    oosCard = new ObjectOutputStream(socketCard.getOutputStream());
                                    oisCard = new ObjectInputStream(socketCard.getInputStream());
                                    
                                    userEmploye = m.getUsername();
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
                            
                            
                        } catch (SQLException | IOException ex) {
                            Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                            System.exit(0);
                        }
                    
                        try {
                            if(rep != null && rep.getCode()== ReponseSPAYMAP.SUCCESS)
                            {
                                oos.writeObject(rep);
                                handshake();
                            }
                                

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
                    ks.load(new FileInputStream("..\\Serveur_Paiements\\ServeurPaiement.JCEKS"),
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
                    
                    //envoi de la clé publique du serveur au client
                    oos.writeObject(cléPublique);
                    System.out.println("Cle publique du serveur envoyé au client ");
                     
                    
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
                    
                    userEmploye=null;
                    oos.writeObject(rep);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void treatListReservNonPayees() {
                 try {
                    //Il faut juste lister les différentes reservations possibles
                    
                    //Récupération des différentes reservations dans la BD
                    ResultSet rs = BD.executeQuery("Select * from reservation WHERE boolpaye = '0'");
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
                        float prixPaye = rs.getFloat("prixPaye");
                        
                        vecRoom.add(id);
                        vecRoom.add(typeReserv);
                        vecRoom.add(dateDebut);
                        vecRoom.add(dateFin);
                        vecRoom.add(prixNet);
                        vecRoom.add(prixPaye);
                        
                        mlv.getListVector().add(vecRoom);
                    }
                    
                    oos.writeObject(mlv);
                   
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private void treatDemandePaiement() {
                MessageCryptedWithSignature m = (MessageCryptedWithSignature)req.message;
                PreparedStatement pst = null;
                
                String message = new String(BouncyClass.decryptDES(cléSym, m.getTexteCrypted()));//Texte déchiffré
                String montantString, stringIdReservation, messageTransaction = null;
                int idReservation;
                String [] tableauString  = message.split(";");
                float montant, prixTotalPaye=0;
                boolean boolFacture = false;
                
                stringIdReservation = tableauString[0];
                montantString = tableauString[3];
                montant = Float.parseFloat(montantString);
                idReservation = Integer.parseInt(stringIdReservation);
                
                //Vérification de la signature
                boolean signatureOk = BouncyClass.verifySignature(cléPubliqueClient, m.getSignature(), message.getBytes());

                if(signatureOk)
                {

                    try {
                        System.out.println("Signature client OK");
                        
                        //Vérification que la carte de crédit est valide auprès du serveur card
                        //Il faut envoyer d'après l'énoncé :
                        /*le numéro de carte et la somme à débiter étant cryptés asymétriquement accompagné de la signature du Serveur_Paiements*/
                        KeyStore ks = null;
                        ks = KeyStore.getInstance("JCEKS");
                        ks.load(new FileInputStream("..\\Serveur_Card\\ServeurCard.JCEKS"), "123".toCharArray());
                        
                        X509Certificate certif = (X509Certificate)ks.getCertificate("lundirsa");
                        cléPubliqueCard = certif.getPublicKey();
                        System.out.println("Cle publique du certificat de ServeurCard.JCEKS recuperée = "+cléPubliqueCard.toString());
                        
                        
                        //Chiffrement du texte clair avec la clé publique
                        byte [] texteCrypted = BouncyClass.encryptRSA(cléPubliqueCard, message.getBytes());
                        MessageCryptedWithSignature mess = new MessageCryptedWithSignature(texteCrypted, m.getSignature());//On garde la signature du serveur paiement
                        RequeteCCAP reqCCAP = new RequeteCCAP(RequeteCCAP.REQUEST_VERIF, mess);
                        
                        oosCard.writeObject(reqCCAP);
                        
                        ReponseCCAP repCCAP = (ReponseCCAP)oisCard.readObject();
                        
                        
                        if(repCCAP.getCode() == ReponseCCAP.SUCCESS)
                        {
                            //Si le paiement a été effectué il faut mettre à jour la BD_HOLIDAYS en mettant à jour la somme payée pour la réservation et s'il a fini de payé 
                            pst = BD.getCon().prepareStatement("Select prixpaye, prixnet FROM reservation WHERE idReservation = ?");
                            pst.setInt(1, idReservation);
                            rs = pst.executeQuery();
                            
                            if(rs.first())
                            {
                                float prixnet = rs.getFloat("prixnet");
                                float prixPaye = rs.getFloat("prixpaye");
                                prixTotalPaye = prixPaye+montant;
                                //S'il dépasse ou c'est égal au prix net la réservation passe en mode payée
                                if(prixTotalPaye >= prixnet)  
                                {
                                    pst = BD.getCon().prepareStatement("UPDATE reservation SET prixpaye = ?, boolpaye = '1' WHERE idReservation = ?");
                                    boolFacture = true;//On va devoir faire une facture
                                }   
                                else
                                    pst = BD.getCon().prepareStatement("UPDATE reservation SET prixpaye = ? WHERE idReservation = ?");
                                
                                
                                pst.setFloat(1, prixTotalPaye);
                                pst.setInt(2, idReservation);
                                System.out.println(pst.toString());
                                pst.executeUpdate();
                                
                                //On ajoute la transaction dans une table
                                //INSERT INTO `sys`.`transaction` (`montant`, `idReservation`) VALUES ('10', '34');
                                pst = BD.getCon().prepareStatement("INSERT INTO transaction (montant, idReservation) VALUES (?,?)");
                                pst.setFloat(1, montant);
                                pst.setInt(2, idReservation);
                                
                                pst.executeUpdate();
                                
                                //On récupère le numéro de la transaction
                                pst = BD.getCon().prepareStatement("Select idTransaction FROM transaction WHERE idReservation = ? AND montant = ?");
                                
                                pst.setInt(1, idReservation);
                                pst.setFloat(2, montant);
                                rs = pst.executeQuery();

                                if(rs.last())
                                {
                                    float prixRestant = prixnet - prixTotalPaye;
                                    if(prixRestant<0)//==> Ca ne peut pas arrivé normalement
                                        prixRestant =0;
                                    String prixRestantString = Float.toString(prixnet);
                                
                                    int idTransaction = rs.getInt("idTransaction");
                                
                                    //Le serveur envoie le prix restant et un numéro de transaction financière
                                    //On prépare le string
                                    messageTransaction = prixRestantString+";"+idTransaction+";"+userEmploye;
                                    System.out.println("Message envoyé avec HMAC : "+messageTransaction);
                                    MessageCryptedWithHMAC mc = new MessageCryptedWithHMAC(BouncyClass.encryptDES(cléSym, messageTransaction.getBytes()), BouncyClass.prepareHMAC(cléSymHMAC, messageTransaction.getBytes()));

                                    rep = new ReponseSPAYMAP(ReponseSPAYMAP.SUCCESS, mc);
                                }
                            }
                            else
                            {
                                rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "Erreur - Réservation non trouvée");
                            } 
                        }
                        else
                        {
                            rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "Carte non valide");
                        }
                        
                        
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                else
                {
                    rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED, "Signature non valide");
                }
                try {
                    oos.writeObject(rep);
                    
                    //On sait si le HMAC a été vérifié vu que le client renvoie une réponse
                    rep = (ReponseSPAYMAP) ois.readObject();
                    
                    if(rep.getCode()== ReponseSPAYMAP.SUCCESS)
                    {
                        System.out.println("HMAC vérifié");
                        //Si le HMAC a été vérifié du côté du client il faut regarder si le solde est payé entièrement on crée une facture finale accompagné de la signature du serveur Paiement
                        //messageTransaction = prixRestantString+";"+idTransaction+";"+userEmploye;
                        String [] tabString = messageTransaction.split(";");
                        String prixRestantString = tabString[0];
                        String idTransaction = tabString[1];
                        
                        
                        if(boolFacture)
                        {
                            System.out.println("Création de la facture");
                            String facture = "Identifiant de la transaction : "+idTransaction+"\nEmployé intervenant : "+userEmploye+"\nPrix Total payé : "+prixTotalPaye+"\n\nMerci de votre confiance";
                        
                            MessageCryptedWithSignature mCrypted = new MessageCryptedWithSignature(BouncyClass.encryptDES(cléSym, facture.getBytes()), BouncyClass.prepareSignature(cléPrivée, facture.getBytes()));
                            
                            rep = new ReponseSPAYMAP(ReponseSPAYMAP.SUCCESS, mCrypted);
                        }
                        else
                            rep = new ReponseSPAYMAP(ReponseSPAYMAP.FAILED);
                        
                        oos.writeObject(rep);
                        System.out.println("Envoi de la facture");
                        //messageTransaction = prixRestantString+";"+idTransaction+";"+userEmploye;
                        
                    }
                    
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(RequeteSPAYMAP.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);
                }
            }
            
        };
    }
}
