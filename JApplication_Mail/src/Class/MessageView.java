/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 *
 * @author Vince
 */
public class MessageView {
    private Message message;

    public MessageView() {
        message = null;
    }

    public MessageView(Message message) {
        this.message = message;
    }

    
    
    
    @Override
    public String toString() {
        try {
            if(message!=null)
                //return message.getFrom()[0] + " : " +message.getSubject();  
                //return message.getFrom()[0].toString();  
            return message.getSubject();
            
        } catch (MessagingException ex) {
            Logger.getLogger(MessageView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Erreur";
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
    
}
