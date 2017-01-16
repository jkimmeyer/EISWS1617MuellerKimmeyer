package de.eis.muellerkimmeyer.helper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * QUELLENANGABE
 * Klasse zum verarbeiten von SQLite Operationen
 * Abgeleitet aus: https://www.youtube.com/watch?v=JPsWaI5Z3gs
 * Autor: John McNeil, Abruf am: 15.01.2017
 */

public class SQLiteHelper {
	
	private static Connection connect;
	private static boolean hasData = false;
	
    public int addKunde(String appId, String uid, String vorname, String nachname, String geburtsdatum) throws SQLException, ClassNotFoundException{
    	
    	if(connect == null){
			getConnection();
		}
		
		PreparedStatement state = connect.prepareStatement("INSERT INTO kunden values(?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
		state.setString(2, appId);
		state.setString(3, uid);
		state.setString(4, vorname);
		state.setString(5, nachname);
		state.setString(6, geburtsdatum);
		state.execute();
		
		ResultSet rs = state.getGeneratedKeys();
		int newId = 0;
		if(rs.next()){
			newId = rs.getInt(1);
		}
		
		return newId;
    	
    }
    
    public ResultSet getKunde(int id) throws ClassNotFoundException, SQLException {
    	
    	if(connect == null){
    		getConnection();
    	}
    	
    	Statement state = connect.createStatement();
    	ResultSet res = state.executeQuery("SELECT * FROM kunden WHERE id = '"+id+"';");
    	return res;
    	
    }
    
    public ResultSet getAllKunden() throws ClassNotFoundException, SQLException {
    	
    	if(connect == null){
    		getConnection();
    	}
    	
    	Statement state = connect.createStatement();
    	ResultSet res = state.executeQuery("SELECT * FROM kunden");
    	return res;
    	
    }
    
    public int addLogbuchEintrag(String art, String anliegen, String bemerkung) throws SQLException, ClassNotFoundException{
    	
    	if(connect == null){
			getConnection();
		}
		
		PreparedStatement state = connect.prepareStatement("INSERT INTO logbuch values(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
		state.setDate(2, new Date(System.currentTimeMillis()));
		state.setString(3, art);
		state.setString(4, anliegen);
		state.setString(5, bemerkung);
		state.execute();
		
		ResultSet rs = state.getGeneratedKeys();
		int newId = 0;
		if(rs.next()){
			newId = rs.getInt(1);
		}
		
		return newId;
    	
    }
    
    public ResultSet getLogbuchEintraege() throws ClassNotFoundException, SQLException {
    	
    	if(connect == null){
    		getConnection();
    	}
    	
    	Statement state = connect.createStatement();
    	ResultSet res = state.executeQuery("SELECT * FROM logbuch");
    	return res;
    	
    }
    
    private void getConnection() throws ClassNotFoundException, SQLException{
    	Class.forName("org.sqlite.JDBC");
    	connect = DriverManager.getConnection("jdbc:sqlite:Kundenverwaltung.db");
    	initialise();
    }
    
    private void initialise() throws SQLException{
    	if(!hasData){
    		
    		Statement state = connect.createStatement();
    		ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='kunden'");
    		
    		if(!res.next()){
    			Statement state2 = connect.createStatement();
    			state2.execute("CREATE TABLE kunden(id integer, "
    						+ "appId varchar(4), "
    						+ "uid varchar(100), "
    						+ "vorname varchar(60), "
    						+ "nachname varchar(60), "
    						+ "geburtsdatum varchar(10), "
    						+ "primary key(id));");
    		}
    		
    		Statement state2 = connect.createStatement();
    		ResultSet res2 = state2.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='logbuch'");
    		
    		if(!res2.next()){
    			Statement state3 = connect.createStatement();
    			state3.execute("CREATE TABLE logbuch(id integer, "
    						+ "datum datetime, "
    						+ "art varchar(100), "
    						+ "anliegen varchar(100), "
    						+ "bemerkung varchar(250), "
    						+ "primary key(id));");
    		}
    		
    		hasData = true;
    	}
    }

}
