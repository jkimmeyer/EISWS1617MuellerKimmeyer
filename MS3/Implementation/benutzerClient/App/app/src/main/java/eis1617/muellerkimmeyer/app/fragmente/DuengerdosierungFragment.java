package eis1617.muellerkimmeyer.app.fragmente;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import eis1617.muellerkimmeyer.app.klassen.LogbuchEintrag;
import eis1617.muellerkimmeyer.app.activities.MainActivity;
import eis1617.muellerkimmeyer.app.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DuengerdosierungFragment extends Fragment implements View.OnClickListener {

    private EditText etVolumenAquarium, etKonzentrationAquarium, etKonzentrationDuenger, etGewuenschteKonzentration;
    private ImageView ivVolumenAquariumInfo, ivKonzentrationAquariumInfo, ivKonzentrationDuengerInfo, ivGewuenschteKonzentrationInfo;
    private Button btnDuengerdosierungBerechnen;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private AlertDialog.Builder dialogBuilder;

    public DuengerdosierungFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_duengerdosierung, container, false);

        etVolumenAquarium = (EditText) rootView.findViewById(R.id.etVolumenAquarium);
        etKonzentrationAquarium = (EditText) rootView.findViewById(R.id.etKonzentrationAquarium);
        etKonzentrationDuenger = (EditText) rootView.findViewById(R.id.etKonzentrationDuenger);
        etGewuenschteKonzentration = (EditText) rootView.findViewById(R.id.etGewuenschteKonzentration);

        ivVolumenAquariumInfo = (ImageView) rootView.findViewById(R.id.ivVolumenAquariumInfo);
        ivKonzentrationAquariumInfo = (ImageView) rootView.findViewById(R.id.ivKonzentrationAquariumInfo);
        ivKonzentrationDuengerInfo = (ImageView) rootView.findViewById(R.id.ivKonzentrationDuengerInfo);
        ivGewuenschteKonzentrationInfo = (ImageView) rootView.findViewById(R.id.ivGewuenschteKonzentrationInfo);

        btnDuengerdosierungBerechnen = (Button) rootView.findViewById(R.id.btnDuengerdosierungBerechnen);

        etVolumenAquarium.setText(Double.toString(MainActivity.aquarium.getNettoVolumenInLiter()));

        ivVolumenAquariumInfo.setOnClickListener(this);
        ivKonzentrationAquariumInfo.setOnClickListener(this);
        ivKonzentrationDuengerInfo.setOnClickListener(this);
        ivGewuenschteKonzentrationInfo.setOnClickListener(this);
        btnDuengerdosierungBerechnen.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialogBuilder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == ivVolumenAquariumInfo){
            makeAlertDialog(R.string.nettowasservolumen_info, "Nettowasservolumen");
        }
        else if(v == ivKonzentrationAquariumInfo){
            makeAlertDialog(R.string.konzentration_aquarium_info, "Konzentration im Aquarium");
        }
        else if(v == ivKonzentrationDuengerInfo){
            makeAlertDialog(R.string.konzentration_duenger_info, "Konzentration im Dünger");
        }
        else if(v == ivGewuenschteKonzentrationInfo){
            makeAlertDialog(R.string.gewuenschte_konzentration_info, "Gewünschte Konzentration");
        }
        else if(v == btnDuengerdosierungBerechnen){
            // Tastatur vor dem wechseln ausblenden, da sie sonst beim Übergang da bleiben würde
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            String nettoWasserVolumen = etVolumenAquarium.getText().toString().trim();
            String konzentrationAquarium = etKonzentrationAquarium.getText().toString().trim();
            String konzentrationDuenger = etKonzentrationDuenger.getText().toString().trim();
            String gewuenschteKonzentration = etGewuenschteKonzentration.getText().toString().trim();
            if(!TextUtils.isEmpty(nettoWasserVolumen) && !TextUtils.isEmpty(konzentrationAquarium) && !TextUtils.isEmpty(konzentrationDuenger) && !TextUtils.isEmpty(gewuenschteKonzentration)){

                int benoetigteMenge = MainActivity.aquarium.getDuengerdosierung(Double.parseDouble(nettoWasserVolumen), Double.parseDouble(konzentrationAquarium), Double.parseDouble(konzentrationDuenger), Double.parseDouble(gewuenschteKonzentration));

                final String message = "Erforderliche Zugabemenge des Flüssigdüngers:\n\n" + benoetigteMenge + " ml";
                dialogBuilder.setMessage(message)
                        .setTitle("Ergebnis")
                        .setPositiveButton("In Logbuch eintragen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addLogbuchEintrag("Düngerdosierung berechnet", R.drawable.ic_logbuch_wasserwechsel, message);
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Zum Logbuch hinzugefügt!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
            else{
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void addLogbuchEintrag(String aktion, int icon, String message){
        Date date = new Date();
        LogbuchEintrag logbuchEintrag = new LogbuchEintrag(aktion,date,icon, message);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("logbuch").child(user.getUid()).child(date.toString()).setValue(logbuchEintrag);
    }

    private void makeAlertDialog(int string, String title){
        dialogBuilder.setMessage(string).setTitle(title).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
