package de.eis.muellerkimmeyer.klassen;

import java.sql.Date;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class LogbuchEintrag {
	
	private int id;
	private Date datum;
	private String art;
	private String anliegen;
	private String bemerkung;
	
	public LogbuchEintrag(int id, Date datum, String art, String anliegen, String bemerkung) {
		super();
		this.id = id;
		this.datum = datum;
		this.art = art;
		this.anliegen = anliegen;
		this.bemerkung = bemerkung;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getAnliegen() {
		return anliegen;
	}

	public void setAnliegen(String anliegen) {
		this.anliegen = anliegen;
	}

	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}
	
	
	
	

}
