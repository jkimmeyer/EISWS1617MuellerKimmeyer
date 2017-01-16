package eis1617.muellerkimmeyer.app.fragmente;


import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.helper.RvAdapterWasserwerte;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;
import eis1617.muellerkimmeyer.app.klassen.WasserwerteEintrag;


/**
 * A simple {@link Fragment} subclass.
 */
public class WasserwerteFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RvAdapterWasserwerte rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private ArrayList<WasserwerteEintrag> eintraege;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ServerRequest serverRequest;

    private static EditText etKh, etGh, etPh, etCo2, etEisen, etKalium, etNo3, etPo3;
    private ImageView ivKhDiagramm, ivGhDiagramm, ivPhDiagramm, ivCo2Diagramm, ivEisenDiagramm, ivKaliumDiagramm, ivNo3Diagramm, ivPo3Diagramm;
    private Button btnNeueWasserwerteEintragen;

    private AlertDialog.Builder dialogBuilder;

    private Paint paint = new Paint();

    public WasserwerteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wasserwerte, container, false);

        etKh = (EditText) rootView.findViewById(R.id.etKh);
        etGh = (EditText) rootView.findViewById(R.id.etGh);
        etPh = (EditText) rootView.findViewById(R.id.etPh);
        etCo2 = (EditText) rootView.findViewById(R.id.etCo2);
        etEisen = (EditText) rootView.findViewById(R.id.etEisen);
        etKalium = (EditText) rootView.findViewById(R.id.etKalium);
        etNo3 = (EditText) rootView.findViewById(R.id.etNo3);
        etPo3 = (EditText) rootView.findViewById(R.id.etPo3);

        ivKhDiagramm = (ImageView) rootView.findViewById(R.id.ivKhDiagramm);
        ivGhDiagramm = (ImageView) rootView.findViewById(R.id.ivGhDiagramm);
        ivPhDiagramm = (ImageView) rootView.findViewById(R.id.ivPhDiagramm);
        ivCo2Diagramm = (ImageView) rootView.findViewById(R.id.ivCo2Diagramm);
        ivEisenDiagramm = (ImageView) rootView.findViewById(R.id.ivEisenDiagramm);
        ivKaliumDiagramm = (ImageView) rootView.findViewById(R.id.ivKaliumDiagramm);
        ivNo3Diagramm = (ImageView) rootView.findViewById(R.id.ivNo3Diagramm);
        ivPo3Diagramm = (ImageView) rootView.findViewById(R.id.ivPo3Diagramm);

        btnNeueWasserwerteEintragen = (Button) rootView.findViewById(R.id.btnNeueWasserwerteEintragen);
        btnNeueWasserwerteEintragen.setOnClickListener(this);
        ivKhDiagramm.setOnClickListener(this);
        ivGhDiagramm.setOnClickListener(this);
        ivPhDiagramm.setOnClickListener(this);
        ivCo2Diagramm.setOnClickListener(this);
        ivEisenDiagramm.setOnClickListener(this);
        ivKaliumDiagramm.setOnClickListener(this);
        ivNo3Diagramm.setOnClickListener(this);
        ivPo3Diagramm.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        serverRequest = new ServerRequest();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvWasserwerte);
        rvLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);

        dialogBuilder = new AlertDialog.Builder(getActivity());

        setUpItemTouchHelper();

        loadEintraege();

        /* Am Anfang soll die Tastatur nicht autmoatisch geöffnet werden,
         * da sie sonst die unteren Felder verdeckt und die Übersicht
         * verloren geht.
         */
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setUpItemTouchHelper(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datumStr = dateFormat.format(eintraege.get(position).datum);
                rvAdapter.removeItem(position);
                String urlParameters = "{\"datum\": \""+ datumStr +"\"}";
                serverRequest.doAsyncRequest("DELETE", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte/"+firebaseAuth.getCurrentUser().getUid(), urlParameters);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float marginRight = 10;

                    paint.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,paint);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_forever);
                    RectF icon_dest = new RectF((float) Math.max(itemView.getRight() - marginRight - height, itemView.getRight() + dX), itemView.getTop(), itemView.getRight() - marginRight, itemView.getBottom());
                    c.drawBitmap(icon,null,icon_dest,paint);

                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadEintraege(){
        eintraege = new ArrayList<>();

        JSONObject response = serverRequest.doAsyncRequest("GET", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte/"+ firebaseUser.getUid(), null);

        try {
            if (response.getString("success") != null && response.getString("success").toString() != "false") {
                JSONArray entries = response.getJSONArray("wasserwerte");

                for(int i = 0; i < entries.length(); i++){
                    String von = entries.getJSONObject(i).getString("von");
                    String datumStr = entries.getJSONObject(i).getString("datum");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date datum = dateFormat.parse(datumStr);

                    double kh = entries.getJSONObject(i).getDouble("kh");
                    double gh = entries.getJSONObject(i).getDouble("gh");
                    double ph = entries.getJSONObject(i).getDouble("ph");
                    double co2 = entries.getJSONObject(i).getDouble("co2");
                    double eisen = entries.getJSONObject(i).getDouble("eisen");
                    double kalium = entries.getJSONObject(i).getDouble("kalium");
                    double no3 = entries.getJSONObject(i).getDouble("no3");
                    double po3 = entries.getJSONObject(i).getDouble("po3");

                    WasserwerteEintrag eintrag = new WasserwerteEintrag(von, datum, kh, gh, ph, co2, eisen, kalium, no3, po3);
                    eintraege.add(eintrag);
                }
            }
            else{
                Log.w("WasserwerteFragment", "insertWasserwerte:failed");
                Toast.makeText(getContext(), "Fehler beim Eintragen der Daten!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Die Textfelder mit den Werten des neusten Eintrags füllen
        if(eintraege.size() > 0) {
            setTextFields(eintraege.get(eintraege.size() - 1));
        }

        rvAdapter = new RvAdapterWasserwerte(eintraege);
        recyclerView.setAdapter(rvAdapter);

    }

    public static void setTextFields(WasserwerteEintrag eintrag){
        etKh.setText(String.valueOf(eintrag.kh));
        etGh.setText(String.valueOf(eintrag.gh));
        etPh.setText(String.valueOf(eintrag.ph));
        etCo2.setText(String.valueOf(eintrag.co2));
        etEisen.setText(String.valueOf(eintrag.eisen));
        etKalium.setText(String.valueOf(eintrag.kalium));
        etNo3.setText(String.valueOf(eintrag.no3));
        etPo3.setText(String.valueOf(eintrag.po3));
    }

    public static void clearTextFields(){
        etKh.setText("");
        etGh.setText("");
        etPh.setText("");
        etCo2.setText("");
        etEisen.setText("");
        etKalium.setText("");
        etNo3.setText("");
        etPo3.setText("");
    }

    @Override
    public void onClick(View v) {
        if(v == btnNeueWasserwerteEintragen){

            // Tastatur beim Klick ausblenden, damit man sieht, wie ein neuer Eintrag dazu gekommen ist
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String von = "Benutzer";
            Date datum = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datumStr = dateFormat.format(datum);
            String kh = etKh.getText().toString().trim();
            String gh = etGh.getText().toString().trim();
            String ph = etPh.getText().toString().trim();
            String co2 = etCo2.getText().toString().trim();
            String eisen = etEisen.getText().toString().trim();
            String kalium = etKalium.getText().toString().trim();
            String no3 = etNo3.getText().toString().trim();
            String po3 = etPo3.getText().toString().trim();

            if(!TextUtils.isEmpty(kh) && !TextUtils.isEmpty(gh) && !TextUtils.isEmpty(ph) && !TextUtils.isEmpty(co2) && !TextUtils.isEmpty(eisen) && !TextUtils.isEmpty(kalium) && !TextUtils.isEmpty(no3) && !TextUtils.isEmpty(po3)){
                String urlParameters = "{\"von\": \"" + von + "\", \"datum\": \"" + datumStr + "\", \"kh\": \"" + Double.parseDouble(kh) + "\", \"gh\": \"" + Double.parseDouble(gh) + "\", \"ph\": \"" + Double.parseDouble(ph) + "\", \"co2\": \"" + Double.parseDouble(co2) + "\", \"eisen\": \"" + Double.parseDouble(eisen) + "\", \"kalium\": \"" + Double.parseDouble(kalium) + "\", \"no3\": \"" + Double.parseDouble(no3) + "\", \"po3\": \"" + Double.parseDouble(po3) + "\"}";
                JSONObject response = serverRequest.doAsyncRequest("POST", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte/"+ firebaseUser.getUid(), urlParameters);

                try {
                    if (response.getString("success") != null && response.getString("success") != "false") {
                        WasserwerteEintrag eintrag = new WasserwerteEintrag(von,datum,Double.parseDouble(kh),Double.parseDouble(gh),Double.parseDouble(ph),Double.parseDouble(co2),Double.parseDouble(eisen),Double.parseDouble(kalium),Double.parseDouble(no3),Double.parseDouble(po3));
                        rvAdapter.insertItem(eintrag);
                        recyclerView.scrollToPosition(0);
                        Toast.makeText(getContext(), "Erfolgreich eingetragen!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.w("WasserwerteFragment", "insertWasserwerte:failed");
                        Toast.makeText(getContext(), "Fehler beim Eintragen der Daten!", Toast.LENGTH_SHORT).show();
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
        else if(v == ivKhDiagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).kh;
            }
            makeGraphDialog(values, "KH Verlauf", "dH°", getWertDurchschnitt("kh"));
        }
        else if(v == ivGhDiagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).gh;
            }
            makeGraphDialog(values, "GH Verlauf", "dH°", getWertDurchschnitt("gh"));
        }
        else if(v == ivPhDiagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).ph;
            }
            makeGraphDialog(values, "pH-Wert Verlauf", "pH", getWertDurchschnitt("ph"));
        }
        else if(v == ivCo2Diagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).co2;
            }
            makeGraphDialog(values, "CO2 Verlauf", "mg/l", getWertDurchschnitt("co2"));
        }
        else if(v == ivEisenDiagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).eisen;
            }
            makeGraphDialog(values, "Eisen Verlauf", "mg/l", getWertDurchschnitt("eisen"));
        }
        else if(v == ivKaliumDiagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).kalium;
            }
            makeGraphDialog(values, "Kalium Verlauf", "mg/l", getWertDurchschnitt("kalium"));
        }
        else if(v == ivNo3Diagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).no3;
            }
            makeGraphDialog(values, "NO3 Verlauf", "mg/l", getWertDurchschnitt("no3"));
        }
        else if(v == ivPo3Diagramm){
            double[] values = new double[eintraege.size()];
            for(int i = 0; i < eintraege.size(); i++){
                values[i] = eintraege.get(i).po3;
            }
            makeGraphDialog(values, "PO3 Verlauf", "mg/l", getWertDurchschnitt("po3"));
        }

    }

    private double getWertDurchschnitt(String wert){

        JSONObject response = serverRequest.doAsyncRequest("GET", "http://eis1617.lupus.uberspace.de/nodejs/wasserwerte", null);
        int anzahlWerte = 0;
        double werteSumme = 0;

        try {
            if (response.getString("success") != null && response.getString("success") != "false") {
                JSONArray wasserwerte = response.getJSONArray("wasserwerte");

                for(int i = 0; i < wasserwerte.length(); i++){

                    JSONObject werte = wasserwerte.getJSONObject(i);
                    if(werte.getString(wert) != null){
                        werteSumme += Double.parseDouble(werte.getString(wert));
                        anzahlWerte++;
                    }

                }

            }
            else{
                Log.w("WasserwerteFragment", "loadDurchschnitt:failed");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return anzahlWerte == 0 ? 0 : werteSumme/anzahlWerte;

    }

    private void makeGraphDialog(double[] values, String title, String einheit, double durchschnitt){

        DialogFragment dialog = GraphDialog.newInstance(values, title, einheit, durchschnitt);
        dialog.show(getFragmentManager(), "graph_dialog");

    }
}
