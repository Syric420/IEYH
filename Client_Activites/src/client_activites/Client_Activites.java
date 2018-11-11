/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_activites;

import GUI.CliForm;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Mika
 */
public class Client_Activites
{
    public static void main(String[] args)
    {
        FileInputStream fis = null;
        try
        {
            Properties prop = new Properties();
            fis = new FileInputStream("..\\Serveur_Activites\\src\\serveur_activites\\serveur_activites.properties");

            prop.load(fis);

            System.out.println("Client_Activites: Création de l'objet CliForm...");
            CliForm cf = new CliForm(prop.getProperty("PORT_ACTIVITES"));
            cf.setVisible(true);
        }
        catch(Exception ex)
        {
            System.out.println("Client_Activites: Exception: " + ex.getMessage());
        }
    }
}
