package de.eis.muellerkimmeyer.aquaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button absendenBtn;
    private TextView textView, androidIDtv;
    private ServerRequest sr;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sr = new ServerRequest();

        absendenBtn = (Button)findViewById(R.id.absenden);
        textView = (TextView)findViewById(R.id.textView);
        androidIDtv = (TextView) findViewById(R.id.androidIDtv);

        absendenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJson("http://eis1617.lupus.uberspace.de/nodejs/", null);
                if(json != null){
                    try{
                        String response = json.getString("response");
                        textView.setText(response);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();
    }
}
