package com.burhanuday.wordpressblog.service;

import android.annotation.SuppressLint;

import com.burhanuday.wordpressblog.utils.NotificationUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Context;
import android.os.AsyncTask;

import com.burhanuday.wordpressblog.network.ApiClient;
import com.burhanuday.wordpressblog.network.ApiService;
import com.burhanuday.wordpressblog.network.model.Post;
import com.burhanuday.wordpressblog.utils.PrefUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by burhanuday on 08-12-2018.
 */
public class GetLatestPostService extends JobService {

    private static final int PAGE_ONE = 1;
    private static final int PER_PAGE = 1;
    private ApiService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Post post;
    private Context context;

    private AsyncTask mBackgroundTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters params) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                context = GetLatestPostService.this;
                apiService = ApiClient.getClient(context).create(ApiService.class);
                compositeDisposable.add(
                        apiService.getFirstPost(PAGE_ONE, PER_PAGE)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<List<Post>>(){
                                    @Override
                                    public void onSuccess(List<Post> posts) {
                                        post = posts.get(0);
                                        checkLatestOrNot();
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                })
                );
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        compositeDisposable.dispose();
        if (mBackgroundTask!=null) mBackgroundTask.cancel(true);
        return true;
    }

    private void checkLatestOrNot(){
        int latestPost = PrefUtils.getLatestId(context);
        if (!(post.getId() == latestPost)){
            //show notification
            NotificationUtils.showNotification(context, post);
        }
    }
}
