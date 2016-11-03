package de.eis.muellerkimmeyer.aquaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button absendenBtn;
    private TextView textView, phTv, kalziumTv;
    private ServerRequest sr;
    private String token;
    private JSONObject wasserwerte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sr = new ServerRequest();

        absendenBtn = (Button)findViewById(R.id.absenden);
        textView = (TextView)findViewById(R.id.textView);
        phTv = (TextView) findViewById(R.id.phTv);
        kalziumTv = (TextView) findViewById(R.id.kalziumTv);

        // Prüfen ob Wasserwerte in DB stehen

        token = FirebaseInstanceId.getInstance().getToken();

        wasserwerte = sr.doAsyncRequest("get", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte?token="+token, null);

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

        absendenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.doAsyncRequest("get", "http://eis1617.lupus.uberspace.de/nodejs/", null);
                if(json != null){
                    try{
                        String response = json.getJSONArray("users").getJSONObject(0).getString("token");
                        textView.setText(response);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }
}
