package HOLICOP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class Voyageur extends Personne
{

    public Voyageur(String identifiant,String addresse_chat,int port)
    {
        super(identifiant,addresse_chat,port);
    }
    
    public void post_Question(String Question)
    {
        //check digest
        try
        {
            String chaine = "1@" + Question +"@";
            Verify ver = new Verify();
            ver.setMD(Question);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( chaine.getBytes() );
            outputStream.write( ver.getMd());
            
            byte[] var =  outputStream.toByteArray();
            System.out.println("Voyageur" + new String (var));
            DatagramPacket dtg = new DatagramPacket(var , var.length,adresseGroupe, port_chat);
            socketGroupe.send(dtg);
            
        } 
        catch (IOException ex)
        {
            System.err.println("Voyageur - Erreur: " + ex.getMessage());
        }
    }
}
