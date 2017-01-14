package eis1617.muellerkimmeyer.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogbuchFragment extends Fragment {

    private RecyclerView recyclerView;
    private RvAdapterLogbuch rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;
    private ArrayList<LogbuchEintrag> eintraege;

    private DatabaseReference databaseReference;
    private DatabaseReference logbuchReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public LogbuchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_logbuch, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rvLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);

        loadEintraege();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void loadEintraege(){
        eintraege = new ArrayList<>();
        logbuchReference = databaseReference.child("logbuch").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    eintraege.add(ds.getValue(LogbuchEintrag.class));
                }

                Collections.reverse(eintraege);

                rvAdapter = new RvAdapterLogbuch(eintraege);
                recyclerView.setAdapter(rvAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LogbuchFragment", "loadLogbuch:onCancelled", databaseError.toException());
            }
        };
        logbuchReference.addValueEventListener(valueEventListener);

    }


}
