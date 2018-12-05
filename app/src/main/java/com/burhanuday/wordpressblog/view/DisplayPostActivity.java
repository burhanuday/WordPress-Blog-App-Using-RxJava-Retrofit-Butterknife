package com.burhanuday.wordpressblog.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

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

    private Post post;
    private int postId;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiService apiService;

    @BindView(R.id.webview_display_post_activity)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_display_post2);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent!=null){
            postId = intent.getIntExtra("post_id", 0);
        }
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        fetchPost();
    }

    private void setUpWebView(){
        String content = post.getContent().getRendered().replaceAll("\\\\n", "").
                replaceAll("\\\\r", "").replaceAll("\\\\", "");

        content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<script src=\"prism.js\"></script>" +
                "<div class=\"content\">" + content+ "</div>";
        webView.loadDataWithBaseURL("file:///android_asset/*",content,
                "text/html; charset=utf-8", "UTF-8", null);
    }

    private void fetchPost(){
        compositeDisposable.add(
                apiService.getPostById(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Post>(){
                    @Override
                    public void onSuccess(Post post1) {
                        post = post1;
                        setUpWebView();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })
        );
    }

}
