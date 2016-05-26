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
import com.hammersmith.onlineapp.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Thuon on 5/20/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{
    private Activity activity;
    private List<Product> products;
    Product product;
    Context context;
    private ClickListener clickListener;
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapter.MyViewHolder holder, int position) {
        product = products.get(position);
        Uri uri = Uri.parse(String.valueOf(product.getImage()));
        context = holder.image.getContext();
        Picasso.with(context).load(uri).into(holder.image);
        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice());
        holder.discount.setText(product.getDiscount());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, price, discount;
        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);
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
    public ProductAdapter(Activity activity, List<Product> products) {
        this.activity = activity;
        this.products = products;
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
