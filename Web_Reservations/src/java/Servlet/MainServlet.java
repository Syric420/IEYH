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
    BeanBD BD;
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
                    pst = BD.getCon().prepareStatement("select password from loginclient where user = ?");
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
                                    //Les deux mots de passe sont identiques on crée la session
                                    session.setAttribute("user", user);session.setAttribute("pwd", pwd);
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
            case "initpayer":
                if(isAuthenticated(request))
                {
                    //on regarde si l'utilisateur est bien connecté
                    sc.log("Appuie sur le bouton Payer");
                    /*RequestDispatcher rd = sc.getRequestDispatcher("/.jsp");
                    rd.forward(request, response);*/
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
                    sc.log("Appuie sur le bouton Reserver chambre");
                    //On récupère les différents données donc numéro de chambre + la date début et de fin
                    String user = (String)request.getParameter("dateDebut");
                    String pwd = (String)request.getParameter("dateFin");
                    int numChambre = Integer.parseInt(request.getParameter("numChambre"));
                    System.out.println(user);System.out.println(pwd);System.out.println(numChambre);
                    
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
                    //On crée l'utilisateur
                    pst = BD.getCon().prepareStatement("INSERT INTO loginclient (user, password) VALUES (?, ?)");
                    HttpSession session = request.getSession(true);

                    pst.setString(1, (String)session.getAttribute("user"));
                    pst.setString(2, (String)session.getAttribute("pwd"));
                    System.out.println("Requete SQL = "+pst.toString());
                    pst.executeUpdate();
                    
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
                        System.out.println("Requete SQL = "+pst.toString());
                        pst.executeUpdate();
                        
                        //Il faut absolument l'id de l'adresse pour pouvoir l'associer au voyageur correspondant
                        pst = BD.getCon().prepareStatement("Select * from adresse WHERE (nomRue = ? AND numero = ?)");
                        pst.setString(1, adresse);
                        pst.setInt(2, numero);
                        System.out.println("Requete SQL = "+pst.toString());
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
                    RequestDispatcher rd = sc.getRequestDispatcher("/JspInit.jsp");
                    rd.forward(request, response);
                    //-------------------
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
