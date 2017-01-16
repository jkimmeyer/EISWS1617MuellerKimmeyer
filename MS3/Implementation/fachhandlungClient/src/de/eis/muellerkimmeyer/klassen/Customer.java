package de.eis.muellerkimmeyer.klassen;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class Customer {
	
	private int id;
	private String appId;
	private String uid;
	private String vorname;
	private String nachname;
	private String geburtsdatum;
	
	public Customer(int id, String appId, String uid, String vorname, String nachname, String geburtsdatum) {
		super();
		this.id = id;
		this.appId = appId;
		this.uid = uid;
		this.vorname = vorname;
		this.nachname = nachname;
		this.geburtsdatum = geburtsdatum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(String geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

}
