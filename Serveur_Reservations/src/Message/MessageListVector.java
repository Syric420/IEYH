/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author Vince
 */
public class MessageListVector implements Message {
    private LinkedList<Vector> listVec;

    public MessageListVector() {
        listVec = new LinkedList<Vector>();
    }

    public MessageListVector(LinkedList<Vector> listVec) {
        this.listVec = listVec;
    }

    
    
    /**
     * @return the vecRoom
     */
    public LinkedList<Vector> getListVector() {
        return listVec;
    }

    /**
     * @param vecRoom the vecRoom to set
     */
    public void setListVector(LinkedList<Vector> listRoom) {
        this.listVec = listRoom;
    }
    
    
}
