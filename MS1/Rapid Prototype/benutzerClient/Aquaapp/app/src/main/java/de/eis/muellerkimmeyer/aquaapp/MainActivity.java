package de.eis.muellerkimmeyer.aquaapp;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *  EISWS1617
 *
 *  Rapid Prototype - Android App (Benutzer Client)
 *
 *  Autor: Moritz Müller, Johannes Kimmeyer
 */

public class MainActivity extends AppCompatActivity {

    //Anlegen aller wichtigen Variablen
    private EditText aqSizeTf;
    private TextView phTv, duengerTv, khTv, co2Tv, fertTv;
    private ServerRequest request;
    private String token;
    private JSONObject wasserwerte;
    private int aquaSize;

    //Testwerte für den Prototyp - für Prototyp zu umfassend, die Werte zu übernehmen

    private float fGoal, amountF, amountAq;

    //Überschreiben der Methode, was passieren soll, wenn das Fenster gestartet wird.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Testwerte für den Prototyp
        fGoal= 1.5f;
        amountF= 200.0f;
        amountAq= 1.1f;

        duengerTv = (TextView) findViewById(R.id.duengerTv);
        phTv = (TextView) findViewById(R.id.phTv);
        khTv = (TextView) findViewById(R.id.khTv);
        co2Tv = (TextView) findViewById(R.id.co2Tv);
        fertTv = (TextView) findViewById(R.id.fertTv);

        /*
         *  Prüfen ob bereits Werte für den Benutzer in der Datenbank stehen
         *  Wenn nicht: Werte werden nicht verändert
         *  Wenn ja: Werte werden  gleich den geladenen Wasserwerten setzen
         *
         * */

        token = FirebaseInstanceId.getInstance().getToken();
        request = new ServerRequest();
        wasserwerte = request.doAsyncRequest("get", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte?token="+token, null);
        calcFertAmount(savedInstanceState);

        try{
            JSONArray ww = wasserwerte.getJSONArray("wasserwerte");
            if(ww.length() > 0){

                //Daten werden aus der Datenbank geholt
                int ph = ww.getJSONObject(ww.length()-1).getInt("ph");
                int KH = ww.getJSONObject(ww.length()-1).getInt("KH");
                String dailyUse = ww.getJSONObject(ww.length()-1).getString("dailyUse");

                //Setzen der neuen Werte ins Textfeld
                co2Tv.setText(Double.toString(calcCo2(KH,ph)) + " mg/l");
                duengerTv.setText(Float.toString((Float.parseFloat(dailyUse))) + " mg");
                phTv.setText(Integer.toString(ph)+"\u00B0");
                khTv.setText(Integer.toString(KH)+"\u00B0");

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
        return Math.round((kh/2.8)*Math.pow(10,(7.91-(double)ph)));
    }

    protected double calcFertilizerAmount(int aqSize, double fGoal, float amountF, float amountAq){
        //Berechnung der Dosierung des Düngemittels mit imaginären Werten(bis auf aqSize alles oben festgelegt)
        return Math.round(aqSize*1000*((fGoal-amountAq)/amountF));
    }

        /**Die Berechnung der Dosierung des Düngemittels findet erst nach Betätigen des Calculate-Buttons statt, da vorher noch kein Wert
        vom Benutzer eingegeben wurde.
         **/
    protected void calcFertAmount(Bundle savedInstanceState){
        aqSizeTf = (EditText) findViewById(R.id.aqSizeTf);
        Button calcButton = (Button) findViewById(R.id.calcButton);
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aquaSize = Integer.parseInt(aqSizeTf.getText().toString());
                fertTv.setText(Double.toString(calcFertilizerAmount(aquaSize, fGoal, amountF, amountAq)) + " ml");
            }
        });

    }
}
