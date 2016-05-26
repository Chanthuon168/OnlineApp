package com.hammersmith.onlineapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hammersmith.onlineapp.adapter.AdvertiseAdapter;
import com.hammersmith.onlineapp.adapter.ProductAdapter;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.model.Advertise;
import com.hammersmith.onlineapp.model.Product;
import com.hammersmith.onlineapp.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvertiseActivity extends AppCompatActivity implements AdvertiseAdapter.ClickListener, View.OnClickListener {
    ImageView imgman, imggirl, kid, other, imgupload;
    private LinearLayoutManager lLayout;
    AdvertiseAdapter advertiseAdapter;
    RecyclerView recyclerView;
    private Advertise advertise;
    Context context;
    private ProgressDialog pDialog;
    private String[] link;
    private List<Advertise> advertises = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        context = AdvertiseActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        imgman = (ImageView) findViewById(R.id.man);
        imggirl = (ImageView) findViewById(R.id.women);
        imgupload = (ImageView) findViewById(R.id.upload);
        kid = (ImageView) findViewById(R.id.kid);
        other = (ImageView) findViewById(R.id.other);
        imgman.setOnClickListener(this);
        imggirl.setOnClickListener(this);
        kid.setOnClickListener(this);
        other.setOnClickListener(this);
        imgupload.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        lLayout = new LinearLayoutManager(AdvertiseActivity.this);
        advertiseAdapter = new AdvertiseAdapter(this, advertises);
        advertiseAdapter.setClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(advertiseAdapter);
        showDialog();

        if (advertises.size() <= 0) {
            // Creating volley request obj
            JsonArrayRequest fieldReq = new JsonArrayRequest(Constant.URL_ADVERTISE, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    hidePDialog();
                    link = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            advertise = new Advertise();
                            advertise.setImage(Constant.URL_HOME + obj.getString("image"));
                            advertise.setLogo(Constant.URL_HOME + obj.getString("logo"));
                            link[i] = obj.getString("link");
                            advertises.add(advertise);
                            hidePDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    advertiseAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
//                    showDialog();
                    Log.d("error", "" + volleyError);
                    hidePDialog();
                }
            });
            AppController.getInstance().addToRequestQueue(fieldReq);
        }
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(AdvertiseActivity.this,WebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("link",link[position]);
        intent.putExtras(bundle);
        startActivity(intent);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.man:
                Intent intent = new Intent(AdvertiseActivity.this,ProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cat_id","1");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.women:
                Intent intent1 = new Intent(AdvertiseActivity.this,ProductActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("cat_id","2");
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.kid:
                Intent intent3 = new Intent(AdvertiseActivity.this,ProductActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putString("cat_id","3");
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;
            case R.id.other:
                Intent intent4 = new Intent(AdvertiseActivity.this,ProductActivity.class);
                Bundle bundle4 = new Bundle();
                bundle4.putString("cat_id","4");
                intent4.putExtras(bundle4);
                startActivity(intent4);
                break;
            case R.id.upload:
                Intent intent5 = new Intent(AdvertiseActivity.this,UploadActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putString("cat_id","5");
                intent5.putExtras(bundle5);
                startActivity(intent5);
                break;
        }
    }
    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(), "Trash", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("chanthuon@gmail.com");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}
