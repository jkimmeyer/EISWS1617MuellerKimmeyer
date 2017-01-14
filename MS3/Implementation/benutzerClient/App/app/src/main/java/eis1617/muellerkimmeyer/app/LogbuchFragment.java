package eis1617.muellerkimmeyer.app;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import java.text.SimpleDateFormat;
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

    private boolean rvAdapterInitialized = false;

    Paint paint = new Paint();

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

        setUpItemTouchHelper();

        loadEintraege();

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
                databaseReference.child("logbuch").child(firebaseUser.getUid()).child(eintraege.get(position).datum.toString()).removeValue();
                rvAdapter.removeItem(position);
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
        logbuchReference = databaseReference.child("logbuch").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!rvAdapterInitialized) {

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        eintraege.add(ds.getValue(LogbuchEintrag.class));
                    }

                    Collections.reverse(eintraege);
                    rvAdapter = new RvAdapterLogbuch(eintraege);
                    recyclerView.setAdapter(rvAdapter);
                    rvAdapterInitialized = true;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LogbuchFragment", "loadLogbuch:onCancelled", databaseError.toException());
            }
        };
        logbuchReference.addValueEventListener(valueEventListener);

    }


}
