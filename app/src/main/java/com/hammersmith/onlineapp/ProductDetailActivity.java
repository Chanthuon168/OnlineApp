package com.hammersmith.onlineapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.utils.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {
    Context context;
    ImageView img_pro;
    TextView title,price,pre_price,discount,description,type,poster,email,website,phone,facebook,txttype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        context = ProductDetailActivity.this;
        final String pro_id = getIntent().getExtras().getString("pro_id");
        img_pro = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        discount = (TextView) findViewById(R.id.discount);
        description = (TextView) findViewById(R.id.description);
        type = (TextView) findViewById(R.id.type);
        pre_price = (TextView) findViewById(R.id.pre_price);
        poster = (TextView) findViewById(R.id.poster);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        website = (TextView) findViewById(R.id.website);
        facebook = (TextView) findViewById(R.id.facebook);
        txttype = (TextView) findViewById(R.id.txttype);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final String cat_id = getIntent().getExtras().getString("cat_id");
        if (cat_id.equals("4")){
            txttype.setText("Type: ");
        }

        final JsonObjectRequest objRequest = new JsonObjectRequest(Constant.URL_PRODUCT_DETAIL + pro_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String str_image = Constant.URL_HOME + jsonObject.getString("image");
                    String str_price = jsonObject.getString("price");
                    String str_discount = jsonObject.getString("discount");
                    Double str_price_discount = Double.parseDouble(str_price) * Double.parseDouble(str_discount) / 100;
                    Double str_pro_price = Double.parseDouble(str_price) - str_price_discount;
                    Uri image = Uri.parse(str_image);
                    Picasso.with(context).load(image).into(img_pro);
                    title.setText(jsonObject.getString("name"));
                    price.setText("$ "+str_pro_price);
                    pre_price.setText(str_price);
                    discount.setText("( "+str_discount+"% OFF )");
                    type.setText(jsonObject.getString("type"));
                    description.setText(jsonObject.getString("description"));
                    poster.setText(jsonObject.getString("owner_name"));
                    phone.setText(jsonObject.getString("phone"));
                    email.setText(jsonObject.getString("email"));
                    website.setText(jsonObject.getString("website"));
                    facebook.setText(jsonObject.getString("facebook"));
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
    }

}
