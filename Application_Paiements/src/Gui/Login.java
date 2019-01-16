/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Class.ReponseSPAYMAP;
import Class.RequeteSPAYMAP;
import Message.MessageLoginDigest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Utilities.BouncyClass;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Mika
 */
public class Login extends javax.swing.JDialog
{
    private String username = "";
    private String password = "";
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private RequeteSPAYMAP req;
    private ReponseSPAYMAP rep;
    
            
    public Login(java.awt.Frame parent, boolean modal, Socket sock, ObjectOutputStream oos, ObjectInputStream ois)
    {
        super(parent, modal);
        initComponents();
        this.socket = sock;
        this.oos = oos;
        this.ois = ois;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonOK = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textUsername = new javax.swing.JTextField();
        textPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        buttonOK.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        buttonOK.setText("OK");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        jLabel3.setText("Username:");

        jLabel4.setText("Password:");

        textUsername.setText("mika");
        textUsername.setToolTipText("");

        textPassword.setText("1234");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(textUsername))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(textUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonOK)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonOKActionPerformed
    {//GEN-HEADEREND:event_buttonOKActionPerformed
        if(textUsername.getText().equals("") || textPassword.getText().equals(""))
            JOptionPane.showMessageDialog(null, "Remplissez les champs!");
        else
        {
            this.setUsername(textUsername.getText());
            this.setPassword(textPassword.getText());
            
            if(login(username, password)==false)
            {
                JOptionPane.showMessageDialog(this, "Erreur : "+rep.getChargeUtile());
            }
            else
            {
                //Login réussi
                //((ApplicationForm)this.getParent());
                handshake();
                ((ApplicationForm)this.getParent()).chargeReservation();
                this.setVisible(false);
                
            }
        }
    }//GEN-LAST:event_buttonOKActionPerformed

    private void handshake() 
    {
        try {
            KeyStore ks = null;
            ks = KeyStore.getInstance("JCEKS");
            ks.load(new FileInputStream("..\\Application_Paiements\\ApplicationPaiement.JCEKS"),
                    "123".toCharArray());
            
            //Récupération du certificat du client + clé publique qu'il y a dedans
            X509Certificate certif = (X509Certificate)ks.getCertificate("paiementrsa");
            ((ApplicationForm)this.getParent()).setCléPublique(certif.getPublicKey());
            System.out.println("Cle publique du certificat de ApplicationPaiement.JCEKS recuperée = "+((ApplicationForm)this.getParent()).getCléPublique().toString());
            
            //récupération clé privée
            ((ApplicationForm)this.getParent()).setCléPrivée((PrivateKey) ks.getKey("paiementrsa", "123".toCharArray()));
            System.out.println("Cle privee recuperee : " + ((ApplicationForm)this.getParent()).getCléPrivée().toString());
            
            //Envoi de la clé publique au serveur
            oos.writeObject(((ApplicationForm)this.getParent()).getCléPublique());
            
            //Reception de la clé publique du serveur
            ((ApplicationForm)this.getParent()).setCléPubliqueServeur((PublicKey)ois.readObject());
            
            //Lecture des deux clés symétriques cryptée que le serveur va envoyer au format byte []
            byte [] cléSymByteCrypted = (byte[])ois.readObject();
            byte [] cléSymHMACByteCrypted = (byte[])ois.readObject();
            
            //On les décrypte à l'aide de la clé privée du client
            byte [] cléSymByte = BouncyClass.decryptRSA(((ApplicationForm)this.getParent()).getCléPrivée(), cléSymByteCrypted);
            byte [] cléSymHMACByte = BouncyClass.decryptRSA(((ApplicationForm)this.getParent()).getCléPrivée(), cléSymHMACByteCrypted);
            
            //on convertit les clés qui sont en byte [] en clé normales pour avoir plus facile à les utiliser
            SecretKey cléSym = new SecretKeySpec(cléSymByte, 0, cléSymByte.length, "AES");
            SecretKey cléSymHMAC = new SecretKeySpec(cléSymHMACByte, 0, cléSymHMACByte.length, "AES");
            System.out.println("cléSym : " + cléSym.toString());
            System.out.println("cléSymHMAC : " + cléSymHMAC.toString());
            
            //Envoie à la gui ApplicationForm pour l'utiliser là bas
            ((ApplicationForm)this.getParent()).setCléSym(cléSym);
            ((ApplicationForm)this.getParent()).setCléSymHMAC(cléSymHMAC);
            
        } catch (KeyStoreException | ClassNotFoundException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        } 
        
        
    }
        
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        req = new RequeteSPAYMAP(RequeteSPAYMAP.REQUEST_LOGOUT);
        if(oos != null)
        {
            try {
                oos.writeObject(req);
                
                rep = (ReponseSPAYMAP) ois.readObject();
                
                if(rep.getCode() == ReponseSPAYMAP.SUCCESS)
                {
                    System.exit(0);
                }   
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur dans la deconnexion");
                    System.exit(0);
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    
    public boolean login(String user, String mdp)
    {
        try 
        {
            //on crée le nombre aléatoire + la date
            long temps = (new Date()).getTime();
            double alea = Math.random();    
            
            byte[] digest = BouncyClass.prepareSaltDigest(mdp, temps, alea);
            //Envoie du user et mdp au serveur
            MessageLoginDigest mlogin = new MessageLoginDigest(user, alea, temps, digest);
            req = new RequeteSPAYMAP(RequeteSPAYMAP.REQUEST_LOGIN, mlogin);
            if(oos !=null && ois!=null)
            {
                oos.writeObject(req);
                //Reception de la réponse
                rep = (ReponseSPAYMAP)ois.readObject();
                if(rep.getCode() == ReponseSPAYMAP.SUCCESS)
                {
                    //Login réussi
                    return true;
                }
                else
                {
                    //Login raté
                    return false;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                /*Login dialog = new Login(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);*/
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOK;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField textPassword;
    private javax.swing.JTextField textUsername;
    // End of variables declaration//GEN-END:variables

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
    /*public boolean checkLogin(String s1, String s2) 
    {
        FileInputStream fis = null;
        try
        {
            Properties prop = new Properties();
            fis = new FileInputStream("src\\GUI\\users.properties");
            prop.load(fis);
            String temp = prop.getProperty(s1); //on stocke le mdp lié à la chaine de char s1 dans temp, retourne null si pas de chaine
            if(temp != null) //utilisateur existant
                if(temp.equals(s2)) // si mdp correct
                    return true;
                else
                    return false; // user existant mais mdp incorrect
            else //utilisateur inexistant
                return false;
        }
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(null, "Login: Erreur lors de la lecture du fichier properties: " + ex.getMessage());
            return false;
        }
    }*/



    
}