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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;



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
    
    public static byte[] encryptDES( SecretKey key, byte [] texteClair) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            return cipher.doFinal(texteClair);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static byte[] decryptDES(SecretKey key, byte [] texteCrypté) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(texteCrypté);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static byte [] prepareSignature(PrivateKey cléPrivée, byte [] byteArray)
    {
        try {
            Signature s = Signature. getInstance("SHA1withRSA","BC");
            System.out.println("Initialisation de la signature");
            s.initSign(cléPrivée);
            System.out.println("Hachage du message");
            s.update(byteArray);
            System.out.println("Generation des bytes");
            byte[] signature = s.sign();
            
            return signature;
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static boolean verifySignature(PublicKey cléPublique, byte [] signature, byte [] message )
    {
        try {
            // confection d'une signature locale
            Signature s = Signature.getInstance ("SHA1withRSA", "BC");
            s.initVerify(cléPublique);
            System.out.println("Hachage du message");
            s.update(message);
            System.out.println("Verification de la signature construite");
            
            return s.verify(signature);
            
            
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(BouncyClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
