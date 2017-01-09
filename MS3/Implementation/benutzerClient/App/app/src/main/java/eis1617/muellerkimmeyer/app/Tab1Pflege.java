package eis1617.muellerkimmeyer.app;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Moritz on 04.01.2017.
 */

public class Tab1Pflege extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    private ArrayList<String> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_pflege, container, false);

        listItems = new ArrayList<>();
        listItems.add("Test1");
        listItems.add("Test2");
        listItems.add("Test3");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rvLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);
        rvAdapter = new RvAdapter(listItems, (MainActivity) getActivity());
        recyclerView.setAdapter(rvAdapter);

        return rootView;
    }

}
