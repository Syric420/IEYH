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
public class MessagePaiementClient implements Message {
    
    private byte[] carteDeCreditByte;
    private byte[] cryptogrammeByte;
    private byte[] montantByte;

    public MessagePaiementClient() {
        this.carteDeCreditByte = null;
        this.cryptogrammeByte = null;
        this.montantByte = null;
    }

    public MessagePaiementClient(byte[] carteDeCreditByte, byte[] cryptogrammeByte, byte[] montantByte) {
        this.carteDeCreditByte = carteDeCreditByte;
        this.cryptogrammeByte = cryptogrammeByte;
        this.montantByte = montantByte;
    }
    
    

    /**
     * @return the carteDeCreditByte
     */
    public byte[] getCarteDeCreditByte() {
        return carteDeCreditByte;
    }

    /**
     * @param carteDeCreditByte the carteDeCreditByte to set
     */
    public void setCarteDeCreditByte(byte[] carteDeCreditByte) {
        this.carteDeCreditByte = carteDeCreditByte;
    }

    /**
     * @return the cryptogrammeByte
     */
    public byte[] getCryptogrammeByte() {
        return cryptogrammeByte;
    }

    /**
     * @param cryptogrammeByte the cryptogrammeByte to set
     */
    public void setCryptogrammeByte(byte[] cryptogrammeByte) {
        this.cryptogrammeByte = cryptogrammeByte;
    }

    /**
     * @return the montantByte
     */
    public byte[] getMontantByte() {
        return montantByte;
    }

    /**
     * @param montantByte the montantByte to set
     */
    public void setMontantByte(byte[] montantByte) {
        this.montantByte = montantByte;
    }

    
}
