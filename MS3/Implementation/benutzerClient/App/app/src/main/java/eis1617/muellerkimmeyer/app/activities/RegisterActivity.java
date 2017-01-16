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

import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.klassen.UserInformation;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvLogin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private ServerRequest serverRequest;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        serverRequest = new ServerRequest();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvLogin = (TextView) findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    private void saveUserInformation(String sid){
        String token = FirebaseInstanceId.getInstance().getToken();
        UserInformation userInformation = new UserInformation(sid, token);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("user").child(user.getUid()).setValue(userInformation);
    }

    private void registerUser(){
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

        progressDialog.setMessage("Registrierung läuft...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){

                    // Benutzer auf unserem Server anlegen

                    String token = FirebaseInstanceId.getInstance().getToken();
                    String urlParameters = "{\"UID\": \""+ firebaseAuth.getCurrentUser().getUid() +"\", \"token\": \"" + token + "\"}";
                    JSONObject response = serverRequest.doAsyncRequest("POST", "http://eis1617.lupus.uberspace.de/nodejs/users", urlParameters);

                    String sid = "";
                    try {
                        if (response.getString("sid") != null) {
                            sid = response.getString("sid");

                            //sid in Firebase Database speichern
                            saveUserInformation(sid);
                        }
                        else{
                            Log.w("RegisterActivity", "insertUserData:failed");
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                    finish();
                    startActivity(new Intent(getApplicationContext(), FirstLoginActivity.class));
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Registrierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            // Tastatur vor dem wechseln ausblenden, da sie sonst beim Übergang da bleiben würde
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            registerUser();
        }
        else if(v == tvLogin){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}