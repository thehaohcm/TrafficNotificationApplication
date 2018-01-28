package com.thehaohcm.trafficnotificationapplication.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.R;

public class NewsDetailActivity extends AppCompatActivity {
    private WebView wvData;
    private ProgressDialog dp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        wvData= (WebView) findViewById(R.id.wvData);
        String url=getIntent().getStringExtra("LINK");
        if(url!=null){
            if(url.contains("yahoo"))
                this.setTitle("Thông tin thời tiết");
            else if(url.contains("giaothong")){
                this.setTitle("Camera giao thông");
                wvData.getSettings().setJavaScriptEnabled(true);
            }
            dp=new ProgressDialog(this);
            dp.setMessage("Đang tải...");
            dp.show();
            wvData.setWebViewClient(onWebViewLoaded);
            wvData.loadUrl(url);
        }
    }

    private WebViewClient onWebViewLoaded=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dp.dismiss();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            dp.dismiss();
            Toast.makeText(NewsDetailActivity.this,"Đã có lỗi xảy ra",Toast.LENGTH_LONG).show();
        }
    };
}
