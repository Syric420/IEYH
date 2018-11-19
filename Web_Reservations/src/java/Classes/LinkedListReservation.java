/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.LinkedList;

/**
 *
 * @author Vince
 */
public class LinkedListReservation {
    private LinkedList<Reservation> list;

    public LinkedListReservation() {
        list = null;
    }

    public LinkedListReservation(LinkedList<Reservation> list) {
        this.list = list;
    }
    
    /**
     * @return the Reservation
     */
    public LinkedList getReservation() {
        return list;
    }

    /**
     * @param Reservation the Reservation to set
     */
    public void setReservation(LinkedList Reservation) {
        this.list = Reservation;
    }
    
    
}
