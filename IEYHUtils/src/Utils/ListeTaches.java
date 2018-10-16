package Utils;

import Interfaces.SourceTaches;
import java.util.*;

public class ListeTaches implements SourceTaches
{
    private LinkedList listeTaches;
    public ListeTaches()
    {
        listeTaches = new LinkedList();
    }
    public synchronized Runnable getTache() throws InterruptedException
    {
        System.out.println("ListeTaches: getTache avant wait");
        while (!existTaches()) wait();
        return (Runnable)listeTaches.remove();
    }
    public synchronized boolean existTaches()
    {
        return !listeTaches.isEmpty();
    }
    public synchronized void recordTache (Runnable r)
    {
        listeTaches.addLast(r);
        System.out.println("ListeTaches: tache dans la file");
        notify();
    }
}