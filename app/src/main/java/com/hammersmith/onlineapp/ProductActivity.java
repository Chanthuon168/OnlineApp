package com.hammersmith.onlineapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hammersmith.onlineapp.adapter.ProductAdapter;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.model.Product;
import com.hammersmith.onlineapp.utils.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.ClickListener {
    private Toolbar toolbar;
    private GridLayoutManager lLayout;
    ProductAdapter productAdapter;
    RecyclerView recyclerView;
    Context context;
    private ProgressDialog pDialog;
    private CollapsingToolbarLayout mCollapsingToolBarLayout;
    private Product product;
    private List<Product> products = new ArrayList<>();
    private ImageView img_banner;
    private int[] pro_id;
    private String cat_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context = ProductActivity.this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        context = ProductActivity.this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        img_banner = (ImageView) findViewById(R.id.img_banner);
        lLayout = new GridLayoutManager(ProductActivity.this, 2);
        productAdapter = new ProductAdapter(this, products);
        productAdapter.setClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(productAdapter);
        mCollapsingToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolBarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        cat_id = getIntent().getExtras().getString("cat_id");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showDialog();
        JsonObjectRequest objRequest = new JsonObjectRequest(Constant.URL_CATEGORY + cat_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    toolbar.setTitle(jsonObject.getString("name"));
                    String str_image = Constant.URL_HOME + jsonObject.getString("image");
                    Uri image = Uri.parse(str_image);
                    Picasso.with(context).load(image).into(img_banner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("gms", "" + volleyError);
            }
        });
        AppController.getInstance().addToRequestQueue(objRequest);

        if (products.size() <= 0) {
            // Creating volley request obj
            JsonArrayRequest fieldReq = new JsonArrayRequest(Constant.URL_PRODUCT + cat_id, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    hidePDialog();
                    pro_id = new int[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            product = new Product();
                            String str_price = obj.getString("price");
                            String str_discount = obj.getString("discount");
                            Double str_price_discount = Double.parseDouble(str_price) * Double.parseDouble(str_discount) / 100;
                            Double str_pro_price = Double.parseDouble(str_price) - str_price_discount;
                            product.setImage(Constant.URL_HOME + obj.getString("image"));
                            product.setTitle(obj.getString("name"));
                            product.setPrice("$ " + str_pro_price);
                            product.setDiscount("( " + str_discount + "% OFF )");
                            pro_id[i] = obj.getInt("id");
                            products.add(product);
                            hidePDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("error", "" + volleyError);
                    hidePDialog();
                }
            });
            AppController.getInstance().addToRequestQueue(fieldReq);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void showDialog() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(ProductActivity.this,ActivityComment.class);
        Bundle bundle = new Bundle();
        bundle.putString("pro_id", String.valueOf(pro_id[position]));
        bundle.putString("cat_id",cat_id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
