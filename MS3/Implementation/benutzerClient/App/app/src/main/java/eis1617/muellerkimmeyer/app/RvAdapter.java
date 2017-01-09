package eis1617.muellerkimmeyer.app;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import java.util.ArrayList;

/**
 * Created by Moritz on 05.01.2017.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {

    private ArrayList<String> listItems;
    private MainActivity activity;

    public RvAdapter (ArrayList<String> listItems, MainActivity activity){

        this.listItems = listItems;
        this.activity = activity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_layout, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.itemTitle.setText(listItems.get(position));
        holder.itemImage.setImageResource(R.drawable.ic_keyboard_arrow_right);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Wasserwechsel wasserwechsel = new Wasserwechsel();
                activity.onItemSelected(wasserwechsel);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView itemTitle;
        ImageView itemImage;

        public MyViewHolder(View itemView){
            super(itemView);

            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
        }

    }
}
