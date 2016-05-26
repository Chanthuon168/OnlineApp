package com.hammersmith.onlineapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersmith.onlineapp.R;
import com.hammersmith.onlineapp.model.Advertise;
import com.hammersmith.onlineapp.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Thuon on 5/20/2016.
 */
public class AdvertiseAdapter extends RecyclerView.Adapter<AdvertiseAdapter.MyViewHolder>{
    private Activity activity;
    private List<Advertise> advertises;
    Advertise advertise;
    Context context;
    private ClickListener clickListener;
    @Override
    public AdvertiseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_advertise, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(AdvertiseAdapter.MyViewHolder holder, int position) {
        advertise = advertises.get(position);
        Uri image = Uri.parse(advertise.getImage());
        Uri logo = Uri.parse(advertise.getLogo());
        context = holder.image.getContext();
        Picasso.with(context).load(image).into(holder.image);
        Picasso.with(context).load(logo).into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return advertises.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image,logo;
        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, getLayoutPosition());
            }
        }
    }
    public AdvertiseAdapter(Activity activity, List<Advertise> advertises) {
        this.activity = activity;
        this.advertises = advertises;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public interface ClickListener {
        public void itemClicked(View view, int position);
    }
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
