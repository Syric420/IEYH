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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class RequeteFUCAMP implements Requete, Serializable
{
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_LOGOUT = 1;
    public static final int REQUEST_ACT = 2;    
    public static final int REQUEST_LIST_ACT = 3;
    public static final int REQUEST_DESIST = 4;
    public static final int REQUEST_SEARCH = 5;

    private int type;
    private Message mes;

    public int getType()
    {
        return type;
    }
    
    public Message getMessage() 
    {
        return mes;
    }
    
    public RequeteFUCAMP(int t, Message m)
    {
        type = t;
        mes = m;
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
            private RequeteFUCAMP req = new RequeteFUCAMP(type, mes);
            
            private BeanBD BD = beanBD;
            private ResultSet rs;
            private ResultSetMetaData rsmd;
            PreparedStatement pst;
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
                        case REQUEST_ACT:
                            treatAct();
                            break;
                        case REQUEST_LIST_ACT:
                            getActivities();
                            break;
                        case REQUEST_DESIST:
                            treatActDesistement();
                            break;
                        case REQUEST_SEARCH:
                            treatActSearch();
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
                MessageLogin m = (MessageLogin)req.mes;
                ReponseFUCAMP rep;
    
                if(state == "AUTHENTICATED")
                {
                    rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Déjà authentifié"));
                    return;
                }
                else
                {
                    try 
                    {
                        pst = BD.getCon().prepareStatement("select password from login where user = ?");
                        pst.setString(1, m.getUsername());

                        System.out.println("Requete SQL = " + pst.toString());
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            String mdp =  rs.getString("password");
                            System.out.println("mdp SQL recu = "+mdp);
                            System.out.println("mdp requeteFUCAMP = "+m.getPassword());
                            if(mdp.equals(m.getPassword()))
                            {
                                //Les deux mots de passe sont identiques
                                rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Login réussi"));
                                state = "AUTHENTICATED";
                            }
                            else
                            {
                                rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Mauvais mot de passe"));
                            }
                        }
                        else
                        {
                            rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("L'utilisateur n'existe pas"));
                        }
                        oos.writeObject(rep);
                    } 
                    catch (SQLException | IOException ex) 
                    {
                        Logger.getLogger(ReponseFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            private void treatLogout()
            {
                if(state == "NON_AUTHENTICATED") 
                {
                    rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Vous n'êtes pas authentifié"));
                    cs.TraceEvenements("serveur#client non authentifié!#" + this.getClass());
                    return;
                }
                state = "NON_AUTHENTICATED";
                rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Déconnexion réussie!"));
                cs.TraceEvenements("serveur#client déconnecté#" + this.getClass());
            }
            
            private synchronized void treatAct()
            {
                cs.TraceEvenements("serveur#réservation activité#" + this.getClass());
                
                MessageInscription m = (MessageInscription)req.mes;

                try
                {
                    //récupère l'id du client concerné
                    pst = BD.getCon().prepareStatement("select idVoyageur from voyageur where nom = ?");
                    pst.setString(1, m.getClient());
                    rs = pst.executeQuery();
                    if(rs.first())
                    {
                        String idCli =  rs.getString("idVoyageur"); //récupère l'id du client
                        pst = BD.getCon().prepareStatement("insert into reservation "
                                + "(idReservation, typeReservation, dateDebut, dateFin, prixNet, boolPaye, refActivite, refChambre, idReferent) "
                                + "values (?,?,?,?,?,?,?,?,?)");

                        int rand = 0;
                        while(true)
                        {
                            rand = ThreadLocalRandom.current().nextInt(1000, 9999); //rand entre 1000 et 9999
                            rs = BD.executeQuery("select idReservation from reservation where idReservation = " + rand);
                            rs.beforeFirst();
                            if(!rs.next())
                                break; //pas de doublon
                        }
                        pst.setString(1, String.valueOf(rand)); //idReservation
                        pst.setString(2, "Activite"); //typeReservation
                        pst.setString(3, m.getDate()); //dateDebut
                        pst.setString(4, m.getDate()); //dateFin
                        pst.setString(5, m.getPrix()); //prixNet
                        pst.setString(6, "0"); //boolPaye
                        pst.setString(7, m.getId()); //refActivite
                        pst.setString(8, null); //refChambre
                        pst.setString(9, idCli); //idReferent
                        System.out.println("Requete SQL = " + pst.toString());
                        pst.executeUpdate();

                        if(m.getType().equals("N")) //si ça correspond à une activité de plusieurs jours
                        {
                            //incrémente la dateFin avec le nb de jours correspondant à la durée de l'activité
                            pst = BD.getCon().prepareStatement("update reservation set dateFin = date_add(?, interval ? day) where idReservation = ?"); //update le champ dans la BD
                            pst.setString(1, m.getDate()); //la date qui subit l'addition
                            pst.setString(2, m.getDureeJour()); //le nombre de jours à ajouter
                            pst.setString(3, String.valueOf(rand)); //l'id de la réservation concerné
                            pst.executeUpdate();
                        }

                        //incrémente le nb de participants de l'activité concerné
                        pst = BD.getCon().prepareStatement("select nbParticipants from activite where idActivite = ?"); //on va chercher le nb de part de l'activité concerné
                        pst.setString(1, m.getId()); //id activité
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            String StrNbPart =  rs.getString("nbParticipants"); //récupère le nbParticipants
                            int IntNbPart = Integer.parseInt(StrNbPart);
                            IntNbPart++;

                            pst = BD.getCon().prepareStatement("update activite set nbParticipants = ? where idActivite = ?"); //update le champ dans la BD
                            pst.setString(1, String.valueOf(IntNbPart)); //le nb de participants incrémenté
                            pst.setString(2, m.getId()); //l'id de l'activité concerné
                            pst.executeUpdate();
                        }
                        rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Réservation OK"));
                    }
                    else
                        rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Le client n'existe pas"));
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(ReponseFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private synchronized void treatActDesistement()
            {
                cs.TraceEvenements("serveur#désistement activité#" + this.getClass());
                    
                MessageDesistement m = (MessageDesistement)req.mes;
                
                try
                {
                    //récupère l'id du client concerné
                    pst = BD.getCon().prepareStatement("select idVoyageur from voyageur where nom = ?");
                    pst.setString(1, m.getClient());
                    rs = pst.executeQuery();
                    if(rs.first())
                    {
                        String idCli =  rs.getString("idVoyageur"); //récupère l'id du client
                        pst = BD.getCon().prepareStatement("select * from reservation where dateDebut = ? and refActivite = ? and idReferent = ?");
                        pst.setString(1, m.getDate()); //date
                        pst.setString(2, m.getId()); //id activité
                        pst.setString(3, idCli); //id client
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            pst = BD.getCon().prepareStatement("delete from reservation where dateDebut = ? and refActivite = ? and idReferent = ?");
                            pst.setString(1, m.getDate()); //date
                            pst.setString(2, m.getId()); //id activité
                            pst.setString(3, idCli); //id client
                            pst.executeUpdate();
                        
                            //décrémente le nb de participants de l'activité concerné
                            pst = BD.getCon().prepareStatement("select nbParticipants from activite where idActivite = ?"); //on va chercher le nb de part de l'activité concerné
                            pst.setString(1, m.getId());
                            rs = pst.executeQuery();
                            if(rs.first())
                            {
                                String StrNbPart =  rs.getString("nbParticipants"); //récupère le nbParticipants
                                int IntNbPart = Integer.parseInt(StrNbPart);
                                IntNbPart--;

                                pst = BD.getCon().prepareStatement("update activite set nbParticipants = ? where idActivite = ?"); //update le champ dans la BD
                                pst.setString(1, String.valueOf(IntNbPart)); //le nb de participants décrémenté
                                pst.setString(2, m.getId()); //l'id de l'activité concerné
                                pst.executeUpdate();
                            }
                            rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Désistement OK"));
                        }
                        else
                            rep = new ReponseFUCAMP(ReponseFUCAMP.SUCCESS, new MessageSimple("Il n'y a pas de réservation trouvé au nom du client concerné"));
                    }
                    else
                    {
                        rep = new ReponseFUCAMP(ReponseFUCAMP.FAILED, new MessageSimple("Le client n'existe pas"));
                    }
                }
                catch (SQLException ex) 
                {
                    Logger.getLogger(ReponseFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void getActivities()
            {
                try
                {
                    cs.TraceEvenements("serveur#récup des activités#" + this.getClass());
                    
                    MessageSimple m = (MessageSimple)req.mes;
                    if(m.getMessage().equals("1")) rs = BD.executeQuery("SELECT * FROM activite WHERE DuréeJour = 1");
                    else if(m.getMessage().equals("N")) rs = BD.executeQuery("SELECT * FROM activite WHERE DuréeJour > 1");
                    else rs = BD.executeQuery("SELECT * FROM activite");
                    
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
                    for(int i=0;i<listActs.size();i++)
                        System.out.println(listActs.get(i));

                    rs.close();
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(RequeteFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            private void treatActSearch()
            {
                try
                {
                    cs.TraceEvenements("serveur#récup des activités search#" + this.getClass());
                    
                    MessageRecherche m = (MessageRecherche)req.mes;
                    if(m.getChamp().equals("Client")) 
                    {
                        //récupère l'id du client concerné
                        pst = BD.getCon().prepareStatement("select idVoyageur from voyageur where nom = ?");
                        pst.setString(1, m.getValeur());
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            String idCli =  rs.getString("idVoyageur"); //récupère l'id du client
                            rs = BD.executeQuery("SELECT idActivite, type, nbParticipants, nbMaxParticipants, DuréeMin, DuréeJour, PrixHTVA " +
                                                 "FROM activite " +
                                                 "INNER JOIN reservation ON activite.idActivite = reservation.refActivite " +
                                                 "INNER JOIN voyageur ON reservation.idReferent = voyageur.idVoyageur " +
                                                 "WHERE idReferent = " + idCli);
                        }
                    }
                    else if(m.getChamp().equals("Date"))
                    {
                        rs = BD.executeQuery("SELECT idActivite, type, nbParticipants, nbMaxParticipants, DuréeMin, DuréeJour, PrixHTVA " +
                                             "FROM activite " +
                                             "INNER JOIN reservation ON activite.idActivite = reservation.refActivite " +
                                             "INNER JOIN voyageur ON reservation.idReferent = voyageur.idVoyageur " +
                                             "WHERE dateDebut = '" + m.getValeur() + "'");
                    }
                    
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
                    for(int i=0;i<listActs.size();i++)
                        System.out.println(listActs.get(i));

                    rs.close();
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(RequeteFUCAMP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
}
