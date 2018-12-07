package com.burhanuday.wordpressblog.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.ApiClient;
import com.burhanuday.wordpressblog.network.ApiService;
import com.burhanuday.wordpressblog.network.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DisplayPostActivity extends AppCompatActivity {

    private int postId;
    private String content;
    private String title;

    @BindView(R.id.webview_display_post_activity)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_display_post2);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        postId = intent.getIntExtra("post_id", 0);
        content = intent.getStringExtra("post_content");
        title = intent.getStringExtra("post_title");
        setUpWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView(){
        WebSettings settings = webView.getSettings();
        //Change your WebView settings here
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(10 * 1024 * 1024);
        settings.setAppCachePath("");
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadsImagesAutomatically(true);
        // Flash settings
        settings.setPluginState(WebSettings.PluginState.ON);
        content = content.replaceAll("\\\\n", "").
                replaceAll("\\\\r", "").replaceAll("\\\\", "");

        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/style.css\" />" +
                "<script src=\"file:///android_asset/prism.js\" type=\"text/javascript\"></script>" +
                "<div class=\"content\">" + "<h1>" + title + "</h1>" + content+ "</div>";
        Log.i("htmlContent", content);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.loadDataWithBaseURL("file:///android_asset/*",content,
                "text/html", "UTF-8", null);
    }

}
