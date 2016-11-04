package de.eis.muellerkimmeyer.aquaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Android App (Benutzer Client)
 *
 *  Autor: Moritz Müller
 */

public class MainActivity extends AppCompatActivity {

    private TextView phTv, kalziumTv;
    private ServerRequest request;
    private String token;
    private JSONObject wasserwerte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phTv = (TextView) findViewById(R.id.phTv);
        kalziumTv = (TextView) findViewById(R.id.kalziumTv);

        /*
         *  Prüfen ob bereits Wasserwerte für den Benutzer in der Datenbank stehen
         *  Wenn nicht: phTv und kalziumTv nicht verändern
         *  Wenn ja: phTv und kalziumTv gleich den geladenen Wasserwerten setzen
         */

        token = FirebaseInstanceId.getInstance().getToken();
        request = new ServerRequest();
        wasserwerte = request.doAsyncRequest("get", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte?token="+token, null);

        try{
            JSONArray ww = wasserwerte.getJSONArray("wasserwerte");
            if(ww.length() > 0){
                int ph = ww.getJSONObject(ww.length()-1).getInt("ph");
                int kalzium = ww.getJSONObject(ww.length()-1).getInt("kalzium");
                phTv.setText(Integer.toString(ph));
                kalziumTv.setText(Integer.toString(kalzium));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }
}
