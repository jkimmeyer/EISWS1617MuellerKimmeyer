package eis1617.muellerkimmeyer.app.fragmente;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import eis1617.muellerkimmeyer.app.activities.MainActivity;
import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class AquariumFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    EditText etBezeichnung, etLaenge, etBreite, etHoehe, etFd, etKh, etGs;
    Button btnSpeichern;
    ProgressDialog progressDialog;
    ImageView ivAquarium;

    FirebaseAuth firebaseAuth;
    ServerRequest serverRequest;

    public AquariumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_aquarium, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        etBezeichnung = (EditText) rootView.findViewById(R.id.etName);
        etLaenge = (EditText) rootView.findViewById(R.id.etLaenge);
        etBreite = (EditText) rootView.findViewById(R.id.etBreite);
        etHoehe = (EditText) rootView.findViewById(R.id.etHoehe);
        etFd = (EditText) rootView.findViewById(R.id.etFuellstanddifferenz);
        etGs = (EditText) rootView.findViewById(R.id.etGlasstaerke);
        etKh = (EditText) rootView.findViewById(R.id.etKieshoehe);
        btnSpeichern = (Button) rootView.findViewById(R.id.buttonSpeichern);
        ivAquarium = (ImageView) rootView.findViewById(R.id.ivAquarium);

        etBezeichnung.setOnFocusChangeListener(this);
        etLaenge.setOnFocusChangeListener(this);
        etBreite.setOnFocusChangeListener(this);
        etHoehe.setOnFocusChangeListener(this);
        etFd.setOnFocusChangeListener(this);
        etGs.setOnFocusChangeListener(this);
        etKh.setOnFocusChangeListener(this);

        progressDialog = new ProgressDialog(getActivity());

        serverRequest = new ServerRequest();

        btnSpeichern.setOnClickListener(this);

        loadData();

        /* Am Anfang soll die Tastatur nicht autmoatisch geöffnet werden,
         * da sie sonst die unteren Felder verdeckt und die Übersicht
         * verloren geht.
         */
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        return rootView;
    }

    private void loadData(){
        etBezeichnung.setText(MainActivity.aquarium.getBezeichnung());
        etLaenge.setText(Double.toString(MainActivity.aquarium.getLaenge()));
        etBreite.setText(Double.toString(MainActivity.aquarium.getBreite()));
        etHoehe.setText(Double.toString(MainActivity.aquarium.getHoehe()));
        etGs.setText(Double.toString(MainActivity.aquarium.getGlasstaerke()));
        etKh.setText(Double.toString(MainActivity.aquarium.getKieshoehe()));
        etFd.setText(Double.toString(MainActivity.aquarium.getFuellstanddifferenz()));
    }

    @Override
    public void onClick(View v) {
        if(v == btnSpeichern){
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
                JSONObject response = serverRequest.doAsyncRequest("PUT", "http://eis1617.lupus.uberspace.de/nodejs/aquarien/" + firebaseAuth.getCurrentUser().getUid(), urlParameters);

                try {
                    if (response.getString("success") != null && response.getString("success").toString() != "false") {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Daten erfolgreich gespeichert!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progressDialog.dismiss();
                        Log.w("FirstLoginActivity", "insertAquariumData:failed");
                        Toast.makeText(getContext(), "Ein Fehler ist aufgetreten! Bitte versuchen Sie es später erneut!", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
            else{
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
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
