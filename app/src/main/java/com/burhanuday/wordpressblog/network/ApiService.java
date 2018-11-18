package com.burhanuday.wordpressblog.network;

import com.burhanuday.wordpressblog.network.model.Post;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by burhanuday on 18-11-2018.
 */
public interface ApiService {

    @GET("posts")
    Single<List<Post>> fetchAllArticles();
}
