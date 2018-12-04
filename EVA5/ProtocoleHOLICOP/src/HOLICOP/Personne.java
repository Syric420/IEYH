package HOLICOP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Personne
{
    
    String identifiant;
    protected int port_chat;
    protected InetAddress adresseGroupe;
    protected MulticastSocket socketGroupe;

    public Personne(String identifiant,String addresse_chat,int port)
    {
        try
        {
            System.out.println(identifiant+addresse_chat+port);
            this.identifiant = identifiant;
            adresseGroupe = InetAddress.getByName(addresse_chat);
            socketGroupe = new MulticastSocket(port);
            port_chat=port;
            socketGroupe.setTimeToLive(25);
            socketGroupe.joinGroup(adresseGroupe);
        }
        catch (UnknownHostException ex)
        {
            System.err.println("Personne - Erreur: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.err.println("Personne - Erreur: " + ex.getMessage());
        }
    }

    public String getIdentifiant()
    {
        return identifiant;
    }

    public void setIdentifiant(String identifiant)
    {
        this.identifiant = identifiant;
    }

    public MulticastSocket getSocketGroupe()
    {
        return socketGroupe;
    }
    
    
    public void post_Event(String event)
    {
        try
        {
            event=("3@" + event);
            DatagramPacket dtg = new DatagramPacket(event.getBytes(), event.length(),adresseGroupe, port_chat);
            socketGroupe.send(dtg);
        }
        catch (IOException ex)
        {
            System.err.println("Personne - Erreur: " + ex.getMessage());
        }
    }  
}
