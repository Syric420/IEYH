package HOLICOP;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Identify implements Serializable
{
    private String login;
    private String password;
    private long temps;
    private double alea;
    private byte[] msgD;
    
    public Identify()
    {
        Security.addProvider(new BouncyCastleProvider());
        login = null;
        password = null;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public byte[] getMd()
    {
        return msgD;
    }

    public void setMd()
    { 
        try
        {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(login.getBytes());
            md.update(password.getBytes());
            temps= (new Date()).getTime();
            alea = Math.random();
            //System.out.println("SetMD " + login +";" + password + ";" + temps + ";" + alea);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeLong(getTemps());
            bdos.writeDouble(getAlea());

            md.update(baos.toByteArray());
            msgD= md.digest();
            //System.out.println(login + " " + password + " " + msgD);
            
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex)
        {
            System.err.println("Identify - Erreur: " + ex.getMessage());
        }
    }
        
    public void setMd(String U,String Pass, long T, double A)
    { 
        try
        {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1", "BC");
            //System.out.println("SetMD " + U +";" + Pass + ";" + T + ";" + A);
            md.update(U.getBytes());
            md.update(Pass.getBytes());
            temps= T;
            alea = A;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeLong(getTemps());
            bdos.writeDouble(getAlea());

            md.update(baos.toByteArray());
            msgD= md.digest();
            //System.out.println(U + " " + Pass + " " + msgD);
            
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex)
        {
            System.err.println("Identify - Erreur: " + ex.getMessage());
        }
    }

    /**
     * @return the temps
     */
    public long getTemps()
    {
        return temps;
    }

    /**
     * @return the alea
     */
    public double getAlea()
    {
        return alea;
    }
}
