package de.eis.muellerkimmeyer.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;
import org.json.JSONObject;

import de.eis.muellerkimmeyer.helper.SQLiteHelper;
import de.eis.muellerkimmeyer.helper.ServerRequest;
import de.eis.muellerkimmeyer.klassen.Aquarium;
import de.eis.muellerkimmeyer.klassen.Customer;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class Wasseranalyse extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final int PADDING = 30;
	
	private Customer customer;
	private Aquarium aquarium;
	private ServerRequest server;
	private SQLiteHelper sqliteHelper;
	private ArrayList<JTextField> textFields;
	
	public Wasseranalyse(Customer customer){
		this.customer = customer;
		this.aquarium = new Aquarium();
		this.server = new ServerRequest();
		this.sqliteHelper = new SQLiteHelper();
		this.textFields = new ArrayList<>();
		initialise();
	}
	
	private void initialise(){
		
		this.setLayout(null);
		
		// Setup Wasserwerte abschicken
		
		JPanel absendenPanel = new JPanel();
		Accordion accordion = new Accordion();
		absendenPanel.setBounds(accordion.accordionPaneWidth-PADDING-400, PADDING, 400, 500);
		Border border = BorderFactory.createLineBorder(Color.black);
		absendenPanel.setBorder(border);
		absendenPanel.setBackground(Color.WHITE);
		absendenPanel.setLayout(null);
		
		int count = 1;
		int padding = 10;
		int height = 30;
		
		makeRow(absendenPanel, "Karbonathärte (KH):", "dH°", count++);
		makeRow(absendenPanel, "Gesamthärte (GH):", "dH°", count++);
		makeRow(absendenPanel, "pH Wert:", "pH", count++);
		makeRow(absendenPanel, "Kohlenstoffdioxid (CO2):", "mg/l", count++);
		makeRow(absendenPanel, "Eisen:", "mg/l", count++);
		makeRow(absendenPanel, "Kalium:", "mg/l", count++);
		makeRow(absendenPanel, "Nitrat (NO3):", "mg/l", count++);
		makeRow(absendenPanel, "Phosphat (PO3):", "mg/l", count++);
		
		JButton btnAbsenden = new JButton("Absenden");
		btnAbsenden.setBounds(padding, count*padding+((count-1)*height), 200, height);
		absendenPanel.add(btnAbsenden);
		count++;
		
		JLabel antwort = new JLabel();
		antwort.setBounds(padding, count*padding+((count-1)*height), 300, height);
		absendenPanel.add(antwort);
		count++;
		
		btnAbsenden.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Date datum = new Date();
	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            String datumStr = dateFormat.format(datum);
				
				String kh = textFields.get(0).getText();
				String gh = textFields.get(1).getText();
				String ph = textFields.get(2).getText();
				String co2 = textFields.get(3).getText();
				String eisen = textFields.get(4).getText();
				String kalium = textFields.get(5).getText();
				String no3 = textFields.get(6).getText();
				String po3 = textFields.get(7).getText();
				
				if(!kh.isEmpty() && !gh.isEmpty() && !ph.isEmpty() && !co2.isEmpty() && !eisen.isEmpty() && !kalium.isEmpty() && !no3.isEmpty() && !po3.isEmpty()){
					
					String urlParameters = "{\"von\": \"Fachhandlung\", \"datum\": \"" + datumStr + "\", \"kh\": \"" + kh + "\", \"gh\": \"" + gh + "\", \"ph\": \"" + ph + "\", \"co2\": \"" + co2 + "\", \"eisen\": \"" + eisen + "\", \"kalium\": \"" + kalium + "\", \"no3\": \"" + no3 + "\", \"po3\": \"" + po3 + "\"}";
	                String response = server.sendPost("http://eis1617.lupus.uberspace.de/nodejs/wasserwerte/"+customer.getUid(), urlParameters);

	                try {
	                	JSONObject json = new JSONObject(response);
	        			if(json.getString("success") != null && json.getString("success") != "false"){
	        				antwort.setText("Werte erfolgreich verschickt!");
	        				sqliteHelper.addLogbuchEintrag("Wasseranalyse verschickt", "Wasseranalyse", "-");
	        				Kundeninformationen ki = new Kundeninformationen();
	        				DefaultTableModel model = (DefaultTableModel) ki.table.getModel();
	        				SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
	        	            String datumStr2 = dateFormat2.format(datum);
	    					model.addRow(new Object[]{datumStr2+" Uhr", "Wasseranalyse verschickt", "Wasseranalyse", "-"});
	        			}
	        			else{
	        				antwort.setText("Fehler beim Verschicken der Werte!");
	        			}
	                }catch (JSONException e1){
	                    e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
	                
				}
				else{
					antwort.setText("Bitte alle Felder ausfüllen!");
				}
				
			}
			
		});
		
		this.add(absendenPanel);
		
		// Setup Berechnungen Panel
		
		JPanel berechnungenPanel = new JPanel();
		berechnungenPanel.setBounds(PADDING, PADDING, 400, 500);
		berechnungenPanel.setBorder(border);
		berechnungenPanel.setBackground(Color.WHITE);
		berechnungenPanel.setLayout(null);
		
		JLabel labelCo2 = new JLabel("CO2 berechnen:");
		labelCo2.setBounds(padding, padding, 200, 30);
		labelCo2.setFont(labelCo2.getFont().deriveFont(20.0f));
		berechnungenPanel.add(labelCo2);
		
		count = 2;
		
		makeRow(berechnungenPanel, "Karbonathärte (KH):", "dH°", count++);
		makeRow(berechnungenPanel, "pH-Wert:", "pH", count++);
		
		JButton btnCo2Berechnen = new JButton("Berechnen");
		btnCo2Berechnen.setBounds(padding, count*padding+((count-1)*height), 200, height);
		berechnungenPanel.add(btnCo2Berechnen);
		count++;
		
		JLabel antwort2 = new JLabel();
		antwort2.setBounds(padding, count*padding+((count-1)*height), 300, height);
		berechnungenPanel.add(antwort2);
		count++;
		
		btnCo2Berechnen.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String kh = textFields.get(8).getText();
				String ph = textFields.get(9).getText();
				
				if(!kh.isEmpty() && !ph.isEmpty()){
					textFields.get(0).setText(kh);
					textFields.get(2).setText(ph);
					textFields.get(3).setText(String.valueOf(aquarium.getCO2Gehalt(Double.parseDouble(kh), Double.parseDouble(ph))));
				}
				else{
					antwort2.setText("Bitte alle Felder ausfüllen!");
				}
				
			}
		
		});
		
		this.add(berechnungenPanel);
		
	}
	
	private void makeRow(JPanel panel, String title, String einheit, int i){
		
		int padding = 10;
		int labelWidth = 250;
		int tfWidth = 70;
		int labelEinheitWidth = 50;
		int height = 30;
		
		JLabel label = new JLabel(title);
		label.setBounds(padding, i*padding+((i-1)*height), labelWidth, height);
		label.setFont(label.getFont().deriveFont(18.0f));
		panel.add(label);
		
		JTextField tf = new JTextField();
		tf.setBounds(padding+labelWidth, i*padding+((i-1)*height), tfWidth, height);
		panel.add(tf);
		textFields.add(tf);
		
		JLabel labelEinheit = new JLabel(einheit);
		labelEinheit.setBounds(padding+labelWidth+tfWidth+padding, i*padding+((i-1)*height), labelEinheitWidth, height);
		labelEinheit.setFont(labelEinheit.getFont().deriveFont(18.0f));
		panel.add(labelEinheit);
		
	}

}
