package de.eis.muellerkimmeyer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller, Johannes Kimmeyer
 */

public class AppFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JButton absendenBtn;
    private JLabel phWertLabel, nährstoff1WertLabel, dayXLabel, nährstoffXWertLabel, tokenLabel;
    private JTextField phWertTf, dayXTf, nährstoff1WertTf, nährstoffXWertTf, tokenTf;
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
        this.phWertLabel.setBounds(10, 10, 150, 30);
        
        this.nährstoff1WertLabel = new JLabel();
        this.nährstoff1WertLabel.setText("Nährstoff-Wert1:");
        this.nährstoff1WertLabel.setBounds(10, 50, 150, 30);
        
        this.nährstoffXWertLabel = new JLabel();
        this.nährstoffXWertLabel.setText("Nährstoff-WertX:");
        this.nährstoffXWertLabel.setBounds(10, 90, 150, 30);
        
        this.dayXLabel = new JLabel();
        this.dayXLabel.setText("TagX:");
        this.dayXLabel.setBounds(10, 130, 150, 30);
        
        this.tokenLabel = new JLabel();
        this.tokenLabel.setText("Empfänger:");
        this.tokenLabel.setBounds(10, 170, 150, 30);
        
        this.phWertTf = new JTextField();
        this.phWertTf.setBounds(160, 10, 50, 30);
        
        this.nährstoff1WertTf = new JTextField();
        this.nährstoff1WertTf.setBounds(160, 50, 50, 30);
        
        this.nährstoffXWertTf = new JTextField();
        this.nährstoffXWertTf.setBounds(160, 90, 50, 30);
        
        this.dayXTf = new JTextField();
        this.dayXTf.setBounds(160, 130, 50, 30);

        this.tokenTf = new JTextField();
        this.tokenTf.setBounds(160, 170, 550, 30);
        
        this.absendenBtn = new JButton("Absenden");
        this.absendenBtn.setBounds(10, 150, 100, 30);
        
        this.getContentPane().add(this.phWertLabel);
        this.getContentPane().add(this.nährstoff1WertLabel);
        this.getContentPane().add(this.nährstoffXWertLabel);
        this.getContentPane().add(this.dayXLabel);
        this.getContentPane().add(this.tokenLabel);
        this.getContentPane().add(this.phWertTf);
        this.getContentPane().add(this.nährstoff1WertTf);
        this.getContentPane().add(this.nährstoffXWertTf);
        this.getContentPane().add(this.dayXTf);
        this.getContentPane().add(this.tokenTf);
        this.getContentPane().add(this.absendenBtn);
    
    }
    
    private void initListeners(){
        
        this.absendenBtn.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String urlString = "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte";
                
                String phWert = phWertTf.getText();
                String n1 = nährstoff1WertTf.getText();
                String nX = nährstoffXWertTf.getText();
                String X = dayXTf.getText();
                String token = tokenTf.getText();
                
                String urlParameters = "{\"tokens\": \""+ token +"\", \"message\": {\"Kalzium\": \""+ phWert +"\",\"X\": \""+ X +"\",\"nX\": \""+ nX +"\",\"ph\": \""+ phWert +"\", \"N1\": \""+ n1 +"\"}}";
                server = new ServerRequest();
                server.sendPost(urlString, urlParameters);
                
                JOptionPane.showMessageDialog(null, server.json);
            }
            
        });
    }
	
}
