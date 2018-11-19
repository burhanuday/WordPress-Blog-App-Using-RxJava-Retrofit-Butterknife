package com.burhanuday.wordpressblog.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

    private ApiService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    final static String mimeType = "text/html";
    final static String encoding = "UTF-8";

    @BindView(R.id.wv_body)
    WebView body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);
        ButterKnife.bind(this);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        int id = getIntent().getIntExtra("_id", 0);
        String link = getIntent().getStringExtra("_link");

        if (link!=null && !link.isEmpty()){
            body.setWebViewClient(new WebViewClient());
            WebSettings settings = body.getSettings();
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
            // Flash settings
            settings.setPluginState(WebSettings.PluginState.ON);
            body.loadUrl(link);

            // Geo location settings
            //settings.setGeolocationEnabled(true);
            //settings.setGeolocationDatabasePath("/data/data/selendroid");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (body.canGoBack()){
            body.goBack();
        }
    }
}
