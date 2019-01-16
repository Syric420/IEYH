/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import Utilities.BouncyClass;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.Arrays;

/**
 *
 * @author Vince
 */
public class MessageCryptedWithSignature implements Message {
    
    private byte[] texteCrypted;
    private byte[] signature;

    public MessageCryptedWithSignature() {
        this.texteCrypted = null;
        this.signature = null;
    }

    public MessageCryptedWithSignature(byte[] texteCrypted, byte[] signature) {
        this.texteCrypted = texteCrypted;
        this.signature = signature;
    }

    /**
     * @return the texteCrypted
     */
    public byte[] getTexteCrypted() {
        return texteCrypted;
    }

    /**
     * @param texteCrypted the texteCrypted to set
     */
    public void setTexteCrypted(byte[] texteCrypted) {
        this.texteCrypted = texteCrypted;
    }

    /**
     * @return the signature
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
    
}
