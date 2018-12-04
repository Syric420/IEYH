package HOLICOP;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Verify
{
    private MessageDigest md;
    private byte[] msgD;
    
    public Verify()
    {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public byte[] getMd()
    {
        return msgD;
    }
    
    public void setMD(String str)
    { 
        try
        {
            md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(str.getBytes());
            msgD = md.digest();          
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex)
        {
            System.err.println("Verify - Erreur: " + ex.getMessage());
        }
    }
    
    public boolean checkDigest(byte[] b)
    {
        return MessageDigest.isEqual(msgD, b);
    }
}
