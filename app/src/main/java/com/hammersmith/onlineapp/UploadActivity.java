package com.hammersmith.onlineapp;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hammersmith.onlineapp.app.AppController;
import com.hammersmith.onlineapp.utils.Constant;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private Button uploadButton, btnselectpic;
    private EditText title, price, discount, description, type, discount_period;
    private RadioGroup radioGroup;
    private ImageView imageview;
    private String fileName;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private Context context;
    private String cat_id;

    private String upLoadServerUri = null;
    private String imagepath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = UploadActivity.this;
        uploadButton = (Button) findViewById(R.id.uploadButton);
        btnselectpic = (Button) findViewById(R.id.button_selectpic);
        imageview = (ImageView) findViewById(R.id.imageView_pic);
        title = (EditText) findViewById(R.id.input_title);
        price = (EditText) findViewById(R.id.input_price);
        discount = (EditText) findViewById(R.id.input_discount);
        description = (EditText) findViewById(R.id.input_description);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        type = (EditText) findViewById(R.id.input_type);
        discount_period = (EditText) findViewById(R.id.input_discount_period);
        discount_period.setOnFocusChangeListener(this);
        radioGroup.setOnClickListener(this);
        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        upLoadServerUri = Constant.URL_UPLOAD_IMAGE;
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == btnselectpic) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
        } else if (arg0 == uploadButton) {
            int id = radioGroup.getCheckedRadioButtonId();
            if (imageview.getTag().equals("imageSample")) {
                Toast.makeText(getApplicationContext(), "Please choose image", Toast.LENGTH_SHORT).show();
            } else if (id == -1) {
                Toast.makeText(getApplicationContext(), "Please choose category", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(title.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input title", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(price.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input price", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(discount.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input discount", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(type.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input type", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(discount_period.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input discount period", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(description.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please input description", Toast.LENGTH_SHORT).show();
            } else {
                dialog = ProgressDialog.show(getApplicationContext(), "", "Uploading file...", true);
                Toast.makeText(getApplicationContext(), "uploading started.....", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    public void run() {
                        uploadFile(imagepath);
                    }
                }).start();
                uploadProduct();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            //Bitmap photo = (Bitmap) data.getData().getPath();
//            Uri selectedImageUri = data.getData();
//            imagepath = getPath(selectedImageUri);
//            Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
//            imageview.setImageBitmap(bitmap);
//            Toast.makeText(this, "File Name & PATH are:" + imagepath + "\n" + imagepath, Toast.LENGTH_LONG).show();
//        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri.getScheme().toString().compareTo("content") == 0) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                    Uri filePathUri = Uri.parse(cursor.getString(column_index));
                    String file_name = filePathUri.getLastPathSegment().toString();
                    Uri selectedImageUri = data.getData();
                    imagepath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
                    imageview.setImageBitmap(bitmap);
                    imageview.setTag(file_name);
//                    Toast.makeText(this, "File Name & PATH are:" + file_name + "\n" + imagepath, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {
        fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + imagepath);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Source File not exist :" + imagepath, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + " C:/xampp/htdocs/online_shopping/images";
                            Toast.makeText(UploadActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                dialog.dismiss();
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UploadActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UploadActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server ", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }

    public void uploadProduct() {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == R.id.r_man) {
            cat_id = "1";
        } else if (id == R.id.r_woman) {
            cat_id = "2";
        } else if (id == R.id.r_kid) {
            cat_id = "3";
        } else if (id == R.id.r_other) {
            cat_id = "4";
        }
        StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_UPLOAD_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getApplicationContext(), "Data uploaded", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UploadActivity.this, AdvertiseActivity.class));
                finish();
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
                params.put("cat_id", cat_id);
                params.put("owner_id", "1");
                params.put("title", title.getText().toString());
                params.put("image", "images/" + imageview.getTag().toString());
                params.put("price", price.getText().toString());
                params.put("discount", discount.getText().toString());
                params.put("type", type.getText().toString());
                params.put("discount_period", discount_period.getText().toString());
                params.put("description", description.getText().toString());
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(userReq);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.input_discount_period:
                if (hasFocus) {
                    DateDialog dateDialog = new DateDialog(v);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dateDialog.show(ft, "DatePicker");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                }
                break;
        }
    }
}
