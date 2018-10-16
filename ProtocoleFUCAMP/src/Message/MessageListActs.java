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
 * @author Mika
 */
public class MessageListActs implements Message
{
    private LinkedList<Vector> list;

    public MessageListActs(LinkedList<Vector> l)
    {
        list = l;    
    }

    /**
     * @return the list
     */
    public LinkedList<Vector> getList()
    {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(LinkedList<Vector> list)
    {
        this.list = list;
    }
}
