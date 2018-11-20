package com.burhanuday.wordpressblog.network;

import com.burhanuday.wordpressblog.network.model.Category;
import com.burhanuday.wordpressblog.network.model.Media;
import com.burhanuday.wordpressblog.network.model.Post;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by burhanuday on 18-11-2018.
 */

public interface ApiService{
    @GET("posts?&_embed")
    Single<List<Post>> fetchAllPosts(@Query("page") int pageNo);

    @GET("posts?&_embed")
    Single<List<Post>> fetchPostsByCategory(@Query("page") int pageNo, @Query("category") String category);

    @GET("posts/{id}")
    Single<Post> getPostById(@Path("id") int postId);

    @GET("categories")
    Single<List<Category>> getAllCategories();
}
