package de.eis.muellerkimmeyer.aquaapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by moritz on 29.10.2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /*
    * QUELLENANGABE
    * sendRegistrationToServer übernommen aus https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    * Autor: "mkyong", Abruf am: 01.11.2016
    * Änderungen: Fehlerbehandlung umstrukturiert, "Content-Type" zum Header hinzugefügt
    */

    private void sendRegistrationToServer(String refreshedToken){

        URL url;

        try{

            url = new URL("http://eis1617.lupus.uberspace.de/nodejs/");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            String urlParameters = "{\"Token\": \""+ refreshedToken +"\"}";
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

        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
