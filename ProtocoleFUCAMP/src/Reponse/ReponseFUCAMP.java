package Reponse;

import Interfaces.Reponse;
import Message.Message;
import java.io.*;

public class ReponseFUCAMP implements Reponse, Serializable
{
    public static int SUCCESS = 1;
    public static int FAILED = 2;
    
    private int code;
    private Message message;
    
    public Message getMessage()
    {
        return message;
    }
    
    public ReponseFUCAMP(int code)
    {
        this.code = code;
    }
    
    public ReponseFUCAMP(int code, Message message)
    {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public int getCode() 
    {
        return code;
    }
}
