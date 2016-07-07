package com.hammersmith.onlineapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.hammersmith.onlineapp.adapter.CommentAdapter;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.model.Comment;
import com.hammersmith.onlineapp.model.Product;
import com.hammersmith.onlineapp.model.User;
import com.hammersmith.onlineapp.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityComment extends AppCompatActivity implements View.OnClickListener {
    EditText edit_comment;
    Button btn_send;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private ArrayList<Comment> comments = new ArrayList<>();
    private Comment comment;
    private LinearLayout l_show_detail;
    private ImageView img_rate, img_comment;
    private User user;
    private String pro_id, cat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        user = PrefUtils.getCurrentUser(getApplicationContext());
        edit_comment = (EditText) findViewById(R.id.comment);
        btn_send = (Button) findViewById(R.id.btn_send);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        l_show_detail = (LinearLayout) findViewById(R.id.l_show_detail);
        img_rate = (ImageView) findViewById(R.id.img_rate);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        pro_id = getIntent().getExtras().getString("pro_id");
        cat_id = getIntent().getExtras().getString("cat_id");
        getComment();
        initList();
        edit_comment.requestFocus();
        btn_send.setOnClickListener(this);
        l_show_detail.setOnClickListener(this);
        img_rate.setOnClickListener(this);
        img_comment.setOnClickListener(this);

    }

    public void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CommentAdapter(this, comments);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                postComment();
                edit_comment.setText("");
                break;
            case R.id.l_show_detail:
                startActivity(new Intent(ActivityComment.this, ProductDetailActivity.class));
                break;
            case R.id.img_rate:
                img_rate.setImageResource(R.drawable.rate_star_selected);
                break;
            case R.id.img_comment:
                edit_comment.requestFocus();
                break;
        }
    }

    public void postComment() {
        final StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_POST_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                adapter.notifyDataSetChanged();
                recyclerView.setVerticalScrollbarPosition(0);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getFacebookID());
                params.put("pro_id", pro_id);
                params.put("comment", edit_comment.getText().toString());
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(userReq);
    }

    public void getComment() {

        if (comments.size() <= 0) {
            // Creating volley request obj
            JsonArrayRequest fieldReq = new JsonArrayRequest(Constant.URL_GET_COMMENT, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            comment = new Comment();
                            comment.setComment(obj.getString("comment"));
                            comments.add(comment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("error", "" + volleyError);
                }
            });
            AppController.getInstance().addToRequestQueue(fieldReq);
        }
    }
}
