package Threads;

import Interfaces.Requete;
import Interfaces.ConsoleServeur;
import java.io.*;
import java.net.*;
import Database.facility.BeanBD;

public class ThreadSerAdmin extends Thread
{
    private final int port;
    private ConsoleServeur guiApplication;
    private ServerSocket SSocket = null;
    private BeanBD BD; 
    
    public ThreadSerAdmin(int p, ConsoleServeur cs, BeanBD bd)
    {
        port = p;
        guiApplication = cs;
        BD = bd;
    }
    
    public void run()
    {
        System.out.println("ThreadSerAdmin: Lancement du thread serveur");
        
        try
        {
            SSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.err.println("ThreadSerAdmin: Erreur de port d'écoute ! ? [" + e + "]"); System.exit(1);
        }
        
        // Mise en attente du serveur
        Socket CSocket = null;
        while (!isInterrupted())
        {
            try
            {
                guiApplication.TraceEvenements("serveur#Serveur admin en attente#" + this.getClass());
                CSocket = SSocket.accept();
                guiApplication.TraceEvenements(CSocket.getRemoteSocketAddress().toString() + "#Client connecté#" + this.getClass());
            }
            catch (IOException e)
            {
                System.err.println("ThreadSerAdmin: Erreur d'accept ! ? [" + e.getMessage() + "]"); System.exit(1);
            }
            ObjectInputStream ois=null;
            ObjectOutputStream oos=null;
            Requete req = null;
            try
            {
                oos = new ObjectOutputStream(CSocket.getOutputStream());
                ois = new ObjectInputStream(CSocket.getInputStream());
                req = (Requete)ois.readObject();
                guiApplication.TraceEvenements("serveur#Requete lue par le serveur admin, instance de " + req.getClass().getName() + "#" + this.getClass());
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Erreur de def de classe [" + e.getMessage() + "]");
            }
            catch (IOException e)
            {
                System.err.println("Erreur ? [" + e.getMessage() + "]");
            }
            Runnable createRunnable = req.createRunnable(CSocket, guiApplication, oos, ois, BD, null);
            if(createRunnable != null)
                createRunnable.run();
        }   
    }
}
