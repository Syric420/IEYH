/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import java.util.LinkedList;

/**
 *
 * @author Vince
 */
public class MessageListString {
    private LinkedList<String> listString;

    public MessageListString(LinkedList<String> listString) {
        this.listString = listString;
    }
    
    public MessageListString() {
        this.listString = new LinkedList();
    }

    /**
     * @return the listString
     */
    public LinkedList<String> getListString() {
        return listString;
    }

    /**
     * @param listString the listString to set
     */
    public void setListString(LinkedList<String> listString) {
        this.listString = listString;
    }
}
