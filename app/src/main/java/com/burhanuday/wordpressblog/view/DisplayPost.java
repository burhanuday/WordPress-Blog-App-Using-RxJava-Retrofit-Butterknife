package com.burhanuday.wordpressblog.view;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.asksira.webviewsuite.WebViewSuite;
import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.ApiClient;
import com.burhanuday.wordpressblog.network.ApiService;
import com.burhanuday.wordpressblog.network.model.Post;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DisplayPost extends AppCompatActivity {

    @BindView(R.id.webViewSuite)
    WebViewSuite webViewSuite;

    private String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_display_post);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra("_id", 0);
        String link = getIntent().getStringExtra("_link");

        webViewSuite.interfereWebViewSetup(new WebViewSuite.WebViewSetupInterference() {
            @Override
            public void interfereWebViewSetup(WebView webView) {
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
            }
        });
        content = getIntent().getStringExtra("post_content");
        webViewSuite.setOpenPDFCallback(new WebViewSuite.WebViewOpenPDFCallback() {
            @Override
            public void onOpenPDF() {
                finish();
            }
        });
        content = content.replaceAll("\\\\n", "").
                replaceAll("\\\\r", "").replaceAll("\\\\", "");

        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"prism.css\" />" +
                "<script src=\"prism.js\"></script>" +
                "<div class=\"content\">" + content+ "</div>";
        //webViewSuite.startLoadData("file:///android_asset/*",content, "text/html; charset=utf-8", "UTF-8");

    }

    @Override
    public void onBackPressed() {
        if (!webViewSuite.goBackIfPossible()) super.onBackPressed();
    }
}
