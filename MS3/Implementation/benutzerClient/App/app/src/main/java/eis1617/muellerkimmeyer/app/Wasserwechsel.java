package eis1617.muellerkimmeyer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Moritz on 06.01.2017.
 */

public class Wasserwechsel extends Fragment {

    public TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wasserwechsel, container, false);
        textView = (TextView) rootView.findViewById(R.id.textView);

        return rootView;
    }

}
