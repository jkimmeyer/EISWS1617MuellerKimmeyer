package aquaap;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class AppFrame extends JFrame {
    
    private JButton absendenBtn;
    private JLabel phWertLabel, kalziumWertLabel, tokenLabel;
    private JTextField phWertTf, kalziumWertTf, tokenTf;
    private ServerRequest server;
    
    public AppFrame(){
        
        this.initComponents();
        this.initListeners();
        
        setVisible(true);
    }
    
    private void initComponents(){
        setTitle("Aquaapp");
        setBounds(0,0,800,800);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
        
        this.phWertLabel = new JLabel();
        this.phWertLabel.setText("PH-Wert:");
        this.phWertLabel.setBounds(10, 10, 100, 30);
        
        this.kalziumWertLabel = new JLabel();
        this.kalziumWertLabel.setText("Kalzium-Wert:");
        this.kalziumWertLabel.setBounds(10, 50, 100, 30);
        
        this.tokenLabel = new JLabel();
        this.tokenLabel.setText("Empfänger:");
        this.tokenLabel.setBounds(10, 90, 100, 30);
        
        this.phWertTf = new JTextField();
        this.phWertTf.setBounds(110, 10, 50, 30);
        
        this.kalziumWertTf = new JTextField();
        this.kalziumWertTf.setBounds(110, 50, 50, 30);

        this.tokenTf = new JTextField();
        this.tokenTf.setBounds(110, 90, 550, 30);
        
        this.absendenBtn = new JButton("Absenden");
        this.absendenBtn.setBounds(10, 150, 100, 30);
        
        this.getContentPane().add(this.phWertLabel);
        this.getContentPane().add(this.kalziumWertLabel);
        this.getContentPane().add(this.tokenLabel);
        this.getContentPane().add(this.phWertTf);
        this.getContentPane().add(this.kalziumWertTf);
        this.getContentPane().add(this.tokenTf);
        this.getContentPane().add(this.absendenBtn);
    
    }
    
    private void initListeners(){
        
        this.absendenBtn.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String urlString = "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte";
                
                String phWert = phWertTf.getText();
                String kalziumWert = kalziumWertTf.getText();
                String token = tokenTf.getText();
                
                String urlParameters = "{\"tokens\": \""+ token +"\", \"message\": {\"ph\": \""+ phWert +"\", \"kalzium\": \""+ kalziumWert +"\"}}";
                server = new ServerRequest();
                server.sendPost(urlString, urlParameters);
                
                JOptionPane.showMessageDialog(null, server.json);
            }
            
        });
    }
    
}
