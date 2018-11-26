/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Database.facility.BeanBD;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vince
 */
public class MainServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    static BeanBD BD;
    ResultSet rs;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext sc = config.getServletContext();
        
        BD = new BeanBD();
        BD.setTypeBD("mysql");
        BD.connect();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String msgErreur;
        ServletContext sc = getServletContext();
        //sc.log("Passage dans processRequest");
        String action = request.getParameter("action");
        System.out.println("action = "+action);
        PreparedStatement pst = null;
        switch(action)
        {
            case "login":
                try {
                    
                    //sc.log("Case login");
                    String user = (String)request.getParameter("user");
                    String pwd = (String)request.getParameter("password");
                    String newUser = (String)request.getParameter("newUser");
                    pst = BD.getCon().prepareStatement("select * from loginclient where user = ?");
                    HttpSession session = request.getSession(true);
                    if(!user.equals("") && !pwd.equals(""))
                    {
                        pst.setString(1, user);
                        if(newUser == null)
                        {   
                            //La case nouvel utilisateur est décoché

                            //System.out.println(pst.toString());
                            rs = pst.executeQuery();
                            if(rs.first())
                            {
                                String mdp =  rs.getString("password");
                                //System.out.println("mdp SQL recu = "+mdp);
                                //System.out.println("mdp formulaire = "+pwd);
                                if(mdp.equals(pwd))
                                {
                                    int id = rs.getInt("refVoyageur");
                                    //Les deux mots de passe sont identiques on crée la session
                                    session.setAttribute("user", user);session.setAttribute("pwd", pwd);session.setAttribute("identifiant", id);
                                    this.setAuthenticated(request, true);//On signifie qu'il est identifié
                                    sc.log("Login réussi");
                                    RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                                    rd.forward(request, response);
                                }
                                else
                                {
                                    this.setAuthenticated(request, false);
                                    //sc.log("Login raté");
                                    msgErreur = "Mot de passe incorrect - veuillez réessayer";
                                    request.setAttribute("msgErreur", msgErreur);
                                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                                    rd.forward(request, response);
                                }
                            }
                            else
                            {
                                this.setAuthenticated(request, false);
                                msgErreur = "Utilisateur introuvable - veuillez réessayer";
                                request.setAttribute("msgErreur", msgErreur);
                                //sc.log("Utilisateur introuvable");
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                                rd.forward(request, response);
                            }
                        }
                        else
                        {
                            //La case nouvel utilisateur est coché
                            //Il faut vérifier si l'utilisateur existe déjà au cas ou il aurait coché sans faire exprès
                            //sc.log("La case nouvel utilisateur est cochée");
                            rs = pst.executeQuery();
                            if(!rs.first())
                            {
                                //sc.log("l'utlisateur n'existe pas on le crée");
                                //Si l'utlisateur n'existe pas on crée sa session
                                
                                session.setAttribute("user", user);session.setAttribute("pwd", pwd);
                                
                                
                                //Et on le redirige vers lap age d'inscription
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspInscription.jsp");
                                rd.forward(request, response);
                            }
                            else
                            {
                                //L'utilisateur existe déjà donc message d'erreur
                                msgErreur = "Nom d'utilisateur déjà existant - veuillez réessayer";
                                request.setAttribute("msgErreur", msgErreur);
                                //sc.log("Nom d'utilisateur déjà existant");
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                                rd.forward(request, response);
                            }
                        }
                    }
                    
                } catch (SQLException ex) {
                        Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                
                break;
            case "init":
                sc.log("INIT");
                
                if(isAuthenticated(request))
                {
                    HttpSession session = request.getSession(true);
                    //on regarde si l'utilisateur est bien connecté
                    sc.log("Appuie sur le bouton Caddie");
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspCaddie.jsp");
                    rd.forward(request, response);
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "initAnnuler":
                if(isAuthenticated(request))
                {
                    HttpSession session = request.getSession(true);
                    //on regarde si l'utilisateur est bien connecté
                    sc.log("Appuie sur le bouton int Annuler");
                    int idReservation = Integer.parseInt(request.getParameter("inputNumReservation"));
                    
                    try {
                        pst = BD.getCon().prepareStatement("DELETE FROM reservation WHERE idReservation = ? AND idReferent = ?");
                        pst.setInt(1, idReservation);
                        pst.setInt(2, (int)session.getAttribute("identifiant"));
                        pst.executeUpdate();
                    } catch (SQLException ex) {

                        
                    }
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                        rd.forward(request, response);
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "initDeco":
                if(isAuthenticated(request))
                {
                    HttpSession session = request.getSession(true);
                    //on regarde si l'utilisateur est bien connecté
                    session.invalidate();
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "initpayer":
                
                if(isAuthenticated(request))
                {
                    try {
                        HttpSession session = request.getSession(true);
                        //on regarde si l'utilisateur est bien connecté
                        sc.log("Appuie sur le bouton Payer");

                        //Il faut parcourir toutes les réservations et additionner le prix
                        pst = BD.getCon().prepareStatement("Select prixNet from reservation where idReferent = ? AND boolPaye = 0");
                        session = request.getSession(true);
                        pst.setInt(1, (int)session.getAttribute("identifiant"));
                        rs = pst.executeQuery();
                        float totalAPayer=0;
                        while(rs.next())
                        {
                            totalAPayer += rs.getFloat(1);
                            System.out.println(totalAPayer);
                        }
                        
                        session.setAttribute("totalAPayer", totalAPayer);
                        
                        
                        RequestDispatcher rd = sc.getRequestDispatcher("/JspPay.jsp");
                        rd.forward(request, response);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "payPayer":
                 if(isAuthenticated(request))
                {
                    try {
                        HttpSession session = request.getSession(true);
                        //on regarde si l'utilisateur est bien connecté

                        //Il faut mettre toutes les réservations sur 1 à payer pour ce client
                        pst = BD.getCon().prepareStatement("UPDATE reservation SET boolPaye = '1' WHERE idReferent = ? AND boolPaye = 0");
                        pst.setInt(1, (int)session.getAttribute("identifiant"));

                        pst.executeUpdate();

                        //Envoi du mail de confirmation 
                        RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                        rd.forward(request, response);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "payAnnuler":
                 if(isAuthenticated(request))
                {
                    HttpSession session = request.getSession(true);
                    //on regarde si l'utilisateur est bien connecté
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                    rd.forward(request, response);
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                break;
            case "reserverChambre":
                if(isAuthenticated(request))
                {
                    try {
                        sc.log("Appuie sur le bouton Reserver chambre");
                        //On récupère les différents données donc numéro de chambre + la date début et de fin
                        String dateDebut = (String)request.getParameter("dateDebut");
                        String dateFin = (String)request.getParameter("dateFin");
                        int numChambre = Integer.parseInt(request.getParameter("numChambre"));

                        /*Il faut regarder si la chambre est dispo à cette date là
                        +
                        Il faut regarder si le client n'a pas déjà réservé une autre chambre à cette date là (dans l'énoncé
                        =on ne perdra pas de vue qu'un même voyageur ne peut pas être à deux endroits différents au même moment*/

                        pst = BD.getCon().prepareStatement("Select * from reservation WHERE refChambre = ? AND ((dateDebut between ? AND ?) OR (dateFin between ? AND ?))");
                       
                        //conversion pour les dates de string à sql.date
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                        java.util.Date date = sdf1.parse(dateDebut);
                        java.sql.Date dateDebutSql = new java.sql.Date(date.getTime()); 
                        
                        date = sdf1.parse(dateFin);
                        java.sql.Date dateFinSql = new java.sql.Date(date.getTime()); 
                        
                        pst.setInt(1, numChambre);
                        pst.setDate(2, dateDebutSql);
                        pst.setDate(3, dateFinSql);
                        pst.setDate(4, dateDebutSql);
                        pst.setDate(5, dateFinSql);
                        System.out.println(pst.toString());
                        
                        rs = pst.executeQuery();
                        
                        if(!rs.first())
                        {
                            //Si il y a aucune réservation à cette date là pour cette chambre
                            //Il faut vérifier si l'utilisateur a déjà reservé une chambre pour cette date là
                            
                            HttpSession session = request.getSession(true);
                            
                            pst = BD.getCon().prepareStatement("Select * from reservation WHERE idReferent = ? AND ((dateDebut between ? AND ?) OR (dateFin between ? AND ?))");
                            int id = (int)session.getAttribute("identifiant");
                            pst.setInt(1, id);
                            pst.setDate(2, dateDebutSql);
                            pst.setDate(3, dateFinSql);
                            pst.setDate(4, dateDebutSql);
                            pst.setDate(5, dateFinSql);
                            
                            rs = pst.executeQuery();
                            
                            if(!rs.first())
                            {
                               //Si aucune réservation faite par ce client à cette date là il faut enregistrer sa réservation
                                sc.log("Le champ est libre il faut enregistrer sa réservation mais mettre qu'il l'a pas encore payé");
                                //J'ai besoin du prix de la chambre pour calculer le prix de la réservation
                                pst = BD.getCon().prepareStatement("Select prixHTVA FROM chambre WHERE idChambre = ?");
                                pst.setInt(1, numChambre);
                                rs = pst.executeQuery();
                                
                                rs.next();
                                float prixHTVA = rs.getFloat("prixHTVA");
                                pst = BD.getCon().prepareStatement("INSERT INTO reservation" + 
                                "(typeReservation, dateDebut, dateFin, boolPaye, refChambre, idReferent, prixNet)"
                                + "VALUES(?, ?, ?, ?, ?, ?, ?)");
                                pst.setString(1, "Chambre");
                                pst.setDate(2, dateDebutSql);
                                pst.setDate(3, dateFinSql);
                                pst.setBoolean(4, false);
                                pst.setInt(5, numChambre);
                                pst.setInt(6, id);
                                
                                //Nombre de jours entre deux dates
                                long nbJoursMili = dateFinSql.getTime() - dateDebutSql.getTime();
                                int nbJours = (int) nbJoursMili/86400000;
                                //On calcule le prix grace au nombre de jours * prix HTVA
                                
                                pst.setFloat(7, prixHTVA*nbJours);
                                pst.executeUpdate();

                                //La réservation est dans la BD maintenant
                                
                                msgErreur = "Réservation ajoutée au caddie";//C'est pas un message d'erreur ici mais bon flemme de changer le nom de variable
                                request.setAttribute("msgErreur", msgErreur);
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                                rd.forward(request, response);
                            }
                            else
                            {
                                //réservation déjà faite par ce client à cette date là il faut afficher un message d'erreur
                                
                                msgErreur = "Problème de réservation - vous avez déjà reservé une chambre pendant ces dates";
                                request.setAttribute("msgErreur", msgErreur);
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspCaddie.jsp");
                                rd.forward(request, response);
                            }
                        }
                        else
                        {
                            //il y a une réservation à cette date là pour cette chambre donc afficher une message d'erreur
                            msgErreur = "Problème de réservation - la chambre est déjà prise pour cette date";
                            request.setAttribute("msgErreur", msgErreur);
                            RequestDispatcher rd = sc.getRequestDispatcher("/JspCaddie.jsp");
                            rd.forward(request, response);
                        }
                        
                    } catch (SQLException | ParseException ex) {
                        Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    //s'il est pas connecté alors on le renvoie au login
                    msgErreur = "Erreur - veuillez vous connecter";
                    request.setAttribute("msgErreur", msgErreur);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                
                break;
            case "inscription":
                sc.log("Appuie sur le bouton inscription");
                try {
                    //On regarde si l'adresse existe déjà (exemple un membre de la même famille) du coup on recrée pas la même adresse
                    String adresse = (String)request.getParameter("Adresse");
                    int numero = Integer.parseInt(request.getParameter("Numero"));
                    int codePostal = Integer.parseInt(request.getParameter("CodePostal"));
                    String commune = (String)request.getParameter("Commune");
                    
                    pst = BD.getCon().prepareStatement("Select * from adresse WHERE (nomRue = ? AND numero = ?)");
                    pst.setString(1, adresse);
                    pst.setInt(2, numero);
                    System.out.println("Requete SQL = "+pst.toString());
                    rs = pst.executeQuery();
                    int numRowInserted;
                    if(!rs.first())
                    {
                        //Si l'adresse existe pas on la crée
                        pst = BD.getCon().prepareStatement("INSERT INTO adresse (nomRue, numero, codePostal, commune) VALUES (?, ?, ?, ?)");
                        pst.setString(1, adresse);
                        pst.setInt(2, numero);
                        pst.setInt(3, codePostal);
                        pst.setString(4, commune);
                        //System.out.println("Requete SQL = "+pst.toString());
                        pst.executeUpdate();
                        
                        //Il faut absolument l'id de l'adresse pour pouvoir l'associer au voyageur correspondant
                        pst = BD.getCon().prepareStatement("Select * from adresse WHERE (nomRue = ? AND numero = ?)");
                        pst.setString(1, adresse);
                        pst.setInt(2, numero);
                        //System.out.println("Requete SQL = "+pst.toString());
                        rs = pst.executeQuery();
                        rs.next();
                        numRowInserted = rs.getInt("idAdresse");//on récupère le numéro idAdresse
                    }   
                    else
                    {
                        //Si l'adresse existe on récupère juste son ID pour pouvoir l'associer au voyageur
                        numRowInserted = rs.getInt("idAdresse");//on récupère le numéro idAdresse
                    }
                    
                    //-------------------
                    //On crée le voyageur et il est considéré comme référent et il faut pas oublier de l'associer à son adresse
                    String nom = (String)request.getParameter("Nom");
                    String prenom = (String)request.getParameter("Prenom");
                    String nationalite = (String)request.getParameter("Nationalite");
                    String dateNaiss = (String)request.getParameter("Date");
                    String email = (String)request.getParameter("Email");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date date = sdf1.parse(dateNaiss);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());  
                    
                    pst = BD.getCon().prepareStatement("INSERT INTO voyageur (nom, prenom, nationalite, dateNaiss, email, adresse, boolReferent) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    pst.setString(1, nom);
                    pst.setString(2, prenom);
                    pst.setString(3, nationalite);
                    pst.setDate(4, sqlDate);
                    pst.setString(5, email);
                    pst.setInt(6, numRowInserted);
                    pst.setBoolean(7, true);
                    System.out.println("Requete SQL = "+pst.toString());
                    pst.executeUpdate();
                    
                    setAuthenticated(request, true);
                    
                    //-------------------
                    
                    //On crée l'utilisateur
                    pst = BD.getCon().prepareStatement("Select * from voyageur WHERE (nom = ? AND prenom = ?)");
                    pst.setString(1, nom);
                    pst.setString(2, prenom);
                    rs = pst.executeQuery();
                    rs.next();
                    
                    int refVoyageur = rs.getInt(1);//On récupère son ID
                    pst = BD.getCon().prepareStatement("INSERT INTO loginclient (user, password, refVoyageur) VALUES (?, ?, ?)");
                    
                    HttpSession session = request.getSession(true);
                    pst.setString(1, (String)session.getAttribute("user"));
                    pst.setString(2, (String)session.getAttribute("pwd"));
                    session.setAttribute("identifiant", refVoyageur);//On le met dans l'objet session 
                    pst.setInt(3, refVoyageur);
                    System.out.println("Requete SQL = "+pst.toString());
                    pst.executeUpdate();
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                    rd.forward(request, response);
                    //----------------------
                } catch (SQLException | ParseException ex) {
                    Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                    setAuthenticated(request, false);
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                    rd.forward(request, response);
                }
                
                break;
            default:
                sc.log("Default");
                RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                rd.forward(request, response);
                break;
        }
        
    }
    
    private void setAuthenticated (HttpServletRequest request, boolean b)
    {
        HttpSession session = request.getSession(true);
        if (b) session.setAttribute("UserValid", "Ok");
        else session.removeAttribute("UserValid");
    }
    
    private boolean isAuthenticated (HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        Object existe = session.getAttribute("UserValid");
        return existe!=null;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
