package de.eis.muellerkimmeyer.panels;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.eis.muellerkimmeyer.helper.SQLiteHelper;
import de.eis.muellerkimmeyer.helper.ServerRequest;
import de.eis.muellerkimmeyer.klassen.Customer;
import de.eis.muellerkimmeyer.klassen.Section;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *  Layout Klasse
 *
 *  Autor: Moritz Müller
 */

public class AppFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
    private ServerRequest server;
    private JTabbedPane tabbedPane;
    private JComponent searchPanel, latestCustomer;
    private int tabbedPaneWidth, tabbedPaneHeight, tabbedPaneMargin;
    private JTextField searchField;
    private JButton btnKundeAnlegen;
    private JTable table;
    private JLabel searchLabel;
    private JScrollPane scrollPane;
    private SQLiteHelper sqliteHelper;
    
    final Color frameBG = new Color(225,230,246);
    final Color panelBG = new Color(242,242,242);
    final Color tableHeaderBG = new Color(200,200,200);
    final Color tableRow1BG = new Color(255,255,255);
    final Color tableRow2BG = new Color(240,240,240);
    final Color tableSelectedBG = new Color(184,207,229);
    
    public AppFrame(){
        
    	sqliteHelper = new SQLiteHelper();
    	server = new ServerRequest();
        this.initComponents();
        
        setVisible(true);
    }
    
    public void initComponents(){
    	setTitle("Kundenverwaltung");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.getContentPane().setLayout(null);
        this.getContentPane().setBackground(frameBG);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        /* Höhe und Breite des TabbedPane ausrechnen, damit es sich an die
         * Fenstergröße anpasst und einen festen äußeren Abstand haben kann
         */
        
        tabbedPaneMargin = 10;
        tabbedPaneWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width-(2*tabbedPaneMargin);
        tabbedPaneHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height-(2*tabbedPaneMargin)-65;
		tabbedPane.setBounds(tabbedPaneMargin, tabbedPaneMargin, tabbedPaneWidth, tabbedPaneHeight);
	
		searchPanel = makeSearchPanel();
		tabbedPane.addTab("+", null, searchPanel, "Kunde öffnen");
		this.getContentPane().add(tabbedPane);
        
    }
    
    protected JComponent makeSearchPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(panelBG);
        panel.setLayout(null);
		
        int panelPadding = 30;
        
		searchLabel = new JLabel("Suche");
		searchLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		searchLabel.setBounds(panelPadding, panelPadding+5, 46, 14);
		panel.add(searchLabel);
		
		searchField = new JTextField();
		searchField.setBounds(100, panelPadding, tabbedPaneWidth-panelPadding-400, 30);
		panel.add(searchField);
		
		btnKundeAnlegen = new JButton("Neuen Kunden hinzufügen");
		btnKundeAnlegen.setBounds(tabbedPaneWidth-panelPadding-280, panelPadding, 280, 30);
		btnKundeAnlegen.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showKundenAnlegenDialog();
				
			}
			
		});
		panel.add(btnKundeAnlegen);
		
		String[] columnNames = {"Kunden ID", "Vorname", "Nachname", "Geburtsdatum"};
		Object[][] customerData = loadCustomerData();

		/*
		 * Da bei einem Doppelklick der entsprechende Tab
		 * geöffnet werden soll und nicht die Daten
		 * editiert werden sollen, muss die Methode
		 * isCellEditable überschrieben werden
		 */
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
	    * Änderung: Berücksichtigung hinzugefügt, ob eine Reihe selektiert ist
	    */
		
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        if(isSelected){
		        	c.setBackground(tableSelectedBG);
		        }
		        else{
		        	c.setBackground(row % 2 == 0 ? tableRow1BG : tableRow2BG);
		        }
		        return this;
		    }
		});
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(panelPadding, 90, tabbedPaneWidth-(2*panelPadding), tabbedPaneHeight-145);
		scrollPane.setViewportView(table);
		panel.add(scrollPane);
		
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateTable();
			}
			public void removeUpdate(DocumentEvent e) {
				updateTable();
			}
			public void insertUpdate(DocumentEvent e) {
				updateTable();
			}

			public void updateTable() {
				String searchText = searchField.getText();

                if (searchText.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		    	if (e.getClickCount() == 2) {
		    		String id = table.getValueAt(table.getSelectedRow(), 0).toString();
		        	String vorname = table.getValueAt(table.getSelectedRow(), 1).toString();
		        	String nachname = table.getValueAt(table.getSelectedRow(), 2).toString();
		            
		            /* Wenn bereits ein Tab mit dem Kunden geöffnet ist, soll
		             * dieser selektiert werden, anstatt einen neuen Tab zu öffnen
		             */
		            String tabName = "[" + id + "] " + vorname + " " + nachname;
		            if(tabbedPane.indexOfTab(tabName) != -1){
		            	tabbedPane.setSelectedComponent(tabbedPane.getComponentAt(tabbedPane.indexOfTab(tabName)));
		            }
		            else{
			            tabbedPane.remove(searchPanel);
						openCustomerTab(id);
						tabbedPane.addTab("+", null, searchPanel, "Kunde öffnen");
						tabbedPane.setSelectedComponent(latestCustomer);
			        }
		    	}
		    }
		});
        return panel;
    }
    
    private void openCustomerTab(String id){
    	
    	Customer customer = null;
    	try {
			ResultSet rs = sqliteHelper.getKunde(Integer.parseInt(id));
			if(rs.next()){
				customer = new Customer(rs.getInt("id"), rs.getString("appId"), rs.getString("uid"), rs.getString("vorname"), rs.getString("nachname"), rs.getString("geburtsdatum"));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	if(customer != null){
    	
	    	JPanel panel = new JPanel();
	    	panel.setLayout(null);
	        /*
	         *  Den zuletzt geöffneten Kunden speichern, 
	         *  damit der entsprechende Tab direkt nach
	         *  dem erstellen selektiert werden kann
	         */
	        latestCustomer = panel;
	    	/*
	    	 * Damit nicht mehr der zuletzt gesuchte
	    	 * Kunde in der Suche steht bzw. selektiert
	    	 * ist, wird das Suchfeld geleert und die
	    	 * Selektierung aufgehoben
	    	 */
	    	searchField.setText("");
	    	table.clearSelection();
	    	
	    	// Inhalt des Panels
	    	ArrayList<Section> sections = new ArrayList<Section>();
	    	
	    	Kundeninformationen kundeninformationen = new Kundeninformationen(customer);
	    	//JPanel kaufberatung = new JPanel();
	    	//JPanel problemanalyse = new JPanel();
	    	Wasseranalyse wasseranalyse = new Wasseranalyse(customer);
	    	sections.add(new Section("Kundeninformationen", kundeninformationen, true));
	    	//sections.add(new Section("Kaufberatung", kaufberatung, false));
	    	//sections.add(new Section("Problemanalyse", problemanalyse, false));
	    	sections.add(new Section("Wasseranalyse", wasseranalyse, false));
	    	Accordion accordion = new Accordion(sections);
	    	panel.add(accordion);
	    	
	    	String name = "[" + customer.getId() + "] " + customer.getVorname() + " " + customer.getNachname();
			tabbedPane.addTab(name, null, panel, name);
			
			/*
		    * QUELLENANGABE
		    * Close-Buttons zu den Tabs hinzufügen
		    * Übernommen aus: http://stackoverflow.com/questions/11553112/how-to-add-close-button-to-a-jtabbedpane-tab
		    * Autor: "MadProgrammer", Abruf am: 02.01.2017
		    * Änderung: Variablen-Namen angepasst, Platz zwischen Label und Button hinzugefügt, Aussehen des Buttons geändert  
		    */
			
			int index = tabbedPane.indexOfTab(name);
			JPanel pnlTab = new JPanel(new GridBagLayout());
			pnlTab.setOpaque(false);
			JLabel lblTitle = new JLabel(name);
			
			JButton btnClose = new JButton("x");
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
	
			pnlTab.add(lblTitle, gbc);
	
			gbc.gridx++;
			gbc.weightx = 0;
			pnlTab.add(new JLabel("    "), gbc);
			
			gbc.gridx++;
			gbc.weightx = 0;
			btnClose.setBorder(null);
			btnClose.setOpaque(false);
			btnClose.setContentAreaFilled(false);
			btnClose.setBorderPainted(false);
			btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
			pnlTab.add(btnClose, gbc);
	
			tabbedPane.setTabComponentAt(index, pnlTab);
	
			btnClose.addActionListener(new CloseActionHandler(name));
    	}
    	else{
    		JOptionPane.showMessageDialog(null, "Ein Fehler ist aufgetreten!", "Fehler", JOptionPane.PLAIN_MESSAGE);
    	}
    }
    
    private void showKundenAnlegenDialog(){
    	int padding = 10;
    	JPanel dialogPanel = new JPanel();
    	dialogPanel.setLayout(null);
    	
        JLabel labelAppId = new JLabel("App ID:");
        labelAppId.setBounds(padding, padding, 100, 20);
        dialogPanel.add(labelAppId);
        
        JTextField tfAppId = new JTextField();
        tfAppId.setBounds(100+padding, padding, 130, 20);
        dialogPanel.add(tfAppId);
        
        JLabel labelVorname = new JLabel("Vorname:");
        labelVorname.setBounds(padding, 20+(2*padding), 100, 20);
        dialogPanel.add(labelVorname);
        
        JTextField tfVorname = new JTextField();
        tfVorname.setBounds(100+padding, 20+(2*padding), 130, 20);
        dialogPanel.add(tfVorname);
        
        JLabel labelNachname = new JLabel("Nachname:");
        labelNachname.setBounds(padding, 40+(3*padding), 100, 20);
        dialogPanel.add(labelNachname);
        
        JTextField tfNachname = new JTextField();
        tfNachname.setBounds(100+padding, 40+(3*padding), 130, 20);
        dialogPanel.add(tfNachname);
        
        JLabel labelGeburstdatum = new JLabel("Geburstdatum:");
        labelGeburstdatum.setBounds(padding, 60+(4*padding), 100, 20);
        dialogPanel.add(labelGeburstdatum);
        
        JTextField tfGeburtsdatum = new JTextField();
        tfGeburtsdatum.setBounds(100+padding, 60+(4*padding), 130, 20);
        dialogPanel.add(tfGeburtsdatum);
        
        UIManager.put("OptionPane.minimumSize", new Dimension(300,200));
    	int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Neuen Kunden anlegen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    	if(result == JOptionPane.OK_OPTION){
    		
    		String appId = tfAppId.getText();
    		String vorname = tfVorname.getText();
    		String nachname = tfNachname.getText();
    		String geburtsdatum = tfGeburtsdatum.getText();
    		
    		if(!appId.isEmpty() && !vorname.isEmpty() && !nachname.isEmpty() && !geburtsdatum.isEmpty()){
    			
    			try {
    				String response = server.sendGet("http://eis1617.lupus.uberspace.de/nodejs/users/"+appId);
    				JSONObject json = new JSONObject(response);
    				if(json.getString("success") != null && json.getString("success") != "false"){
    					JSONArray users = json.getJSONArray("user");
    					JSONObject user = users.getJSONObject(0);
    					String uid = user.getString("uid");
    					int id = sqliteHelper.addKunde(appId, uid, vorname, nachname, geburtsdatum);
    					DefaultTableModel model = (DefaultTableModel) table.getModel();
    					model.addRow(new Object[]{id, vorname, nachname, geburtsdatum});
    				}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
    			
    		}
    		
    	}
    }
    
    private Object[][] loadCustomerData() {
    	
    	ArrayList<Customer> kunden = new ArrayList<>();
    	
    	try {
    		ResultSet rs = sqliteHelper.getAllKunden();
    		while(rs.next()){
    			kunden.add(new Customer(rs.getInt("id"), rs.getString("appId"), rs.getString("uid"), rs.getString("vorname"), rs.getString("nachname"), rs.getString("geburtsdatum")));
    		}
    		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	Object[][] customerData = new Object[kunden.size()][4];
    	
    	for(int i = 0; i < kunden.size(); i++){
    		
    		customerData[i][0] = kunden.get(i).getId();
    		customerData[i][1] = kunden.get(i).getVorname();
    		customerData[i][2] = kunden.get(i).getNachname();
    		customerData[i][3] = kunden.get(i).getGeburtsdatum();
    		
    	}
    		
		return customerData;
	}
    
    /*
    * QUELLENANGABE
    * ActionListener für die jeweiligen Close-Buttons der Tabs
    * Übernommen aus: http://stackoverflow.com/questions/11553112/how-to-add-close-button-to-a-jtabbedpane-tab
    * Autor: "MadProgrammer", Abruf am: 02.01.2017
    * Änderung: -
    */
    
    public class CloseActionHandler implements ActionListener {

        private String tabName;

        public CloseActionHandler(String tabName) {
            this.tabName = tabName;
        }

        public String getTabName() {
            return tabName;
        }

        public void actionPerformed(ActionEvent evt) {

            int index = tabbedPane.indexOfTab(getTabName());
            if (index >= 0) {

                tabbedPane.removeTabAt(index);

            }

        }

    }
	
}


