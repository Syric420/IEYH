package HOLICOP;

import java.io.IOException;
import java.net.DatagramPacket;

public class Employe extends Personne
{

    public Employe(String identifiant,String addresse_chat,int port)
    {
        super(identifiant,addresse_chat,port);
    }
    
    public void answer_Question(String Question)
    {
        try
        {
            String chaine = "2@" + Question;
            DatagramPacket dtg = new DatagramPacket(chaine.getBytes(), chaine.length(),adresseGroupe, port_chat);
            socketGroupe.send(dtg);
        } 
        catch (IOException ex)
        {
            System.err.println("Employe - Erreur: " + ex.getMessage());
        }
    }
}
