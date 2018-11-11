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
public class MessageInscription implements Message
{
    private String type;
    private String id;
    private String prix;
    private String date;
    private String client;
    private String dureeJour;

    public MessageInscription(String t, String i, String p, String d, String c, String dj)
    {
        type = t;
        id = i;
        prix = p;
        date = d;
        client = c;
        dureeJour = dj;
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
     * @return the prix
     */
    public String getPrix()
    {
        return prix;
    }

    /**
     * @param prix the prix to set
     */
    public void setPrix(String prix)
    {
        this.prix = prix;
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

    /**
     * @return the dureeJour
     */
    public String getDureeJour()
    {
        return dureeJour;
    }

    /**
     * @param dureeJour the dureeJour to set
     */
    public void setDureeJour(String dureeJour)
    {
        this.dureeJour = dureeJour;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

}
