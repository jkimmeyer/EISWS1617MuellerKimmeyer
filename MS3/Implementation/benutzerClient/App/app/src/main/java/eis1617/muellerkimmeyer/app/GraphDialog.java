package eis1617.muellerkimmeyer.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by morit on 14.01.2017.
 */

public class GraphDialog extends DialogFragment {

    private LayoutInflater inflater;
    private View view;
    private LineGraphSeries<DataPoint> series;
    private GraphView graph;

    public static GraphDialog newInstance(double[] values, String title, String einheit) {
        GraphDialog frag = new GraphDialog();
        Bundle args = new Bundle();
        args.putDoubleArray("values", values);
        args.putString("title", title);
        args.putString("einheit", einheit);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        double[] values = getArguments().getDoubleArray("values");
        String title = getArguments().getString("title");
        String einheit = getArguments().getString("einheit");

        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.statistics_dialog, null);

        graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(getMaxValue(values)+1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(values.length);
        graph.getGridLabelRenderer().setNumVerticalLabels((int)getMaxValue(values)+2);
        graph.getGridLabelRenderer().setNumHorizontalLabels(values.length+1);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Wert in " + einheit);
        graph.setTitle(title);

        series = new LineGraphSeries<>();

        for(int x = 0; x < values.length; x++){
            double y = values[values.length-1-x];
            series.appendData(new DataPoint(x, y), true, values.length);
        }
        graph.addSeries(series);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view).setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    private double getMaxValue(double[] values){
        double max = 0;
        for (int i = 0; i < values.length; i++){
            if (values[i] > max){
                max = values[i];
            }
        }
        return max;
    }
}
