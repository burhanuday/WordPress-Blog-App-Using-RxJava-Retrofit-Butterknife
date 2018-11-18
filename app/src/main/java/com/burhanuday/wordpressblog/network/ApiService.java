package com.burhanuday.wordpressblog.network;

import com.burhanuday.wordpressblog.network.model.Media;
import com.burhanuday.wordpressblog.network.model.Post;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by burhanuday on 18-11-2018.
 */
public interface ApiService {

    @GET("posts")
    Single<List<Post>> fetchAllPosts();

    @GET("posts/{id}")
    Call<Post> getPostById(@Path("id") int postId);

    @GET("media/{featured_media}")
    Call<Media> getPostThumbnail(@Path("featured_media") int media);
}
