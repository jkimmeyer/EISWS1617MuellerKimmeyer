package eis1617.muellerkimmeyer.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ServerRequest serverRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        serverRequest = new ServerRequest();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    private void updateUserInformation(){
        String token = FirebaseInstanceId.getInstance().getToken();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user/" + user.getUid() + "/token/", token);
        databaseReference.updateChildren(childUpdates);
    }

    private void userLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Bitte E-Mail Adresse angeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Bitte Passwort angeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Login läuft...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    // Benutzer-Token auf unserem Server aktualisieren

                    String token = FirebaseInstanceId.getInstance().getToken();
                    String urlParameters = "{\"UID\": \""+ firebaseAuth.getCurrentUser().getUid() +"\", \"token\": \"" + token + "\"}";
                    JSONObject response = serverRequest.doAsyncRequest("PUT", "http://eis1617.lupus.uberspace.de/nodejs/users", urlParameters);

                    // Benutzer-Token in der Firebase Databse aktualisieren

                    try {
                        if (response.getString("success") != null) {
                            updateUserInformation();
                        }
                        else{
                            Log.w("LoginActivity", "updateUserData:failed");
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                    /* Prüfen ob bereits ein Aquarium eingetragen wurde.
                     * Falls nicht, muss es der erste Login sein
                     * und somit wird die FirstLoginActivity
                     * aufgerufen.
                     */
                    JSONObject response2 = serverRequest.doAsyncRequest("GET", "http://eis1617.lupus.uberspace.de/nodejs/aquarien/" + firebaseAuth.getCurrentUser().getUid(), null);

                    try{
                        if(response2.getJSONArray("aquarien").length() == 0){
                            startActivity(new Intent(getApplicationContext(), FirstLoginActivity.class));
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();
                    finish();

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            // Tastatur vor dem wechseln ausblenden, da sie sonst beim Übergang da bleiben würde
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            userLogin();
        }
        else if(v == tvRegister){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }
}
