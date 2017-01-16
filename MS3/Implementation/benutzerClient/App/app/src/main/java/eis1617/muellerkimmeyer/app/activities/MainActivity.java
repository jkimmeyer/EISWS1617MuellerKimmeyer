package eis1617.muellerkimmeyer.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import eis1617.muellerkimmeyer.app.klassen.Aquarium;
import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.klassen.UserInformation;
import eis1617.muellerkimmeyer.app.fragmente.AquariumFragment;
import eis1617.muellerkimmeyer.app.fragmente.CO2BerechnungFragment;
import eis1617.muellerkimmeyer.app.fragmente.DuengerdosierungFragment;
import eis1617.muellerkimmeyer.app.fragmente.LogbuchFragment;
import eis1617.muellerkimmeyer.app.fragmente.WasserwechselFragment;
import eis1617.muellerkimmeyer.app.fragmente.WasserwerteFragment;
import eis1617.muellerkimmeyer.app.helper.ServerRequest;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView textViewEmail;
    private TextView textViewUserID;

    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private ServerRequest serverRequest;

    public static Aquarium aquarium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        serverRequest = new ServerRequest();

        loadAquarium();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView =  navigationView.getHeaderView(0);
        textViewEmail = (TextView)headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(firebaseUser.getEmail());
        textViewUserID = (TextView)headerView.findViewById(R.id.tvUserID);

        userReference = databaseReference.child("user").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                textViewUserID.setText("ID: " + userInformation.sid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadUID:onCancelled", databaseError.toException());
            }
        };
        userReference.addListenerForSingleValueEvent(valueEventListener);

        FirebaseMessaging.getInstance().subscribeToTopic("test");
    }

    private void loadAquarium(){
        JSONObject response = serverRequest.doAsyncRequest("GET", "http://eis1617.lupus.uberspace.de/nodejs/aquarien/" + firebaseAuth.getCurrentUser().getUid(), null);

        try{
            if(response.getJSONArray("aquarien").length() > 0){
                JSONObject aq = response.getJSONArray("aquarien").getJSONObject(0);
                String bezeichnung = aq.getString("bezeichnung");
                double laenge = aq.getDouble("laenge");
                double breite = aq.getDouble("breite");
                double hoehe = aq.getDouble("hoehe");
                double glasstaerke = aq.getDouble("glasstaerke");
                double kieshoehe = aq.getDouble("kieshoehe");
                double fd = aq.getDouble("fd");
                this.aquarium = new Aquarium(bezeichnung, laenge, breite, hoehe, glasstaerke, kieshoehe, fd);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.nav_wasserwechsel) {
            WasserwechselFragment wasserwechselFragment = new WasserwechselFragment();
            manager.beginTransaction().replace(R.id.content_main, wasserwechselFragment, wasserwechselFragment.getTag()).commit();
            getSupportActionBar().setTitle("Wasserwechsel");
        } else if (id == R.id.nav_aquarium) {
            AquariumFragment aquariumFragment = new AquariumFragment();
            manager.beginTransaction().replace(R.id.content_main, aquariumFragment, aquariumFragment.getTag()).commit();
            getSupportActionBar().setTitle("Virtuelles Aquarium");
        } else if (id == R.id.nav_logbuch) {
            LogbuchFragment logbuchFragment = new LogbuchFragment();
            manager.beginTransaction().replace(R.id.content_main, logbuchFragment, logbuchFragment.getTag()).commit();
            getSupportActionBar().setTitle("Logbuch");
        } else if (id == R.id.nav_duengerdosierung) {
            DuengerdosierungFragment duengerdosierungFragment = new DuengerdosierungFragment();
            manager.beginTransaction().replace(R.id.content_main, duengerdosierungFragment, duengerdosierungFragment.getTag()).commit();
            getSupportActionBar().setTitle("Düngerdosierung");
        } else if (id == R.id.nav_wasserwerte) {
            WasserwerteFragment wasserwerteFragment = new WasserwerteFragment();
            manager.beginTransaction().replace(R.id.content_main, wasserwerteFragment, wasserwerteFragment.getTag()).commit();
            getSupportActionBar().setTitle("Deine Wasserwerte");
        } else if (id == R.id.nav_co2_berechnung) {
            CO2BerechnungFragment co2BerechnungFragment = new CO2BerechnungFragment();
            manager.beginTransaction().replace(R.id.content_main, co2BerechnungFragment, co2BerechnungFragment.getTag()).commit();
            getSupportActionBar().setTitle("CO2 Berechnung");
        } else if (id == R.id.nav_fachhandel) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
