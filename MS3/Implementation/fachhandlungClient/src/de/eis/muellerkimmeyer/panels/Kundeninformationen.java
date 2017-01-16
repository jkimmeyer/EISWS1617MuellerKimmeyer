package de.eis.muellerkimmeyer.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.eis.muellerkimmeyer.helper.SQLiteHelper;
import de.eis.muellerkimmeyer.helper.ServerRequest;
import de.eis.muellerkimmeyer.klassen.Aquarium;
import de.eis.muellerkimmeyer.klassen.Customer;
import de.eis.muellerkimmeyer.klassen.LogbuchEintrag;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class Kundeninformationen extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final int PADDING = 30;
	private final Color tableHeaderBG = new Color(200,200,200);
    private final Color tableRow1BG = new Color(255,255,255);
    private final Color tableRow2BG = new Color(240,240,240);

	private Customer customer;
	private Aquarium aquarium;
	private SQLiteHelper sqliteHelper;
	private ServerRequest serverRequest;
	public static JTable table;
	
	public Kundeninformationen(){
	}
	
	public Kundeninformationen(Customer customer){
		this.customer = customer;
		this.sqliteHelper = new SQLiteHelper();
		this.serverRequest = new ServerRequest();
		initialise();
	}
	
	private void initialise(){
		
		this.setLayout(null);
		
		// Setup Image
		
		ImageIcon image = new ImageIcon("images/benutzer.jpg");
		JLabel labelImage = new JLabel("", image, JLabel.CENTER);
		labelImage.setBounds(PADDING,PADDING,150,150);
		this.add(labelImage);
		
		// Setup Personen Daten
		
		JLabel labelVorname = new JLabel("Vorname:");
		labelVorname.setBounds(PADDING, PADDING+150+20, 150, 30);
		labelVorname.setFont(labelVorname.getFont().deriveFont(18.0f));
		this.add(labelVorname);
		
		JLabel labelVornameWert = new JLabel(customer.getVorname());
		labelVornameWert.setBounds(PADDING+150, PADDING+150+20, 100, 30);
		labelVornameWert.setFont(labelVornameWert.getFont().deriveFont(18.0f));
		this.add(labelVornameWert);
		
		JLabel labelNachname = new JLabel("Nachname:");
		labelNachname.setBounds(PADDING, PADDING+150+50, 150, 30);
		labelNachname.setFont(labelNachname.getFont().deriveFont(18.0f));
		this.add(labelNachname);
		
		JLabel labelNachnameWert = new JLabel(customer.getNachname());
		labelNachnameWert.setBounds(PADDING+150, PADDING+150+50, 100, 30);
		labelNachnameWert.setFont(labelNachnameWert.getFont().deriveFont(18.0f));
		this.add(labelNachnameWert);
		
		JLabel labelGeburtstag = new JLabel("Geburtsdatum:");
		labelGeburtstag.setBounds(PADDING, PADDING+150+80, 150, 30);
		labelGeburtstag.setFont(labelGeburtstag.getFont().deriveFont(18.0f));
		this.add(labelGeburtstag);
		
		JLabel labelGeburtstagWert = new JLabel(customer.getGeburtsdatum());
		labelGeburtstagWert.setBounds(PADDING+150, PADDING+150+80, 100, 30);
		labelGeburtstagWert.setFont(labelGeburtstagWert.getFont().deriveFont(18.0f));
		this.add(labelGeburtstagWert);
		
		// Setup Aquarium Daten
		
		aquarium = loadAquariumDaten();
		
		JPanel aquariumDaten = new JPanel();
		aquariumDaten.setLayout(null);
		Accordion accordion = new Accordion();
		aquariumDaten.setBounds(accordion.accordionPaneWidth-PADDING-408, PADDING, 408, 500);
		Border border = BorderFactory.createLineBorder(Color.black);
		aquariumDaten.setBorder(border);
		aquariumDaten.setBackground(Color.WHITE);
		
		ImageIcon imageAq = new ImageIcon("images/aquarium.png");
		JLabel labelImageAq = new JLabel("", imageAq, JLabel.CENTER);
		labelImageAq.setBounds(PADDING,PADDING,348,151);
		aquariumDaten.add(labelImageAq);
		
		JLabel labelLaenge = new JLabel("Länge:");
		labelLaenge.setBounds(PADDING, PADDING+151+20, 240, 30);
		labelLaenge.setFont(labelLaenge.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelLaenge);
		
		JLabel labelLaengeWert = new JLabel(aquarium.getLaenge()+" cm");
		labelLaengeWert.setBounds(PADDING+240, PADDING+151+20, 100, 30);
		labelLaengeWert.setFont(labelLaengeWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelLaengeWert);
		
		JLabel labelBreite = new JLabel("Breite:");
		labelBreite.setBounds(PADDING, PADDING+151+50, 240, 30);
		labelBreite.setFont(labelBreite.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelBreite);
		
		JLabel labelBreiteWert = new JLabel(aquarium.getBreite()+" cm");
		labelBreiteWert.setBounds(PADDING+240, PADDING+151+50, 100, 30);
		labelBreiteWert.setFont(labelBreiteWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelBreiteWert);
		
		JLabel labelHoehe = new JLabel("Höhe:");
		labelHoehe.setBounds(PADDING, PADDING+151+80, 240, 30);
		labelHoehe.setFont(labelHoehe.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelHoehe);
		
		JLabel labelHoeheWert = new JLabel(aquarium.getHoehe()+" cm");
		labelHoeheWert.setBounds(PADDING+240, PADDING+151+80, 100, 30);
		labelHoeheWert.setFont(labelHoeheWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelHoeheWert);
		
		JLabel labelGlasstaerke = new JLabel("Glasstärke:");
		labelGlasstaerke.setBounds(PADDING, PADDING+151+110, 240, 30);
		labelGlasstaerke.setFont(labelGlasstaerke.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelGlasstaerke);
		
		JLabel labelGlasstaerkeWert = new JLabel(aquarium.getGlasstaerke()+" cm");
		labelGlasstaerkeWert.setBounds(PADDING+240, PADDING+151+110, 100, 30);
		labelGlasstaerkeWert.setFont(labelGlasstaerkeWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelGlasstaerkeWert);
		
		JLabel labelKieshoehe = new JLabel("Kieshöhe:");
		labelKieshoehe.setBounds(PADDING, PADDING+151+140, 240, 30);
		labelKieshoehe.setFont(labelKieshoehe.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelKieshoehe);
		
		JLabel labelKieshoeheWert = new JLabel(aquarium.getKieshoehe()+" cm");
		labelKieshoeheWert.setBounds(PADDING+240, PADDING+151+140, 100, 30);
		labelKieshoeheWert.setFont(labelKieshoeheWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelKieshoeheWert);
		
		JLabel labelFd = new JLabel("Füllstanddifferenz:");
		labelFd.setBounds(PADDING, PADDING+151+170, 240, 30);
		labelFd.setFont(labelFd.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelFd);
		
		JLabel labelFdWert = new JLabel(aquarium.getFuellstanddifferenz()+" cm");
		labelFdWert.setBounds(PADDING+240, PADDING+151+170, 100, 30);
		labelFdWert.setFont(labelFdWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelFdWert);
		
		JLabel labelBruttoVol = new JLabel("Aquarium Volumen:");
		labelBruttoVol.setBounds(PADDING, PADDING+151+230, 240, 30);
		labelBruttoVol.setFont(labelBruttoVol.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelBruttoVol);
		
		JLabel labelBruttoVolWert = new JLabel(aquarium.getBruttoVolumenInLiter()+" Liter");
		labelBruttoVolWert.setBounds(PADDING+240, PADDING+151+230, 100, 30);
		labelBruttoVolWert.setFont(labelBruttoVolWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelBruttoVolWert);
		
		JLabel labelNettoVol = new JLabel("Netto Wasser Volumen:");
		labelNettoVol.setBounds(PADDING, PADDING+151+260, 240, 30);
		labelNettoVol.setFont(labelNettoVol.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelNettoVol);
		
		JLabel labelNettoVolWert = new JLabel(aquarium.getNettoVolumenInLiter()+" Liter");
		labelNettoVolWert.setBounds(PADDING+240, PADDING+151+260, 100, 30);
		labelNettoVolWert.setFont(labelNettoVolWert.getFont().deriveFont(18.0f));
		aquariumDaten.add(labelNettoVolWert);
		
		this.add(aquariumDaten);
		
		// Setup Logbuch
		
		JLabel labelLogbuch = new JLabel("Logbuch:");
		labelLogbuch.setBounds(PADDING, PADDING+300, 100, 30);
		labelLogbuch.setFont(labelLogbuch.getFont().deriveFont(18.0f));
		this.add(labelLogbuch);
		
		JButton btnNeuerEintrag = new JButton("Neuer Eintrag");
		btnNeuerEintrag.setBounds(PADDING+750-150, PADDING+300, 150, 30);
		btnNeuerEintrag.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showEintragAnlegenDialog();
			}
			
		});
		this.add(btnNeuerEintrag);
		
		String[] columnNames = {"Datum", "Art", "Anliegen", "Bemerkung"};
		Object[][] customerData = loadLogbuchEintraege();
		
		DefaultTableModel tableModel = new DefaultTableModel(customerData, columnNames) {

			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		    
		};
		
		table = new JTable();
		table.setModel(tableModel);
		table.setRowHeight(20);
		table.getTableHeader().setBackground(tableHeaderBG);
		
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(rowSorter);
		
		/*
	    * QUELLENANGABE
	    * Jede zweite Reihe soll in einer anderen Farbe dargestellt
	    * werden, um die Übersichtlichkeit zu verbessern. 
	    * Übernommen aus: http://stackoverflow.com/questions/22301575/how-to-change-the-background-color-of-each-second-row
	    * Autor: "Gorbles", Abruf am: 21.12.2016
	    * Änderung: -
	    */
		
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        
		        c.setBackground(row % 2 == 0 ? tableRow1BG : tableRow2BG);
		        
		        return this;
		    }
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(PADDING, PADDING+340, 750, 160);
		scrollPane.setViewportView(table);
		this.add(scrollPane);
		
	}
	
	private Aquarium loadAquariumDaten(){
		Aquarium aquarium = null;
		String response = serverRequest.sendGet("http://eis1617.lupus.uberspace.de/nodejs/aquarien/"+customer.getUid());
		try {
			JSONObject json = new JSONObject(response);
			if(json.getString("success") != null && json.getString("success") != "false"){
				JSONArray aqArr = json.getJSONArray("aquarien");
				JSONObject aq = aqArr.getJSONObject(0);
				aquarium = new Aquarium(aq.getString("bezeichnung"), aq.getDouble("laenge"), aq.getDouble("breite"), aq.getDouble("hoehe"), aq.getDouble("glasstaerke"), aq.getDouble("kieshoehe"), aq.getDouble("fd"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aquarium;
	}
	
	private Object[][] loadLogbuchEintraege(){
		ArrayList<LogbuchEintrag> eintraege = new ArrayList<>();
    	
    	try {
    		ResultSet rs = sqliteHelper.getLogbuchEintraege();
    		while(rs.next()){
    			eintraege.add(new LogbuchEintrag(rs.getInt("id"), rs.getDate("datum"), rs.getString("art"), rs.getString("anliegen"), rs.getString("bemerkung")));
    		}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	Collections.reverse(eintraege);
    	
    	Object[][] logbuchData = new Object[eintraege.size()][4];
    	
    	for(int i = 0; i < eintraege.size(); i++){
    		
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            String datumStr = dateFormat.format(eintraege.get(i).getDatum());
    		
    		logbuchData[i][0] = datumStr+" Uhr";
    		logbuchData[i][1] = eintraege.get(i).getArt();
    		logbuchData[i][2] = eintraege.get(i).getAnliegen();
    		logbuchData[i][3] = eintraege.get(i).getBemerkung();
    		
    	}
    		
		return logbuchData;
	}
	
	private void showEintragAnlegenDialog(){
    	int padding = 10;
    	JPanel dialogPanel = new JPanel();
    	dialogPanel.setLayout(null);
    	
        JLabel labelArt = new JLabel("Art:");
        labelArt.setBounds(padding, padding, 100, 20);
        dialogPanel.add(labelArt);
        
        JTextField tfArt = new JTextField();
        tfArt.setBounds(100+padding, padding, 130, 20);
        dialogPanel.add(tfArt);
        
        JLabel labelAnliegen = new JLabel("Anliegen:");
        labelAnliegen.setBounds(padding, 20+(2*padding), 100, 20);
        dialogPanel.add(labelAnliegen);
        
        JTextField tfAnliegen = new JTextField();
        tfAnliegen.setBounds(100+padding, 20+(2*padding), 130, 20);
        dialogPanel.add(tfAnliegen);
        
        JLabel labelBemerkung = new JLabel("Bemerkung:");
        labelBemerkung.setBounds(padding, 40+(3*padding), 100, 20);
        dialogPanel.add(labelBemerkung);
        
        JTextField tfBemerkung = new JTextField();
        tfBemerkung.setBounds(100+padding, 40+(3*padding), 130, 20);
        dialogPanel.add(tfBemerkung);
        
        UIManager.put("OptionPane.minimumSize", new Dimension(300,200));
    	int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Neuen Kunden anlegen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    	if(result == JOptionPane.OK_OPTION){
    		
    		String art = tfArt.getText();
    		String anliegen = tfAnliegen.getText();
    		String bemerkung = tfBemerkung.getText();
    		
    		if(!art.isEmpty() && !anliegen.isEmpty() && !bemerkung.isEmpty()){
    			
    			try {
					sqliteHelper.addLogbuchEintrag(art, anliegen, bemerkung);
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
		            String datumStr = dateFormat.format(new Date(System.currentTimeMillis()));
					model.addRow(new Object[]{datumStr+" Uhr", art, anliegen, bemerkung});
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    			
    		}
    		
    	}
    }
	
}
