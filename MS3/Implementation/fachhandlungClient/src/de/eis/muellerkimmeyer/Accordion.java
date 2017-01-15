package de.eis.muellerkimmeyer;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Accordion extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Section> sections;
	public int accordionPaneWidth;
	public int accordionPaneHeight;
	private int yCounter;
	
	final int sectionHeaderHeight = 30;
	final int accordionPaneMargin = 15;
	
	public Accordion(){
		
		this.accordionPaneWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width-(2*accordionPaneMargin)-25;
		this.accordionPaneHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height-(2*accordionPaneMargin)-110;
		
	}
	
	public Accordion(ArrayList<Section> section){
		this.sections = section;
		
		this.accordionPaneWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width-(2*accordionPaneMargin)-25;
		this.accordionPaneHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height-(2*accordionPaneMargin)-110;
		
		this.initAccordion();
	}
	
	public void initAccordion(){
		
		this.yCounter = 0;
		
		this.setBounds(accordionPaneMargin,accordionPaneMargin,accordionPaneWidth,accordionPaneHeight);
		this.setLayout(null);
		
		sections.forEach((section)->{
			JPanel headerPanel = new JPanel();
			headerPanel.setBounds(0, yCounter, accordionPaneWidth, sectionHeaderHeight);
			Border border = BorderFactory.createLineBorder(Color.black);
			headerPanel.setBorder(border);
			headerPanel.setLayout(null);
			JLabel label = new JLabel(section.getTitle());
			label.setBounds(10,0,accordionPaneWidth,sectionHeaderHeight);
			headerPanel.add(label);
			headerPanel.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					
					setActive(section.getTitle());
					removeAll();
					initAccordion();
					repaint();
					
				}
			});
			headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			this.add(headerPanel);
			yCounter = yCounter+sectionHeaderHeight-1;
			
			JPanel sectionPanel = section.getPanel();
			int sectionPanelHeight = accordionPaneHeight - (sections.size()*sectionHeaderHeight) - (sections.size()*1);
			sectionPanel.setBounds(0, yCounter, accordionPaneWidth, sectionPanelHeight);
			sectionPanel.setBorder(border);
			sectionPanel.setBackground(Color.WHITE);
			if(!section.isActive()){
				sectionPanel.setVisible(false);
			}
			else{
				yCounter = yCounter + sectionPanelHeight-1;
				sectionPanel.setVisible(true);
			}
			this.add(sectionPanel);
			
		});
		
	}
	
	public void setActive(String title) {
		this.sections.forEach((section)->{
			section.setActive(false);
			if(section.getTitle() == title){
				section.setActive(true);
			}
		});
	}
	
}
