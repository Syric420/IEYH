/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author Vince
 */
public class MessageInt implements Message {
    private int idReservation;

    public MessageInt() {
        idReservation = -1;
    }

    public MessageInt(int idReservation) {
        this.idReservation = idReservation;
    }

    
    /**
     * @return the idReservation
     */
    public int getIdReservation() {
        return idReservation;
    }

    /**
     * @param idReservation the idReservation to set
     */
    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }
    
}
