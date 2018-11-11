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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        
        String action = request.getParameter("action");
        switch(action)
        {
            case "login":
                try {
                    PreparedStatement pst = null;
                    sc.log("Case login");
                    String user = (String)request.getParameter("user");
                    String pwd = (String)request.getParameter("password");
                    String newUser = (String)request.getParameter("newUser");
                    
                    if(newUser == null)
                    {   

                        //La case nouvel utilisateur est décoché
                        pst = BD.getCon().prepareStatement("select password from login where user = ?");

                        pst.setString(1, user);

                        System.out.println("Requete SQL = "+pst.toString());
                        rs = pst.executeQuery();
                        if(rs.first())
                        {
                            String mdp =  rs.getString("password");
                            System.out.println("mdp SQL recu = "+mdp);
                            System.out.println("mdp formulaire = "+pwd);
                            if(mdp.equals(pwd))
                            {
                                //Les deux mots de passe sont identiques
                                sc.log("Login réussi");
                            }
                            else
                            {
                                sc.log("Login raté");
                                msgErreur = "Mot de passe incorrect - veuillez réessayer";
                                request.setAttribute("msgErreur", msgErreur);
                                RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                                rd.forward(request, response);
                            }
                        }
                        else
                        {
                            msgErreur = "Utilisateur introuvable - veuillez réessayer";
                            request.setAttribute("msgErreur", msgErreur);
                            sc.log("Utilisateur introuvable");
                            RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                            rd.forward(request, response);
                        }
                    }    
                    else
                    {
                        //La case nouvel utilisateur est coché

                    }
                        /*System.out.println(user +" : "+pwd);
                        System.out.println(newUser);*/



                    
                } catch (SQLException ex) {
                        Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                break;
            default:
                RequestDispatcher rd = sc.getRequestDispatcher("/JspLogin.jsp");
                rd.forward(request, response);
                break;
        }
        
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
