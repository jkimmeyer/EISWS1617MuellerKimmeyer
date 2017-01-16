package eis1617.muellerkimmeyer.app.fragmente;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import eis1617.muellerkimmeyer.app.R;

/**
 * Created by morit on 14.01.2017.
 */

public class GraphDialog extends DialogFragment {

    private final int GRAPH_THICKNESS = 10;

    private LayoutInflater inflater;
    private View view;
    private LineGraphSeries<DataPoint> ownSeries;
    private LineGraphSeries<DataPoint> otherSeries;
    private GraphView graph;

    public static GraphDialog newInstance(double[] values, String title, String einheit, double durchschnitt) {
        GraphDialog frag = new GraphDialog();
        Bundle args = new Bundle();
        args.putDoubleArray("values", values);
        args.putString("title", title);
        args.putString("einheit", einheit);
        args.putDouble("durchschnitt", durchschnitt);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        double[] values = getArguments().getDoubleArray("values");
        String title = getArguments().getString("title");
        String einheit = getArguments().getString("einheit");
        double durchschnitt = getArguments().getDouble("durchschnitt");

        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.statistics_dialog, null);

        graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Math.ceil(Math.max(getMaxValue(values), durchschnitt))+1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(values.length);
        graph.getGridLabelRenderer().setNumVerticalLabels((int)Math.ceil(Math.max(getMaxValue(values), durchschnitt))+2);
        graph.getGridLabelRenderer().setNumHorizontalLabels(values.length+1);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Wert in " + einheit);
        graph.setTitle(title);

        ownSeries = new LineGraphSeries<>();
        otherSeries = new LineGraphSeries<>();

        for(int x = 0; x < values.length; x++){
            double y = values[values.length-1-x];
            ownSeries.appendData(new DataPoint(x, y), true, values.length);
            otherSeries.appendData(new DataPoint(x, durchschnitt), true, values.length);
        }

        otherSeries.setColor(Color.GREEN);
        ownSeries.setThickness(GRAPH_THICKNESS);
        otherSeries.setThickness(GRAPH_THICKNESS);
        graph.addSeries(otherSeries);
        graph.addSeries(ownSeries);

        ownSeries.setTitle("Deine Werte");
        otherSeries.setTitle("Durchschnittswert aller App-Nutzer");

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setMargin(40);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

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
