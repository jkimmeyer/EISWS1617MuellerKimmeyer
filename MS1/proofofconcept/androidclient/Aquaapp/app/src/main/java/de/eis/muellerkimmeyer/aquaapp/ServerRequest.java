package de.eis.muellerkimmeyer.aquaapp;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by moritz on 28.10.2016.
 */

public class ServerRequest {

    static JSONObject jObj = null;
    static String json = "";

    public ServerRequest(){

    }

    /*
    * QUELLENANGABE
    * getJsonFromUrl übernommen aus http://stackoverflow.com/questions/33691914/cannot-resolve-symbol-httpentity-httpresponse (Antwort 1)
    * Autor: "NaviRamyle", Abruf am: 28.10.2016
    * Änderungen: Rückgabewert nicht String sondern JSONObject
    * Beschreibung: Wird von der Klasse "Request" aufgerufen, um in einem asnychronen Task
    * eine Verbindung herzustellen.
    */

    public JSONObject getJsonFromUrl(String urlString){

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

        try{
            jObj = new JSONObject(json);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return jObj;

    }

    /*
    * QUELLENANGABE
    * getJson, Params und Request übernommen aus https://www.learn2crack.com/2014/04/android-login-registration-nodejs-client.html
    * Autor: Raj Amal, Abruf am: 28.10.2016
    * Änderungen: - Bei Params: NameValuePair ist deprecated, deswegen ContentValues
    *             - Bei Request: Keine Parameterübergabe an getJsonFromUrl, da wir bis jetzt nur einen GET Request machen und keinen POST Request, bei dem Parameter übergeben werden müssten
    */

    JSONObject jsonObj;
    public JSONObject getJson(String url, ContentValues params){

        Params param = new Params(url,params);
        Request myTask = new Request();

        try{
            jsonObj = myTask.execute(param).get();
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
        String url;
        ContentValues params = new ContentValues();

        Params(String url, ContentValues params){
            this.url = url;
            this.params = params;
        }
    }

    private class Request extends AsyncTask<Params, String, JSONObject>{

        @Override
        protected JSONObject doInBackground(Params... args){
            ServerRequest request = new ServerRequest();
            JSONObject json = request.getJsonFromUrl(args[0].url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json){

            super.onPostExecute(json);

        }
    }

}
