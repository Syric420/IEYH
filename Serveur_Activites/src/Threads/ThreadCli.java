package Threads;

import Interfaces.SourceTaches;
import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadCli extends Thread
{
    private Runnable tacheEnCours;
    private SourceTaches tachesAExecuter;
    private String name;
    private boolean fin;
    
    public ThreadCli(SourceTaches ts, String n)
    {
        tachesAExecuter = ts;
        name = n;
    }
    
    public void run()
    {
        
        while(!isInterrupted())
        {
            try
            {
                tacheEnCours = tachesAExecuter.getTache();
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption : " + e.getMessage());
            }
            tacheEnCours.run();
        }
    }
}
