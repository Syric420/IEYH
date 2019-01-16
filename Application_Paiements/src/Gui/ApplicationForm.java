/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Message.MessageListVector;
import Utilities.ReadProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.swing.table.DefaultTableModel;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import Class.*;

/**
 *
 * @author Vince
 */
public class ApplicationForm extends javax.swing.JFrame {

    Login login = null;
    PaiementDialog pd = null;
    FactureDialog fd = null;
    
    Socket socket;
    int port;
    String adresseIP;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    private PrivateKey cléPrivée;
    private PublicKey cléPublique;
    private PublicKey cléPubliqueServeur;
    private SecretKey cléSym;
    private SecretKey cléSymHMAC;
    RequeteSPAYMAP req = null;
    
    /**
     * Creates new form ApplicationForm
     */
    public ApplicationForm() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            initComponents();
            preparejTable();
            //Connexion  au serveur
            ReadProperties rp = new ReadProperties("/Utilities/config.properties");
            
            port = Integer.parseInt(rp.getProp("PORT_PAY"));
            adresseIP = rp.getProp("ADRESSE_IP_SERV");
            socket = new Socket(adresseIP, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            //----------------------
            
            login = new Login(this,true, socket, oos, ois);
            pd = new PaiementDialog(this, true, oos, ois);
            fd = new FactureDialog(this, true);
            login.setVisible(true);
            
        } catch (IOException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }
    
    public void preparejTable()
    {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {
                        
                    },
                    new String [] {
                        "idReservation", "typeReservation", "DateDébut", "DateFin", "Prix total", "Prix déjà payé"
                    }
            ) {
                Class[] types = new Class [] {
                    java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
    }
    
    public void chargeReservation() {
        //Cette méthode permet de demander la liste des chambres et de l'afficher dans une jtable
        req = new RequeteSPAYMAP(RequeteSPAYMAP.REQUEST_LISTRESERV);
        if(oos!=null)
        {
            try {
                oos.writeObject(req);
                
                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                dtm.setRowCount(0);
                System.out.println("Req envoyée");
                //Une fois la requête envoyée, le serveur va nous renvoyer une linkedlist de vector
                
                MessageListVector mlv = (MessageListVector)ois.readObject();
                
                
                //System.out.println("Taille = "+mlr.getListVector().size());
                for(int i=0; i<mlv.getListVector().size();i++)
                {
                    dtm.addRow(mlv.getListVector().get(i));
                }
                
            } catch (IOException ex) {
                System.out.println("Test");
                Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Application Paiements");
        setResizable(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Payer");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(290, 290, 290))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(jTable1.getSelectedRow()!= -1)
        {
            int ligne = jTable1.getSelectedRow();
            pd.idReservation = jTable1.getValueAt(ligne, 0).toString();
            pd.jLabel_sommeTotalAPayer.setText(jTable1.getValueAt(ligne, 4).toString());
            pd.jLabel_sommeDejaPayee.setText(jTable1.getValueAt(ligne, 5).toString());
            pd.setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ApplicationForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ApplicationForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ApplicationForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ApplicationForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApplicationForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the cléPrivée
     */
    public PrivateKey getCléPrivée() {
        return cléPrivée;
    }

    /**
     * @param cléPrivée the cléPrivée to set
     */
    public void setCléPrivée(PrivateKey cléPrivée) {
        this.cléPrivée = cléPrivée;
    }

    /**
     * @return the cléPublique
     */
    public PublicKey getCléPublique() {
        return cléPublique;
    }

    /**
     * @param cléPublique the cléPublique to set
     */
    public void setCléPublique(PublicKey cléPublique) {
        this.cléPublique = cléPublique;
    }

    /**
     * @return the cléSym
     */
    public SecretKey getCléSym() {
        return cléSym;
    }

    /**
     * @param cléSym the cléSym to set
     */
    public void setCléSym(SecretKey cléSym) {
        this.cléSym = cléSym;
    }

    /**
     * @return the cléSymHMAC
     */
    public SecretKey getCléSymHMAC() {
        return cléSymHMAC;
    }

    /**
     * @param cléSymHMAC the cléSymHMAC to set
     */
    public void setCléSymHMAC(SecretKey cléSymHMAC) {
        this.cléSymHMAC = cléSymHMAC;
    }

    /**
     * @return the cléPubliqueServeur
     */
    public PublicKey getCléPubliqueServeur() {
        return cléPubliqueServeur;
    }

    /**
     * @param cléPubliqueServeur the cléPubliqueServeur to set
     */
    public void setCléPubliqueServeur(PublicKey cléPubliqueServeur) {
        this.cléPubliqueServeur = cléPubliqueServeur;
    }
}
