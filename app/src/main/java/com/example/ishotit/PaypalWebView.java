package com.example.ishotit;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class PaypalWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wView = new WebView(this);
        wView.getSettings().setJavaScriptEnabled(true);
        setContentView(wView);
        wView.loadUrl("https://www.paypal.com");
    }
}