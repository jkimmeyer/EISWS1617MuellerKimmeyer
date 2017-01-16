package de.eis.muellerkimmeyer.klassen;

import javax.swing.JPanel;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class Section {

	private String title;
	private JPanel panel;
	private boolean isActive;
	
	public Section(String title, JPanel panel, boolean isActive){
		this.title = title;
		this.panel = panel;
		this.isActive = isActive;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
}
