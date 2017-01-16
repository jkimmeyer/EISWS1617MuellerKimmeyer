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
public class CO2BerechnungFragment extends Fragment implements View.OnClickListener{

    private EditText etKh, etPh;
    private ImageView ivKhInfo, ivPhInfo;
    private Button btnCO2Berechnen;

    private AlertDialog.Builder dialogBuilder;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;


    public CO2BerechnungFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_co2_berechnung, container, false);

        etKh = (EditText) rootView.findViewById(R.id.etKh);
        etPh = (EditText) rootView.findViewById(R.id.etPh);

        ivKhInfo = (ImageView) rootView.findViewById(R.id.ivKhInfo);
        ivPhInfo = (ImageView) rootView.findViewById(R.id.ivPhInfo);

        btnCO2Berechnen = (Button) rootView.findViewById(R.id.btnCO2Berechnen);

        ivKhInfo.setOnClickListener(this);
        ivPhInfo.setOnClickListener(this);
        btnCO2Berechnen.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialogBuilder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == ivKhInfo){
            makeAlertDialog(R.string.kh_info, "Karbonathärte (KH)");
        }
        else if(v == ivPhInfo){
            makeAlertDialog(R.string.ph_info, "pH-Wert");
        }
        else if(v == btnCO2Berechnen){

            // Tastatur vor dem wechseln ausblenden, da sie sonst beim Übergang da bleiben würde
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String kh = etKh.getText().toString().trim();
            String ph = etPh.getText().toString().trim();
            if(!TextUtils.isEmpty(kh) && !TextUtils.isEmpty(ph)){
                int co2 = MainActivity.aquarium.getCO2Gehalt(Double.parseDouble(kh), Double.parseDouble(ph));

                final String message = "Der CO2-Gehalt beträgt ungefähr:\n\n" + co2 + " mg/l: ";
                dialogBuilder.setMessage(message)
                        .setTitle("Ergebnis")
                        .setPositiveButton("In Logbuch eintragen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addLogbuchEintrag("CO2-Gehalt berechnet", R.drawable.ic_co2, message);
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
        LogbuchEintrag logbuchEintrag = new LogbuchEintrag(aktion,date,icon,message);
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
