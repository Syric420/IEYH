package Interfaces;

import Database.facility.BeanBD;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public interface Requete
{
    public Runnable createRunnable (Socket s, ConsoleServeur cs, ObjectOutputStream oos, ObjectInputStream ois, BeanBD beanBD);
}