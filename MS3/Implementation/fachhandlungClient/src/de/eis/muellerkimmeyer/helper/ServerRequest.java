package de.eis.muellerkimmeyer.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz Müller
 */

public class ServerRequest {
    
    public String json;
    
    public ServerRequest(){

    }
    
    /*
    * QUELLENANGABE
    * sendGet und sendPost übernommen aus https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    * Autor: "mkyong", Abruf am: 01.11.2016
    * Änderungen: Fehlerbehandlung umstrukturiert, bei sendPost "Content-Type" zum Header hinzugefügt, Abfragen des Status Codes
    */
    
    // HTTP GET request
    
    public String sendGet(String urlString){
        
        URL url;
        HttpURLConnection urlConnection = null;

        try{
            url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            BufferedReader br = new BufferedReader(isw);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            br.close();

            json = sb.toString();
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        return json;
        
    }
    
    // HTTP POST request
    public String sendPost(String urlString, String urlParameters) {
        
        URL url;

        try{
            
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            
            connection.setDoOutput(true);
            
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            
            if(connection.getResponseCode() == 200) {
            
	            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String line;
	            StringBuilder sb = new StringBuilder();
	
	            while ((line = br.readLine()) != null) {
	                sb.append(line+"\n");
	            }
	            
	            json = sb.toString();
	            
	            br.close();
            }else{
                json = "{ success: \"false\" }";
            }
            
            wr.flush();
            wr.close();
            
            
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        return json;

    }
    
}