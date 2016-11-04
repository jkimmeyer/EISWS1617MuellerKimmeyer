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
    private JLabel phWertLabel, nutrient1WertLabel, dayXLabel, nutrientXWertLabel, tokenLabel;
    private JTextField phWertTf, dayXTf, nutrient1WertTf, nutrientXWertTf, tokenTf;
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
        
        this.nutrient1WertLabel = new JLabel();
        this.nutrient1WertLabel.setText("Nutrients-Tag1:");
        this.nutrient1WertLabel.setBounds(10, 50, 150, 30);
        
        this.nutrientXWertLabel = new JLabel();
        this.nutrientXWertLabel.setText("Nutrients-TagX:");
        this.nutrientXWertLabel.setBounds(10, 90, 150, 30);
        
        this.dayXLabel = new JLabel();
        this.dayXLabel.setText("TagX:");
        this.dayXLabel.setBounds(10, 130, 150, 30);
        
        this.tokenLabel = new JLabel();
        this.tokenLabel.setText("Empfänger:");
        this.tokenLabel.setBounds(10, 170, 150, 30);
        
        this.phWertTf = new JTextField();
        this.phWertTf.setBounds(160, 10, 50, 30);
        
        this.nutrient1WertTf = new JTextField();
        this.nutrient1WertTf.setBounds(160, 50, 50, 30);
        
        this.nutrientXWertTf = new JTextField();
        this.nutrientXWertTf.setBounds(160, 90, 50, 30);
        
        this.dayXTf = new JTextField();
        this.dayXTf.setBounds(160, 130, 50, 30);

        this.tokenTf = new JTextField();
        this.tokenTf.setBounds(160, 170, 550, 30);
        
        this.absendenBtn = new JButton("Absenden");
        this.absendenBtn.setBounds(10, 210, 100, 30);
        
        this.getContentPane().add(this.phWertLabel);
        this.getContentPane().add(this.nutrient1WertLabel);
        this.getContentPane().add(this.nutrientXWertLabel);
        this.getContentPane().add(this.dayXLabel);
        this.getContentPane().add(this.tokenLabel);
        this.getContentPane().add(this.phWertTf);
        this.getContentPane().add(this.nutrient1WertTf);
        this.getContentPane().add(this.nutrientXWertTf);
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
                String nutrientsDay1 = nutrient1WertTf.getText();
                String nutrientsDayX = nutrientXWertTf.getText();
                String anzTage = dayXTf.getText();
                String token = tokenTf.getText();
                
                String urlParameters = "{\"tokens\": \""+ token +"\", \"message\": {\"anzTage\": \""+ anzTage +"\",\"nutrientsDayX\": \""+ nutrientsDayX +"\",\"ph\": \""+ phWert +"\", \"nutrientsDay1\": \""+ nutrientsDay1 +"\"}}";
                server = new ServerRequest();
                server.sendPost(urlString, urlParameters);
                
                JOptionPane.showMessageDialog(null, server.json);
            }
            
        });
    }
	
}
