/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author Vince
 */

public class PieceAttachee implements Serializable {
    public static final int IMAGE = 1;
    public static final int DIGEST = 2;
    public static final int PIECE_ATTACHEE = 3;
    
    private int type;
    private String fileName;
    private String pathFile;
    private String texteClair;
    private byte[] digest;
    private ByteArrayOutputStream baos;
    
    public PieceAttachee(int type, String texteClair)
    {
        this.type = type;
        this.texteClair = texteClair;
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public PieceAttachee(int type, ByteArrayOutputStream ba, String fn) 
    {
            this.type = type;
            this.baos = ba;
            this.fileName = fn;
    }

    public PieceAttachee(int type, String path, String fn) 
    {
            this.type = type;
            this.pathFile = path;
            this.fileName = fn;
    }
    
    public String createDigest()
    {
        try {
            MimeBodyPart msgBP = new MimeBodyPart();
            MessageDigest md = MessageDigest.getInstance("MD5", "BC");
            md.update(texteClair.getBytes());// Ajout du texte dans le digest
            digest = md.digest();//Création du digest avec padding si besoin
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<digest.length;i++) {
                    String hex=Integer.toHexString(0xff & digest[i]);
                    if(hex.length()==1) hexString.append('0');
                    hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PieceAttachee.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.security.NoSuchProviderException ex) {
            Logger.getLogger(PieceAttachee.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public MimeBodyPart createMsgBodyPartImage()
    {
        try {
            
            MimeBodyPart msgBP = new MimeBodyPart();
            
            System.out.println("Path = "+pathFile);
            DataSource so = new FileDataSource(pathFile);
            msgBP.setDataHandler (new DataHandler (so));
            msgBP.setFileName(fileName);
            return msgBP;
        } catch (MessagingException ex) {
            System.err.println("Message erreur : "+ex);
        }
        return null;
    }
    
    public void download(String path)
    {
        try {
            FileOutputStream fos =new FileOutputStream(path);
            baos.writeTo(fos);
            fos.close();
            System.out.println("Pièce attachée " + fileName + " récupérée");
        } catch (IOException ex) {
            Logger.getLogger(PieceAttachee.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return fileName;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the pathFile
     */
    public String getPathFile() {
        return pathFile;
    }

    /**
     * @param pathFile the pathFile to set
     */
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    /**
     * @return the baos
     */
    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    /**
     * @param baos the baos to set
     */
    public void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }
    
}
