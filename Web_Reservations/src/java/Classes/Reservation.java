/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.sql.Date;

/**
 *
 * @author Vince
 */
public class Reservation {
    private int idReservation;
    private String typeReservation;
    private java.sql.Date dateDebut;
    private java.sql.Date dateFin;
    private float prixNet;
    private boolean paye;
    private int refChambre;

    public Reservation() {
        idReservation = -1;
        typeReservation = null;
        dateDebut = null;
        dateFin = null;
        prixNet = 0;
        paye = false;
        refChambre = 0;
    }

    public Reservation(int idReservation, String typeReservation, Date dateDebut, Date dateFin, float prixNet, boolean paye, int refChambre) {
        this.idReservation = idReservation;
        this.typeReservation = typeReservation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixNet = prixNet;
        this.paye = paye;
        this.refChambre = refChambre;
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

    /**
     * @return the typeReservation
     */
    public String getTypeReservation() {
        return typeReservation;
    }

    /**
     * @param typeReservation the typeReservation to set
     */
    public void setTypeReservation(String typeReservation) {
        this.typeReservation = typeReservation;
    }

    /**
     * @return the dateDebut
     */
    public java.sql.Date getDateDebut() {
        return dateDebut;
    }

    /**
     * @param dateDebut the dateDebut to set
     */
    public void setDateDebut(java.sql.Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * @return the dateFin
     */
    public java.sql.Date getDateFin() {
        return dateFin;
    }

    /**
     * @param dateFin the dateFin to set
     */
    public void setDateFin(java.sql.Date dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * @return the prixNet
     */
    public float getPrixNet() {
        return prixNet;
    }

    /**
     * @param prixNet the prixNet to set
     */
    public void setPrixNet(float prixNet) {
        this.prixNet = prixNet;
    }

    /**
     * @return the paye
     */
    public boolean isPaye() {
        return paye;
    }

    /**
     * @param paye the paye to set
     */
    public void setPaye(boolean paye) {
        this.paye = paye;
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
    
    
}
