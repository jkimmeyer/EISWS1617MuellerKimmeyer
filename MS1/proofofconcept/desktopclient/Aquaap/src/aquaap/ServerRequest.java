package aquaap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author moritz
 */
public class ServerRequest {
    
    public String json;
    
    public ServerRequest(){

    }
    
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
        HttpURLConnection urlConnection = null;

        try{
            
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            
            connection.setDoOutput(true);
            
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
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
    
}
