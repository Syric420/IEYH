/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author Mika
 */
public class MessageDesistement implements Message
{
    private String id;
    private String date;
    private String client;

    public MessageDesistement(String i, String d, String c)
    {
        id = i;
        date = d;
        client = c;
    }
    
    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * @return the client
     */
    public String getClient()
    {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(String client)
    {
        this.client = client;
    }
}
