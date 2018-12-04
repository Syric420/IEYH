/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_ieyhchat;

import GUI.ServForm;

/**
 *
 * @author Mika
 */
public class Serveur_IeyhChat
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Lancement du seveur chat");
        ServForm sf = new ServForm();
        sf.setVisible(true);
    }
}
