package com.hammersmith.onlineapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.model.User;
import com.hammersmith.onlineapp.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minea2015 on 11/18/2015.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String status = "0";
    String socialtype;
    Context context = MainActivity.this;
    private static final String TAG = "RegisterActivity";

    LoginButton loginButton;
    private CallbackManager callbackManager;
    User user;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    public static String googleName;
    public static String googleEmail;
    public static String googleID;
    Uri personPhotoUri = Uri.EMPTY;
    TextView title;
    Typeface typeface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.title);
        callbackManager = CallbackManager.Factory.create();

        if (PrefUtils.getCurrentUser(MainActivity.this) != null) {
            Intent home = new Intent(MainActivity.this, AdvertiseActivity.class);
            startActivity(home);
            finish();
        }
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile,email,user_birthday"));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.registerCallback(callbackManager, mCallback);
            }
        });

        // ******** Google Plus *********//
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_sign_out).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // ******** End Google Plus *********//

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.facebook.samples.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            showProgressDialog();
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            JSONObject obj = response.getJSONObject();
//                            setProfileToview(obj);
                            try {
                                if (obj != null) {
                                    user = new User();
                                    user.setFacebookID(obj.getString("id"));
                                    user.setName(obj.getString("name"));
                                    user.setEmail(obj.getString("email"));
                                    PrefUtils.setCurrentUser(user, MainActivity.this);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            saveUserSocial();

                            JsonObjectRequest objectRequest = new JsonObjectRequest(Constant.URL_CHECKUSERLOGIN + user.getFacebookID(), null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        String strstatus = (jsonObject.getString("status"));
//                                        Toast.makeText(getApplicationContext(),strstatus,Toast.LENGTH_SHORT).show();
                                        if (strstatus.equals(status)) {
                                            Intent main = new Intent(MainActivity.this, AdvertiseActivity.class);
                                            main.putExtra("status", strstatus);
                                            startActivity(main);
                                            finish();
                                        } else if (strstatus.equals("1")) {
                                            Intent intent = new Intent(MainActivity.this, AdvertiseActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (strstatus.equals("user_not_existed")) {
                                            Intent main = new Intent(MainActivity.this, AdvertiseActivity.class);
                                            main.putExtra("status", strstatus);
                                            startActivity(main);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    hideProgressDialog();
                                }
                            });
                            int socketTimeout = 60000;
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            objectRequest.setRetryPolicy(policy);
                            AppController.getInstance().addToRequestQueue(objectRequest);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    public void saveUserSocial() {
        StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Toast.makeText(getApplicationContext(), "Data uploaded...", Toast.LENGTH_SHORT).show();
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
                params.put("social_type", "fb");
                params.put("link", user.getFacebookID());
                params.put("username", user.getName());
                params.put("email", user.getEmail());
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(userReq);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btn_sign_out:
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        showProgressDialog();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            googleName = acct.getDisplayName();
            googleEmail = acct.getEmail();
            googleID = acct.getId();
            personPhotoUri = acct.getPhotoUrl();
            final User user = new User();
            user.setName(googleName);
            user.setFacebookID(googleID);
            user.setEmail(googleEmail);
            user.setImageProfile(String.valueOf(personPhotoUri));
            PrefUtils.setCurrentUser(user, MainActivity.this);

            saveUserGoogle();

            JsonObjectRequest objectRequest = new JsonObjectRequest(Constant.URL_CHECKUSERLOGIN + user.getFacebookID(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String strstatus = (jsonObject.getString("status"));
//                                        Toast.makeText(getApplicationContext(),strstatus,Toast.LENGTH_SHORT).show();
                        if (strstatus.equals(status)) {
                            Intent main = new Intent(MainActivity.this, AdvertiseActivity.class);
                            main.putExtra("status", strstatus);
                            startActivity(main);
                            finish();
                        } else if (strstatus.equals("1")) {
                            Intent intent = new Intent(MainActivity.this, AdvertiseActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (strstatus.equals("user_not_existed")) {
                            Intent main = new Intent(MainActivity.this, AdvertiseActivity.class);
                            main.putExtra("status", strstatus);
                            startActivity(main);
                            finish();
                        }
                        signOut();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    hideProgressDialog();
                }
            });
            int socketTimeout = 60000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(objectRequest);
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.btn_sign_out).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_sign_out).setVisibility(View.GONE);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait a moment....");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void saveUserGoogle() {
        StringRequest googleRequest = new StringRequest(Request.Method.POST, Constant.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(getApplicationContext(), "Data uploaded", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(),""+volleyError, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("link", googleID);
                params.put("username", googleName);
                params.put("email", googleEmail);
                params.put("social_type", "gg");
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        googleRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(googleRequest);
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Quite App")
                .setMessage("Are you sure want to quite this app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}