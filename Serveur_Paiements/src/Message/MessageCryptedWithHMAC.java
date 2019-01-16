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
public class MessageCryptedWithHMAC implements Message {
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vince
 */
    private byte[] texteCrypted;
    private byte[] hmac;

    public MessageCryptedWithHMAC() {
        this.texteCrypted = null;
        this.hmac = null;
    }

    public MessageCryptedWithHMAC(byte[] texteCrypted, byte[] hmac) {
        this.texteCrypted = texteCrypted;
        this.hmac = hmac;
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
    public byte[] getHmac() {
        return hmac;
    }

    /**
     * @param signature the signature to set
     */
    public void setHmac(byte[] hmac) {
        this.hmac = hmac;
    }
    
}
