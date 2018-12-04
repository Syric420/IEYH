/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import HOLICOP.Identify;

/**
 *
 * @author Mika
 */
public class MessageLogin implements Message
{
    public static int LOGIN_VOYAGEUR = 1;
    public static int LOGIN_EMPLOYE = 2;
    
    private String username;
    
    private int typeMessage;
    private Identify msgD;
    private int port_chat;
    private String addresse_chat;
    
    public MessageLogin()
    {
        this.username = null;
        this.msgD = null;
    }
    
    public MessageLogin(String u)
    {
        this.username = u;
    }
    
    public MessageLogin(String u, Identify digest)
    {
        this.username = u;
        this.msgD = digest;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the typeMessage
     */
    public int getTypeMessage()
    {
        return typeMessage;
    }

    /**
     * @param typeMessage the typeMessage to set
     */
    public void setTypeMessage(int typeMessage)
    {
        this.typeMessage = typeMessage;
    }

    /**
     * @return the msgD
     */
    public Identify getMsgD()
    {
        return msgD;
    }

    /**
     * @param msgD the msgD to set
     */
    public void setMsgD(Identify msgD)
    {
        this.msgD = msgD;
    }

    /**
     * @return the port_chat
     */
    public int getPort_chat()
    {
        return port_chat;
    }

    /**
     * @param port_chat the port_chat to set
     */
    public void setPort_chat(int port_chat)
    {
        this.port_chat = port_chat;
    }

    /**
     * @return the addresse_chat
     */
    public String getAddresse_chat()
    {
        return addresse_chat;
    }

    /**
     * @param addresse_chat the addresse_chat to set
     */
    public void setAddresse_chat(String addresse_chat)
    {
        this.addresse_chat = addresse_chat;
    }
}
