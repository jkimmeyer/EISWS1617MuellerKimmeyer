package eis1617.muellerkimmeyer.app;


import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WasserwechselFragment extends Fragment implements View.OnClickListener {

    private EditText etVolumenAquarium, etGewuenschteMenge, etKhGhLw, etKhGhAq, etGewuenschteKhGh, etWirkungsgradRE;
    private ImageView ivVolumenAquariumInfo, ivGewuenschteMengeInfo, ivKhGhLwInfo, ivKhGhAqInfo, ivGewuenschteKhGhInfo, ivWirkungsgradREInfo;
    private Button btnWasserwechselBerechnen;

    private AlertDialog.Builder dialogBuilder;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public WasserwechselFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wasserwechsel, container, false);

        etVolumenAquarium = (EditText) rootView.findViewById(R.id.etVolumenAquarium);
        etGewuenschteMenge = (EditText) rootView.findViewById(R.id.etGewuenschteMenge);
        etKhGhLw = (EditText) rootView.findViewById(R.id.etKhGhLw);
        etKhGhAq = (EditText) rootView.findViewById(R.id.etKhGhAq);
        etGewuenschteKhGh = (EditText) rootView.findViewById(R.id.etGewuenschteKhGh);
        etWirkungsgradRE = (EditText) rootView.findViewById(R.id.etWirkungsgradRE);

        ivVolumenAquariumInfo = (ImageView) rootView.findViewById(R.id.ivVolumenAquariumInfo);
        ivGewuenschteMengeInfo = (ImageView) rootView.findViewById(R.id.ivGewuenschteMengeInfo);
        ivKhGhLwInfo = (ImageView) rootView.findViewById(R.id.ivKhGhLwInfo);
        ivKhGhAqInfo = (ImageView) rootView.findViewById(R.id.ivKhGhAqInfo);
        ivGewuenschteKhGhInfo = (ImageView) rootView.findViewById(R.id.ivGewuenschteKhGhInfo);
        ivWirkungsgradREInfo = (ImageView) rootView.findViewById(R.id.ivWirkungsgradREInfo);

        btnWasserwechselBerechnen = (Button) rootView.findViewById(R.id.btnWasserwechselBerechnen);

        etVolumenAquarium.setText(Double.toString(MainActivity.aquarium.getNettoVolumenInLiter()));

        ivVolumenAquariumInfo.setOnClickListener(this);
        ivGewuenschteMengeInfo.setOnClickListener(this);
        ivKhGhLwInfo.setOnClickListener(this);
        ivKhGhAqInfo.setOnClickListener(this);
        ivGewuenschteKhGhInfo.setOnClickListener(this);
        ivWirkungsgradREInfo.setOnClickListener(this);

        btnWasserwechselBerechnen.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void addLogbuchEintrag(String aktion, int icon, String message){
        Date date = new Date();
        LogbuchEintrag logbuchEintrag = new LogbuchEintrag(aktion,date,icon, message);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("logbuch").child(user.getUid()).child(date.toString()).setValue(logbuchEintrag);
    }

    @Override
    public void onClick(View v) {
        if(v == ivVolumenAquariumInfo){
            makeAlertDialog(R.string.nettowasservolumen_info, "Nettowasservolumen");
        }
        else if(v == ivGewuenschteMengeInfo){
            makeAlertDialog(R.string.gewuenschte_menge_info, "Gewünschte Wasserwechselmenge");
        }
        else if(v == ivKhGhLwInfo){
            makeAlertDialog(R.string.khghlw_info, "KH / GH im Leitungswasser");
        }
        else if(v == ivKhGhAqInfo){
            makeAlertDialog(R.string.khghaq_info, "KH / GH im Aquarium");
        }
        else if(v == ivGewuenschteKhGhInfo){
            makeAlertDialog(R.string.gewuenschte_khgh_info, "Gewünschte KH / GH");
        }
        else if(v == ivWirkungsgradREInfo){
            makeAlertDialog(R.string.wirkungsgrad_re_info, "Wirkungsgrad Reinwassererzeugung");
        }
        else if(v == btnWasserwechselBerechnen){
            // Tastatur vor dem wechseln ausblenden, da sie sonst beim Übergang da bleiben würde
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            String nettoWasserVolumen = etVolumenAquarium.getText().toString().trim();
            String gewuenschteMenge = etGewuenschteMenge.getText().toString().trim();
            String khGhLw = etKhGhLw.getText().toString().trim();
            String khGhAq = etKhGhAq.getText().toString().trim();
            String gewuenschteKhGh = etGewuenschteKhGh.getText().toString().trim();
            String wirkungsgradRE = etWirkungsgradRE.getText().toString().trim();
            if(!TextUtils.isEmpty(nettoWasserVolumen) && !TextUtils.isEmpty(gewuenschteMenge) && !TextUtils.isEmpty(khGhLw) && !TextUtils.isEmpty(khGhAq) && !TextUtils.isEmpty(gewuenschteKhGh) && !TextUtils.isEmpty(wirkungsgradRE)){
                double[] wassermengen = MainActivity.aquarium.getBenoetigteWassermengen(Double.parseDouble(nettoWasserVolumen), Double.parseDouble(gewuenschteMenge), Double.parseDouble(khGhLw), Double.parseDouble(khGhAq), Double.parseDouble(gewuenschteKhGh), Double.parseDouble(wirkungsgradRE));
                double leitungswasser = wassermengen[0];
                double reinwasser = wassermengen[1];
                dialogBuilder = new AlertDialog.Builder(getActivity());
                final String message = "Leitungswasser: " + leitungswasser + " Liter\nReinwasser: " + reinwasser + " Liter";
                dialogBuilder.setMessage(message)
                             .setTitle("Ergebnis")
                             .setPositiveButton("In Logbuch eintragen", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     addLogbuchEintrag("Wasserwechsel berechnet", R.drawable.ic_logbuch_wasserwechsel, message);
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

    private void makeAlertDialog(int string, String title){
        dialogBuilder = new AlertDialog.Builder(getActivity());
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
