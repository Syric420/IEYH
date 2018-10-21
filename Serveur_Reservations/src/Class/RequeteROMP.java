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
import java.sql.Date;

public class RequeteROMP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;
    public static final int REQUEST_LISTROOM = 2;
    public static final int REQUEST_BROOM = 3;
    public static final int REQUEST_LISTRESERV = 4;
    public static final int REQUEST_CROOM = 5;

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
                        case REQUEST_LISTROOM:
                            treatListRoom();
                            break;
                        case REQUEST_BROOM:
                            treatBroom();
                            break;
                        case REQUEST_LISTRESERV:
                            treatListReserv();
                            break;
                        case REQUEST_CROOM:
                            treatCroom();
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
                
                if(state.equals("AUTHENTICATED"))
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
                try {
                    if(state.equals("NON_AUTHENTICATED"))
                    {
                        rep = new ReponseROMP(ReponseROMP.FAILED,"Vous n'êtes pas authentifié");
                        cs.TraceEvenements("serveur#client non authentifié!#" + this.getClass());
                        return;
                    }
                    state = "NON_AUTHENTICATED";
                    rep = new ReponseROMP(ReponseROMP.SUCCESS, "Déconnexion réussie!");
                    cs.TraceEvenements("serveur#client déconnecté#" + this.getClass());
                    
                    oos.writeObject(rep);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            

            private void treatBroom() {
                try {
                    MessageBooking mb = (MessageBooking)req.message;
                    //INSERT INTO `sys`.`reservation` (`idReservation`, `typeReservation`, `typeReservation`, `dateFin`, `boolPaye`, `refChambre`, `idReferent`) VALUES (NULL, 'Chambre', '2018-10-20', '2018-10-21', '0', '1', '1');
                    
                    //Regarder si le référent existe
                    PreparedStatement pst = BD.getCon().prepareStatement("Select * from voyageur WHERE nom = ? AND prenom = ?");
                    pst.setString(1, mb.getNomRef());
                    pst.setString(2, mb.getPrenomRef());
                    rs = pst.executeQuery();
                    if(rs.first())
                    {
                        int idVoyageur = rs.getInt("idVoyageur");
                        //Si le voyageur existe alors on prend son ID car on en aura besoin pour faire la réservation
                        
                        //On vérifie si une réservation n'est pas déjà faite
                        pst = BD.getCon().prepareStatement("Select * from reservation WHERE refChambre = ? AND ((dateDebut between ? AND ?) OR (dateFin between ? AND ?))");
                        pst.setInt(1, mb.getRefChambre());
                        pst.setDate(2, mb.getDateArrivee());
                        pst.setDate(3, mb.getDateDepart());
                        pst.setDate(4, mb.getDateArrivee());
                        pst.setDate(5, mb.getDateDepart());
                        
                        System.out.println(pst);
                        rs = pst.executeQuery();
                        if(!rs.first())
                        {
                             pst = BD.getCon().prepareStatement("INSERT INTO reservation" + 
                                "(typeReservation, dateDebut, dateFin, boolPaye, refChambre, idReferent, prixNet)"
                                + "VALUES(?, ?, ?, 0, ?, ?, ?)");
                            pst.setString(1, "Chambre");
                            pst.setDate(2, mb.getDateArrivee());
                            pst.setDate(3, mb.getDateDepart());
                            pst.setInt(4, mb.getRefChambre());
                            pst.setInt(5, idVoyageur);

                            //Nombre de jours entre deux dates
                            long nbJoursMili = mb.getDateDepart().getTime() - mb.getDateArrivee().getTime();
                            int nbJours = (int) nbJoursMili/86400000;
                            //On calcule le prix grace au nombre de jours * prix HTVA
                            pst.setFloat(6, mb.getPrixHTVA()*nbJours);

                            pst.executeUpdate();
                            System.out.println("Reservation ajoutée");
                            rep = new ReponseROMP(ReponseROMP.SUCCESS, "Réservation bien ajoutée");
                        }
                        else
                        {
                            System.out.println("Erreur date déjà prise");
                            rep = new ReponseROMP(ReponseROMP.FAILED, "Problème dans la réservation : la chambre est déjà prise pour une telle date");
                        } 
                    }
                    else
                    {
                        System.out.println(mb.getNomRef()+" "+mb.getPrenomRef()+" est introuvable dans la BD");
                        rep = new ReponseROMP(ReponseROMP.FAILED, "L'utilisateur n'existe pas");
                    }
                        
                    //
                    
                    oos.writeObject(rep);
                    
                            //
                            } catch (SQLException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            


            private void treatListRoom() {
                try {
                    //Il faut juste lister les différentes chambres possibles
                    
                    //Récupération des différentes chambres dans la BD
                    ResultSet rs = BD.executeQuery("Select * from chambre");
                    
                     
                     MessageListVector mlr = new MessageListVector();
                    //Ca sera une linkedlist de vector pour avoir plus facile à la mettre dans la jtable
                    while(rs.next())
                    {
                        Vector vecRoom = new Vector();
                        int id = rs.getInt(1);
                        
                        String categorie, type;
                        categorie = rs.getString(2);
                        type = rs.getString(3);
                        float prixHtva = rs.getFloat(4);
                        
                        vecRoom.add(id);
                        vecRoom.add(categorie);
                        vecRoom.add(type);
                        vecRoom.add(prixHtva);
                        mlr.getListRoom().add(vecRoom);//Mettre ça dans un message et l'envoyer
                    }
                    
                     
                     
                     oos.writeObject(mlr);
                   
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }

            private void treatListReserv() {
                 try {
                    //Il faut juste lister les différentes reservations possibles
                    
                    //Récupération des différentes reservations dans la BD
                    ResultSet rs = BD.executeQuery("Select * from reservation");
                    
                     
                     MessageListVector mlr = new MessageListVector();
                    //Ca sera une linkedlist de vector pour avoir plus facile à la mettre dans la jtable
                    while(rs.next())
                    {
                        Vector vecRoom = new Vector();
                        int id = rs.getInt(1);
                        
                        String type;
                        type = rs.getString(2);
                        Date dateDebut, dateFin;
                        dateDebut = rs.getDate(3);
                        dateFin = rs.getDate(4);
                        
                        float prix = rs.getFloat(5);
                        
                        boolean paye = rs.getBoolean(6);
                        int refChambre = rs.getInt(8);
                        int idRef = rs.getInt(9);
                        
                        vecRoom.add(id);
                        vecRoom.add(type);
                        vecRoom.add(dateDebut);
                        vecRoom.add(dateFin);
                        vecRoom.add(prix);
                        vecRoom.add(paye);
                        vecRoom.add(refChambre);
                        vecRoom.add(idRef);
                        
                        mlr.getListRoom().add(vecRoom);//Mettre ça dans un message et l'envoyer
                    }
                    
                     
                     
                     oos.writeObject(mlr);
                   
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private void treatCroom() {
                try {
                    MessageCancel mc = (MessageCancel) req.message;
                    PreparedStatement pst = BD.getCon().prepareStatement("DELETE FROM reservation WHERE idReservation = ?");
                    pst.setInt(1, mc.getIdReservation());
                    
                    pst.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteROMP.class.getName()).log(Level.SEVERE, null, ex);
                }
                

            }

            
            
        };
    }
}
