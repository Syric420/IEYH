package serveur_activites;

import GUI.ServForm;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Mika
 */
public class Serveur_Activites
{
    public static void main(String[] args)
    {
        FileInputStream fis = null;
        try
        {
            Properties prop = new Properties();
            fis = new FileInputStream("src\\serveur_activites\\serveur_activites.properties");

            prop.load(fis);
            System.out.println("Serveur_Activites: Création de l'objet ServForm...");
            ServForm sf = new ServForm(prop.getProperty("PORT_ACTIVITES"), prop.getProperty("NB_MAX_CLI"));
            sf.setVisible(true);
        }
        catch(Exception ex)
        {
            System.out.println("Serveur_Activites: Exception: " + ex.getMessage());
        }
    }
}
