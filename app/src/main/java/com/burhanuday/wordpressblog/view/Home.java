package com.burhanuday.wordpressblog.view;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.ApiClient;
import com.burhanuday.wordpressblog.network.ApiService;
import com.burhanuday.wordpressblog.network.model.Category;
import com.burhanuday.wordpressblog.network.model.Post;
import com.burhanuday.wordpressblog.utils.MyDividerItemDecoration;
import com.burhanuday.wordpressblog.utils.PostAdapter;
import com.burhanuday.wordpressblog.utils.RecyclerTouchListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Home.class.getSimpleName();
    private ApiService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PostAdapter mAdapter;
    private List<Post> postsList = new ArrayList<>();
    private int currentPage=1;
    private boolean isLoading = false;
    private List<Category> categoryList = new ArrayList<>();
    private ActionBarDrawerToggle toggle;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.txt_empty_notes_view)
    TextView noNotesView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nv)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_title_home));
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        mAdapter = new PostAdapter(this, postsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // open full post
                Post post = postsList.get(position);
                int index = post.getId();
                Intent showFullScreen = new Intent(Home.this, DisplayPost.class);
                showFullScreen.putExtra("_id", index);
                showFullScreen.putExtra("_link", post.getLink());
                startActivity(showFullScreen);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    fetchNextPage();
                }
            }
        });
        fetchCategories();
        fetchAllPosts();

        Intent showSplashScreen = new Intent(Home.this, SplashScreen.class);
        startActivity(showSplashScreen);
    }

    /**
     * fetch names of categories to add them dynamically to navigation drawer
     */

    private void fetchCategories(){
        Log.i("fetching", "start fetching categories");
        compositeDisposable.add(
                apiService.getAllCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Category>>(){
                    @Override
                    public void onSuccess(List<Category> categories) {
                        categoryList.clear();
                        categoryList.addAll(categories);
                        Menu menu = navigationView.getMenu();
                        for (Category category: categories){
                            menu.add(category.getName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                })
        );
    }

    /**
     * loads posts from page 1
     * clears list
     */

    private void fetchAllPosts(){
        isLoading = true;
        Log.i("fetching", String.valueOf(currentPage));
        compositeDisposable.add(
                apiService.fetchAllPosts(currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Post>>(){
                    @Override
                    public void onSuccess(List<Post> posts) {
                        postsList.clear();
                        postsList.addAll(posts);
                        mAdapter.notifyDataSetChanged();
                        toggleEmptyPosts();
                        currentPage++;
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        showError(e);
                        isLoading = false;
                    }
                })
        );
    }

    /**
     * used for pagination
     * loads posts from the page currentPage
     */

    private void fetchNextPage(){
        if (isLoading){
            return;
        }
        Log.i("fetching", String.valueOf(currentPage));
        compositeDisposable.add(
                apiService.fetchAllPosts(currentPage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Post>>(){

                            @Override
                            public void onSuccess(List<Post> posts) {
                                currentPage++;
                                postsList.addAll(posts);
                                mAdapter.notifyDataSetChanged();
                                toggleEmptyPosts();
                                isLoading = false;
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                isLoading = false;
                            }
                        })
        );
    }

    /**
     * Check if list size is 0
     * show empty view if size is 0
     */

    private void toggleEmptyPosts() {
        if (postsList.size() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof IOException) {
                message = "No internet connection!";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Check LogCat.";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    /**
     * ItemListener to listen events on Navigation Drawer
     * @param menuItem
     * @return
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        String title = (String) menuItem.getTitle();

        Category category = null;
        for (Category category1 : categoryList){
            if (category1.getName().equals(title)){
                category = category1;
            }
        }

        assert category != null;
        fetchByCategory(category.getSlug());

        return true;
    }

    /**
     * fetch list of posts when the category name is provided
     * @param slug = unique name of category
     */

    private void fetchByCategory(String slug){
        if (slug == null){
            return;
        }
        Log.i("fetching", "by category");
        compositeDisposable.add(
                apiService.fetchPostsByCategory(1, slug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Post>>(){
                    @Override
                    public void onSuccess(List<Post> posts) {
                        for (Post post : posts){
                            Log.i("fetching", post.getTitle().getRendered() + "by category");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("fetching", "Error: " + e.getMessage());
                    }
                })
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Override onBackPressed to close drawer when the back button
     * is pressed
     */

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}