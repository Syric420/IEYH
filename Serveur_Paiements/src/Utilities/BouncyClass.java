/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



/**
 *
 * @author Vince
 */
public class BouncyClass {

    
    public static byte[] prepareSaltDigest(String mdp, long temps, double alea)
    {
        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(mdp.getBytes());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeLong(temps); bdos.writeDouble(alea);
            
            md.update(baos.toByteArray());
            
            digest = md.digest();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }
    
    public static boolean isSaltDigestEqual(byte [] digest1, byte [] digest2)
    {
        
        if (MessageDigest.isEqual(digest1, digest2) )
        {  
            return true;
        }
        else
            return false;
    }
    
    public static byte[] encryptRSA(PublicKey cléPublique, byte [] texte)
    {
        try {
            Cipher chiffrement = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            chiffrement.init(Cipher.ENCRYPT_MODE, cléPublique);
            byte[] texteCrypté = chiffrement.doFinal(texte);
            return texteCrypté;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static byte[] decryptRSA(PrivateKey cléPrivée, byte [] texteCrypté)
    {
        try {
            Cipher chiffrement = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            chiffrement.init(Cipher.DECRYPT_MODE, cléPrivée);
            byte[] texteDecrypte = chiffrement.doFinal(texteCrypté);
            return texteDecrypte;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
