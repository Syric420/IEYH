package Threads;

import Interfaces.Requete;
import Interfaces.SourceTaches;
import Interfaces.ConsoleServeur;
import java.io.*;
import java.net.*;
import java.util.*;
import Database.facility.BeanBD;

public class ThreadSer extends Thread
{
    private final int port, nbMaxCli;
    private SourceTaches tachesAExecuter;
    private ConsoleServeur guiApplication;
    private ServerSocket SSocket = null;
    private BeanBD BD; 
    
    public ThreadSer(int p, int n, SourceTaches st, ConsoleServeur cs, BeanBD bd)
    {
        port = p;
        nbMaxCli = n;
        tachesAExecuter = st; 
        guiApplication = cs;
        BD = bd;
    }
    
    public void run()
    {
        System.out.println("ThreadSer: Lancement du thread serveur");
        
        try
        {
            SSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.err.println("ThreadSer: Erreur de port d'écoute ! ? [" + e + "]"); System.exit(1);
        }
        
        // Démarrage du pool de threads
        System.out.println("ThreadSer: Lancement du pool de threads");
        for (int i=0; i<nbMaxCli; i++)
        {
            ThreadCli thr = new ThreadCli(tachesAExecuter, "Thread du pool n°" + String.valueOf(i));
            thr.start();
            System.out.println("ThreadSer: Thread " + i + " lancé");
        }
        
        // Mise en attente du serveur
        Socket CSocket = null;
        while (!isInterrupted())
        {
            try
            {
                guiApplication.TraceEvenements("serveur#Serveur en attente#" + this.getClass());
                CSocket = SSocket.accept();
                guiApplication.TraceEvenements("client" + CSocket.getRemoteSocketAddress().toString() + "#Client connecté#" + this.getClass());
            }
            catch (IOException e)
            {
                System.err.println("ThreadSer: Erreur d'accept ! ? [" + e.getMessage() + "]"); System.exit(1);
            }
            ObjectInputStream ois=null;
            ObjectOutputStream oos=null;
            Requete req = null;
            try
            {
                oos = new ObjectOutputStream(CSocket.getOutputStream());
                ois = new ObjectInputStream(CSocket.getInputStream());
                req = (Requete)ois.readObject();
                guiApplication.TraceEvenements("serveur#Requete lue par le serveur, instance de " + req.getClass().getName() + "#" + this.getClass());
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Erreur de def de classe [" + e.getMessage() + "]");
            }
            catch (IOException e)
            {
                System.err.println("Erreur ? [" + e.getMessage() + "]");
            }
            Runnable travail = req.createRunnable(CSocket, guiApplication, oos, ois, BD);
            if (travail != null)
            {
                tachesAExecuter.recordTache(travail);
                guiApplication.TraceEvenements("serveur#Travail mis dans la file#" + this.getClass());
            }
            else 
                guiApplication.TraceEvenements("serveur#Travail non mis dans la file#" + this.getClass());
        }   
    }
}
