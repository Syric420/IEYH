package Class;

import Interfaces.Reponse;
import Message.Message;
import java.io.*;

public class ReponseSPAYMAP implements Reponse, Serializable
{
    public static int SUCCESS = 1;
    public static int FAILED = 2;
    
    private int code;
    private String chargeUtile;
    private Message message;
    private byte [] messageCrypted;
    
    public ReponseSPAYMAP(int code)
    {
        this.code = code;
        
    }
    
    public ReponseSPAYMAP(int code, byte [] messageCrypt)
    {
        this.code = code;
        this.messageCrypted = messageCrypt;
    }
    
    public ReponseSPAYMAP(int code, String cu)
    {
        this.code = code;
        this.chargeUtile = cu;
    }

    public ReponseSPAYMAP(int code, Message message) {
        this.code = code;
        this.message = message;
    }

    public ReponseSPAYMAP(int code, String chargeUtile, Message message) {
        this.code = code;
        this.chargeUtile = chargeUtile;
        this.message = message;
    }
    
    
    @Override
    public int getCode() 
    {
        return code;
    }

    /**
     * @return the chargeUtile
     */
    public String getChargeUtile() {
        return chargeUtile;
    }

    /**
     * @param chargeUtile the chargeUtile to set
     */
    public void setChargeUtile(String chargeUtile) {
        this.chargeUtile = chargeUtile;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * @return the messageCrypted
     */
    public byte[] getMessageCrypted() {
        return messageCrypted;
    }

    /**
     * @param messageCrypted the messageCrypted to set
     */
    public void setMessageCrypted(byte[] messageCrypted) {
        this.messageCrypted = messageCrypted;
    }
}
