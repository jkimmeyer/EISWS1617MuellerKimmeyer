package eis1617.muellerkimmeyer.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

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
