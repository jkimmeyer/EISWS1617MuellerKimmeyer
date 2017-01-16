package eis1617.muellerkimmeyer.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import eis1617.muellerkimmeyer.app.activities.FirstLoginActivity;
import eis1617.muellerkimmeyer.app.activities.MainActivity;
import eis1617.muellerkimmeyer.app.activities.RegisterActivity;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ServerRequest serverRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        serverRequest = new ServerRequest();

        if(firebaseAuth.getCurrentUser() != null){

            /* Prüfen ob bereits ein Aquarium eingetragen wurde.
             * Falls nicht, muss es der erste Login sein
             * und somit wird die FirstLoginActivity
             * aufgerufen.
             */
            JSONObject response = serverRequest.doAsyncRequest("GET", "http://eis1617.lupus.uberspace.de/nodejs/aquarien/" + firebaseAuth.getCurrentUser().getUid(), null);

            try{
                if(response.getJSONArray("aquarien").length() == 0){
                    startActivity(new Intent(getApplicationContext(), FirstLoginActivity.class));
                }
                else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
        else{
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
        finish();
    }

}
