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
public class MessageRecherche implements Message
{
    private String champ;
    private String valeur;

    public MessageRecherche(String c, String v)
    {
        champ = c;
        valeur = v;
    }

    /**
     * @return the champ
     */
    public String getChamp()
    {
        return champ;
    }

    /**
     * @param champ the champ to set
     */
    public void setChamp(String champ)
    {
        this.champ = champ;
    }

    /**
     * @return the valeur
     */
    public String getValeur()
    {
        return valeur;
    }

    /**
     * @param valeur the valeur to set
     */
    public void setValeur(String valeur)
    {
        this.valeur = valeur;
    }
}
