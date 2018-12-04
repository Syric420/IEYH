package Thread;

import HOLICOP.Verify;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;

public class ThreadReception extends Thread
{
    String nom;
    MulticastSocket socketGroupe;
    JList<String> LMsgRecus;
    boolean client;
    javax.swing.JComboBox<String> jComboBoxQuestion;
    
    public ThreadReception (String n, boolean bool, MulticastSocket ms, JList<String> l, JComboBox<String> jComboBox)
    {
        nom = n;
        socketGroupe = ms; 
        LMsgRecus = l;
        client = bool;
        jComboBoxQuestion = jComboBox;
        if(!bool)
        {
            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) jComboBoxQuestion.getModel();
            dcbm.addElement("Post Event");
        }
    }
    
    public void run()
    {
        boolean enMarche = true;
        while (enMarche)
        {
            try
            {
                byte[] buf = new byte[1000];
                DatagramPacket dtg = new DatagramPacket(buf, buf.length);
                socketGroupe.receive(dtg);
                System.out.println("reception" + new String (buf));
                String str[] = (new String (buf)).trim().split("@");
                DefaultListModel dlm;

                switch (str[0])
                {
                    case "1": //employé reçoit question - POST_QUESTION
                        if(!client)
                        {
                            Verify ver = new Verify();
                            ver.setMD(str[1]);
                            
                            byte[] tmp;
                            int cpt=0;
                            int debut=0,fin=0;
                            for(int i = 0; i<buf.length;i++)
                            {
                                if(buf[i]==64)
                                    cpt++;
                                else
                                    if(cpt>1 && debut ==0)
                                        debut=i;
                                if(buf[i] ==0)
                                {
                                    fin=i;
                                    break;
                                }      
                            }
                            
                            tmp = Arrays.copyOfRange(buf, debut, fin);
                            
                            if(ver.checkDigest(tmp))
                            {
                                System.out.println("OK");
                                DefaultComboBoxModel dcbm = (DefaultComboBoxModel)jComboBoxQuestion.getModel();
                                dcbm.addElement(str[1]);
                                dlm = (DefaultListModel) LMsgRecus.getModel();
                                dlm = (DefaultListModel<String>) LMsgRecus.getModel();
                                dlm.addElement(str[1]);
                            }
                            else 
                                System.out.println("Not OK");
                        }
                        break;

                    case "2": //si client -> reçoit réponse -- si employé -> supprime question de ComboBox -- ANSWER_QUESTION
                        if(!client)
                        {
                            //supprime dans ComboBox
                            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) jComboBoxQuestion.getModel();
                            dcbm.removeElement(str[1]);
                        }
                        
                        //affiche message au client + employé
                        dlm = (DefaultListModel)LMsgRecus.getModel();
                        dlm = (DefaultListModel<String>)LMsgRecus.getModel();
                        System.out.println("str0 :" + str[0] + "str1 :" + str[1] + "str2 :" + str[2]);
                        dlm.addElement("@"+ str[1] + "  " +str[2]);                            
                        break;

                    case "3": //ajout message simple dans la list - POST_EVENT
                        dlm = (DefaultListModel) LMsgRecus.getModel();
                        dlm = (DefaultListModel<String>) LMsgRecus.getModel();
                        dlm.addElement(str[1]);
                        break;
                }
            }
            catch (IOException ex)
            {
                System.err.println("ThreadReception - Erreur: " + ex.getMessage());
                enMarche = false;
            }
        }
    }
}