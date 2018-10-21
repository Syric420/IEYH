/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import java.sql.Date;

/**
 *
 * @author Vince
 */
public class MessageBooking implements Message {
    private int refChambre;
    private Date dateArrivee;
    private Date dateDepart;
    private String nomRef;
    private String prenomRef;
    private float prixHTVA;

    public MessageBooking(int refChambre, Date dateArrivee, Date dateDepart, String nomRef, String prenomRef, float prix) {
        this.refChambre = refChambre;
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.nomRef = nomRef;
        this.prenomRef = prenomRef;
        this.prixHTVA = prix;
    }

    /**
     * @return the dateArrivee
     */
    public Date getDateArrivee() {
        return dateArrivee;
    }

    /**
     * @param dateArrivee the dateArrivee to set
     */
    public void setDateArrivee(Date dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    /**
     * @return the nomRef
     */
    public String getNomRef() {
        return nomRef;
    }

    /**
     * @param nomRef the nomRef to set
     */
    public void setNomRef(String nomRef) {
        this.nomRef = nomRef;
    }

    /**
     * @return the prenomRef
     */
    public String getPrenomRef() {
        return prenomRef;
    }

    /**
     * @param prenomRef the prenomRef to set
     */
    public void setPrenomRef(String prenomRef) {
        this.prenomRef = prenomRef;
    }

    /**
     * @return the dateDepart
     */
    public Date getDateDepart() {
        return dateDepart;
    }

    /**
     * @param dateDepart the dateDepart to set
     */
    public void setDateDepart(Date dateDepart) {
        this.dateDepart = dateDepart;
    }

    /**
     * @return the refChambre
     */
    public int getRefChambre() {
        return refChambre;
    }

    /**
     * @param refChambre the refChambre to set
     */
    public void setRefChambre(int refChambre) {
        this.refChambre = refChambre;
    }

    /**
     * @return the prixHTVA
     */
    public float getPrixHTVA() {
        return prixHTVA;
    }

    /**
     * @param prixHTVA the prixHTVA to set
     */
    public void setPrixHTVA(float prixHTVA) {
        this.prixHTVA = prixHTVA;
    }
    
    
    
}
