package com.hammersmith.onlineapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog mProgress;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        context = WebviewActivity.this;
        webView = (WebView)findViewById(R.id.webview);

        final String link = getIntent().getExtras().getString("link");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        mProgress = ProgressDialog.show(context, "Loading",
                "Please wait for a moment...");
        webView.loadUrl(link);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }
        });
    }
}
