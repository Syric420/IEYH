/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SNMP;

import Graphique.ServeurManagement;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 *
 * @author Vince
 */
public class SnmpListener implements ResponseListener
{
    private Snmp snmpManager;
    private ServeurManagement gui;
    public SnmpListener (Snmp s, ServeurManagement g)
    {
        snmpManager = s;
        gui = g;
    }
    
    @Override
    public void onResponse(ResponseEvent event)
    {
        
            ((Snmp)event.getSource()).cancel(event.getRequest(), this);
            System.out.println("Réponse reçue (PDU): "+event.getResponse());

            PDU rep = event.getResponse();
            if(rep != null)
            {
                int nValues = rep.size();
                for (int i=0; i<nValues; i++)
                {
                    VariableBinding vb = rep.get(i);
                    Variable value = vb.getVariable();
                    gui.modelJlist.addElement(vb);
                }
            }
            else
                JOptionPane.showMessageDialog(gui, "Erreur : Pas de réponse", "Serveur_Control", JOptionPane.ERROR_MESSAGE);
        
    }
}
            
