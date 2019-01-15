/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author Vince
 */
public class MessageLoginDigest implements Message
{

  
    private String username;
    private double alea;
    private long temps;
    private byte [] digest;

    public MessageLoginDigest(String u, double a, long t, byte [] d)
    {
        this.username = u;
        this.alea = a;
        this.temps = t;
        this.digest = d;
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
     * @return the alea
     */
    public double getAlea() {
        return alea;
    }

    /**
     * @param alea the alea to set
     */
    public void setAlea(double alea) {
        this.alea = alea;
    }

    /**
     * @return the temps
     */
    public long getTemps() {
        return temps;
    }

    /**
     * @param temps the temps to set
     */
    public void setTemps(long temps) {
        this.temps = temps;
    }

    /**
     * @return the digest
     */
    public byte[] getDigest() {
        return digest;
    }

    /**
     * @param digest the digest to set
     */
    public void setDigest(byte[] digest) {
        this.digest = digest;
    }
}
