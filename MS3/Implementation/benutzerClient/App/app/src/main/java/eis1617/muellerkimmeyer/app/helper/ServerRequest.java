package eis1617.muellerkimmeyer.app.helper;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Android App (Benutzer Client)
 *
 *  Autor: Moritz Müller
 */

public class ServerRequest {

    public ServerRequest(){}

    /*
    * QUELLENANGABE
    * sendRequest und sendGet übernommen aus https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    * Autor: "mkyong", Abruf am: 01.11.2016
    * Änderungen: Fehlerbehandlung umstrukturiert, bei sendRequest "Content-Type" zum Header hinzugefügt,
    * JSONObject als Rückgabewert, PUT und DELETE hinzugefügt, Statuscode-Abfrage
    */

    // HTTP POST, PUT, DELETE request
    public JSONObject sendRequest(String method, String urlString, String urlParameters) {

        URL url;
        JSONObject jObj = null;
        String json = "";

        try{

            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);

            // Ein BufferedReader kann nur bei einem 200er Statuscode verwendet werden,
            // deshalb muss dieser vorher abgefragt werden. Wenn es ein 400 oder höher
            // Code ist, wird das JSONObject manuell gesetzt

            if(connection.getResponseCode() == 200) {

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                jObj = new JSONObject(json);

                br.close();
            }else{
                json = "{ success: \"false\" }";
                jObj = new JSONObject(json);
            }
            wr.flush();
            wr.close();

        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return jObj;

    }

    public JSONObject sendGet(String urlString){

        URL url;
        HttpURLConnection urlConnection = null;
        JSONObject jObj = null;
        String json = "";

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
            jObj = new JSONObject(json);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return jObj;

    }

    public JSONObject doAsyncRequest(String method, String url, String urlParams){

        JSONObject jsonObj = null;
        Request request = new Request();
        Params params = new Params(method, url, urlParams);

        try{
            jsonObj = request.execute(params).get();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }

        return jsonObj;

    }

    private static class Params {

        String method;
        String url;
        String params;

        Params(String method, String url, String params){
            this.method = method;
            this.url = url;
            this.params = params;
        }
    }

    private class Request extends AsyncTask<Params, String, JSONObject>{

        @Override
        protected JSONObject doInBackground(Params... args){
            ServerRequest request = new ServerRequest();
            JSONObject json = null;
            if(args[0].method == "GET"){
                json = request.sendGet(args[0].url);
            }
            else{
                json = request.sendRequest(args[0].method, args[0].url, args[0].params);
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json){

            super.onPostExecute(json);

        }
    }

}
