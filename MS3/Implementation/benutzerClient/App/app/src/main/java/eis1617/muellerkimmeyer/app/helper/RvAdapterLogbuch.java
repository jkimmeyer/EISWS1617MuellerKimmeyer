package eis1617.muellerkimmeyer.app.helper;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import eis1617.muellerkimmeyer.app.R;
import eis1617.muellerkimmeyer.app.klassen.LogbuchEintrag;

/**
 * Created by morit on 12.01.2017.
 */

public class RvAdapterLogbuch extends RecyclerView.Adapter<RvAdapterLogbuch.MyViewHolder> {

    private ArrayList<LogbuchEintrag> eintraege;
    private AlertDialog.Builder dialogBuilder;

    public RvAdapterLogbuch(ArrayList<LogbuchEintrag> eintraege){
        this.eintraege = eintraege;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.logbuch_eintrag_item, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final MyViewHolder mHolder = holder;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        holder.tvAktion.setText(eintraege.get(position).aktion);
        holder.tvDatum.setText(dateFormat.format(eintraege.get(position).datum)+"\n"+timeFormat.format(eintraege.get(position).datum)+" Uhr");
        holder.ivIcon.setImageResource(eintraege.get(position).icon);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(mHolder.itemView.getContext());
                dialogBuilder.setMessage(eintraege.get(position).message).setTitle(eintraege.get(position).aktion).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

    }

    public void insertItem(LogbuchEintrag eintrag){
        eintraege.add(0, eintrag);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, eintraege.size());
    }

    public void removeItem(int position) {
        eintraege.remove(position);
        notifyDataSetChanged();
        /*
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eintraege.size());
        */
    }

    @Override
    public int getItemCount() {
        return eintraege.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAktion, tvDatum;
        ImageView ivIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvAktion = (TextView) itemView.findViewById(R.id.tvAktion);
            tvDatum = (TextView) itemView.findViewById(R.id.tvDatum);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        }
    }
}
