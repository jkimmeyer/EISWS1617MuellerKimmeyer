package eis1617.muellerkimmeyer.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.os.Handler;

/**
 * Created by morit on 12.01.2017.
 */

public class RvAdapterWasserwerte extends RecyclerView.Adapter<RvAdapterWasserwerte.MyViewHolder> {

    private ArrayList<WasserwerteEintrag> eintraege;
    private int focusedItem = 0;

    public RvAdapterWasserwerte(ArrayList<WasserwerteEintrag> eintraege){
        Collections.reverse(eintraege);
        this.eintraege = eintraege;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wasserwerte_eintrag_item, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        holder.tvDatum.setText(dateFormat.format(eintraege.get(position).datum)+"\n"+timeFormat.format(eintraege.get(position).datum)+" Uhr");
        holder.tvVon.setText(eintraege.get(position).von);

        if(eintraege.get(position).von == "Fachhandlung") {
            holder.ivIcon.setImageResource(R.drawable.ic_home);
        }
        else{
            holder.ivIcon.setImageResource(R.drawable.ic_account_circle);
        }

        holder.itemView.setSelected(focusedItem == position);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                WasserwerteFragment.setTextFields(eintraege.get(position));
                notifyItemChanged(focusedItem);
                focusedItem = position;
                notifyItemChanged(focusedItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eintraege.size();
    }

    public void insertItem(WasserwerteEintrag eintrag){
        eintraege.add(0, eintrag);
        int oldFocusedItem = this.focusedItem;
        focusedItem = 0;
        notifyItemInserted(0);
        notifyItemChanged(oldFocusedItem+1);
        notifyItemRangeChanged(0, eintraege.size());
    }

    public void removeItem(int position) {
        eintraege.remove(position);
        if(focusedItem > position){
            focusedItem--;
        }
        else if(focusedItem == position){
            WasserwerteFragment.setTextFields(eintraege.get(focusedItem));
        }
        if(eintraege.size() == 0){
            WasserwerteFragment.clearTextFields();
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eintraege.size());
    }

    public int getFocusedItem() {
        return focusedItem;
    }

    public void setFocusedItem(int focusedItem) {
        this.focusedItem = focusedItem;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDatum, tvVon;
        ImageView ivIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDatum = (TextView) itemView.findViewById(R.id.tvDatum);
            tvVon = (TextView) itemView.findViewById(R.id.tvVon);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        }
    }

}
