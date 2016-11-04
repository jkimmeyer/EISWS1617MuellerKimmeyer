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
 *  Autor: Moritz Müller, Johannes Kimmeyer
 */

public class MainActivity extends AppCompatActivity {

    private TextView phTv, duengerTv,khTv,co2Tv ;
    private ServerRequest request;
    private String token;
    private JSONObject wasserwerte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        duengerTv = (TextView) findViewById(R.id.duengerTv);
        phTv = (TextView) findViewById(R.id.phTv);
        khTv = (TextView) findViewById(R.id.khTv);
        co2Tv = (TextView) findViewById(R.id.co2Tv);

        /*
         *  Prüfen ob bereits Wasserwerte für den Benutzer in der Datenbank stehen
         *  Wenn nicht: phTv und kalziumTv nicht verändern
         *  Wenn ja: phTv und kalziumTv gleich den geladenen Wasserwerten setzen
         *
         * */

        token = FirebaseInstanceId.getInstance().getToken();
        request = new ServerRequest();
        wasserwerte = request.doAsyncRequest("get", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte?token="+token, null);

        try{
            JSONArray ww = wasserwerte.getJSONArray("wasserwerte");
            if(ww.length() > 0){
                int ph = ww.getJSONObject(ww.length()-1).getInt("ph");
                int KH = ww.getJSONObject(ww.length()-1).getInt("KH");
                long dailyUse = ww.getJSONObject(ww.length()-1).getLong("dailyUse");


                co2Tv.setText(Double.toString(calcCo2(KH,ph)));
                duengerTv.setText(Long.toString(dailyUse));
                phTv.setText(Integer.toString(ph));
                khTv.setText(Integer.toString(KH));

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }
    protected double calcCo2(int kh, int ph){
        /**Berechnung des Co2 Gehalts pro Liter
         Formel: (KH/2,8)*10^7,91-pH
         **/
        return (kh/2.8)*Math.pow(10,(7.91-(double)ph));
    }
}
