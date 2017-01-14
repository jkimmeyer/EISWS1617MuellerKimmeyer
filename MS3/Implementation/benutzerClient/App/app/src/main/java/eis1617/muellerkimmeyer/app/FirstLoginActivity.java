package eis1617.muellerkimmeyer.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText etBezeichnung, etLaenge, etBreite, etHoehe, etFd, etKh, etGs;
    Button btnWeiter;
    TextView tvLogout;
    ProgressDialog progressDialog;
    ImageView ivAquarium;

    FirebaseAuth firebaseAuth;
    ServerRequest serverRequest;

    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


        etBezeichnung = (EditText) findViewById(R.id.etName);
        etLaenge = (EditText) findViewById(R.id.etLaenge);
        etBreite = (EditText) findViewById(R.id.etBreite);
        etHoehe = (EditText) findViewById(R.id.etHoehe);
        etFd = (EditText) findViewById(R.id.etFuellstanddifferenz);
        etGs = (EditText) findViewById(R.id.etGlasstaerke);
        etKh = (EditText) findViewById(R.id.etKieshoehe);
        btnWeiter = (Button) findViewById(R.id.buttonWeiter);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        ivAquarium = (ImageView) findViewById(R.id.ivAquarium);

        etBezeichnung.setOnFocusChangeListener(this);
        etLaenge.setOnFocusChangeListener(this);
        etBreite.setOnFocusChangeListener(this);
        etHoehe.setOnFocusChangeListener(this);
        etFd.setOnFocusChangeListener(this);
        etGs.setOnFocusChangeListener(this);
        etKh.setOnFocusChangeListener(this);

        progressDialog = new ProgressDialog(this);

        serverRequest = new ServerRequest();

        btnWeiter.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.welcome_message).setTitle("Herzlich Willkommen!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        /* Am Anfang soll die Tastatur nicht autmoatisch geöffnet werden,
         * da sie sonst die unteren Felder verdeckt und die Übersicht
         * verloren geht.
         */
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

    }

    @Override
    public void onClick(View v) {
        if(v == tvLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if(v == btnWeiter){

            String bezeichnung = etBezeichnung.getText().toString().trim();
            String laenge = etLaenge.getText().toString().trim();
            String breite = etBreite.getText().toString().trim();
            String hoehe = etHoehe.getText().toString().trim();
            String glasstaerke = etGs.getText().toString().trim();
            String kieshoehe = etKh.getText().toString().trim();
            String fd = etFd.getText().toString().trim();

            if(bezeichnung.length() > 0 && laenge.length() > 0 && breite.length() > 0 && hoehe.length() > 0 && glasstaerke.length() > 0 && kieshoehe.length() > 0 && fd.length() > 0){

                progressDialog.setMessage("Daten werden verarbeitet...");
                progressDialog.show();

                String urlParameters = "{\"bezeichnung\": \"" + bezeichnung + "\", \"laenge\": \"" + Double.parseDouble(laenge) + "\", \"breite\": \"" + Double.parseDouble(breite) + "\", \"hoehe\": \"" + Double.parseDouble(hoehe) + "\", \"glasstaerke\": \"" + Double.parseDouble(glasstaerke) + "\", \"kieshoehe\": \"" + Double.parseDouble(kieshoehe) + "\", \"fd\": \"" + Double.parseDouble(fd) + "\"}";
                JSONObject response = serverRequest.doAsyncRequest("POST", "http://eis1617.lupus.uberspace.de/nodejs/aquarien/" + firebaseAuth.getCurrentUser().getUid(), urlParameters);

                try {
                    if (response.getString("success") != null && response.getString("success").toString() != "false") {
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    else{
                        progressDialog.dismiss();
                        Log.w("FirstLoginActivity", "insertAquariumData:failed");
                        Toast.makeText(this, "Ein Fehler ist aufgetreten! Bitte versuchen Sie es später erneut!", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
            else{
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            if(v == etBezeichnung || v == etGs){
                ivAquarium.setImageResource(R.drawable.aquarium);
            }
            else if(v == etLaenge){
                ivAquarium.setImageResource(R.drawable.aquarium_laenge);
            }
            else if(v == etBreite){
                ivAquarium.setImageResource(R.drawable.aquarium_breite);
            }
            else if(v == etHoehe){
                ivAquarium.setImageResource(R.drawable.aquarium_hoehe);
            }
            else if(v == etKh){
                ivAquarium.setImageResource(R.drawable.aquarium_kiesmenge);
            }
            else if(v == etFd){
                ivAquarium.setImageResource(R.drawable.aquarium_fd);
            }
        }
    }
}
