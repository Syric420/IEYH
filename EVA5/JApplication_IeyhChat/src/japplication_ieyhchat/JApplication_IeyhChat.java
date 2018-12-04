/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package japplication_ieyhchat;

import GUI.Chat;

/**
 *
 * @author Mika
 */
public class JApplication_IeyhChat
{
    public static void main(String[] args)
    {
        System.out.println("Lancement de l'application chat");
        Chat c = new Chat();
        c.setVisible(true);
    }
}
