package com.burhanuday.wordpressblog.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.internal.NavigationMenuItemView;
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
import android.widget.ActionMenuView;
import android.widget.TextView;
import android.widget.Toast;

import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.ApiClient;
import com.burhanuday.wordpressblog.network.ApiService;
import com.burhanuday.wordpressblog.network.model.Category;
import com.burhanuday.wordpressblog.network.model.Post;
import com.burhanuday.wordpressblog.utils.MyDividerItemDecoration;
import com.burhanuday.wordpressblog.utils.PostAdapter;
import com.burhanuday.wordpressblog.utils.RecyclerTouchListener;
import com.burhanuday.wordpressblog.utils.RecyclerViewScrollListener;

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
    private boolean categoryMode = false;
    private int categoryId;
    private RecyclerViewScrollListener recyclerViewScrollListener;

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
        toolbar.setTitle("All posts");
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
        recyclerViewScrollListener = new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                if (!categoryMode){
                    fetchNextPage();
                }else {
                    fetchNextByCategory();
                }
            }
        };
        recyclerView.addOnScrollListener(recyclerViewScrollListener);
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
                        int i=0;
                        for (Category category: categories){
                            menu.add(category.getName());
                            menu.getItem(i).setActionView(R.layout.navigation_item_row);
                            setMenuCounter(i, category.getCount());
                            i++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                })
        );
    }

    private void setMenuCounter(int item, int count){
        TextView view = (TextView) navigationView.getMenu().getItem(item).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }


    /**
     * loads posts from page 1
     * clears list
     */

    private void fetchAllPosts(){
        if (isLoading){
            return;
        }
        mAdapter.showLoading(true);
        mAdapter.notifyDataSetChanged();
        currentPage = 1;
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
                        isLoading = false;
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                        toggleEmptyPosts();
                        currentPage++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        showError(e);
                        isLoading = false;
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
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
        mAdapter.showLoading(true);
        mAdapter.notifyDataSetChanged();
        isLoading = true;
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
                                isLoading = false;
                                mAdapter.showLoading(false);
                                mAdapter.notifyDataSetChanged();
                                toggleEmptyPosts();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                isLoading = false;
                                mAdapter.showLoading(false);
                                mAdapter.notifyDataSetChanged();
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
        recyclerViewScrollListener.onDataCleared();
        int id = menuItem.getItemId();
        String title = (String) menuItem.getTitle();
        getSupportActionBar().setTitle(title);
        if (title.equals("All posts")){
            fetchAllPosts();
            categoryMode = false;
            if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        Category category = null;
        for (Category category1 : categoryList){
            if (category1.getName().equals(title)){
                category = category1;
            }
        }
        categoryMode = true;
        assert category != null;
        fetchByCategory(category.getId());
        Log.i("fetching", "slug is: " + category.getSlug());
        this.categoryId = category.getId();
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * fetch list of posts when the category name is provided
     * @param slug = unique name of category
     */

    private void fetchByCategory(int slug){
        if (isLoading){
            return;
        }
        isLoading = true;
        mAdapter.showLoading(true);
        mAdapter.notifyDataSetChanged();
        currentPage = 1;
        Log.i("fetching", String.valueOf(currentPage) + " by category");
        compositeDisposable.add(
                apiService.fetchPostsByCategory(currentPage, slug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Post>>(){
                    @Override
                    public void onSuccess(List<Post> posts) {
                        for (Post post : posts) {
                            Log.i("fetching", post.getTitle().getRendered() + " by category");
                        }
                            postsList.clear();
                            postsList.addAll(posts);
                        isLoading = false;
                        mAdapter.showLoading(false);
                            mAdapter.notifyDataSetChanged();
                            toggleEmptyPosts();
                            currentPage++;

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("fetching", "Error: " + e.getMessage());
                        isLoading = false;
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                    }
                })
        );
    }

    /**
     * paginate posts when loading posts by category
     */
    private void fetchNextByCategory(){
        if (isLoading){
            return;
        }
        isLoading = true;
        mAdapter.showLoading(true);
        mAdapter.notifyDataSetChanged();
        Log.i("fetching", String.valueOf(currentPage) + " by category");
        compositeDisposable.add(
                apiService.fetchPostsByCategory(currentPage, categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Post>>(){
                    @Override
                    public void onSuccess(List<Post> posts) {
                        currentPage++;
                        postsList.addAll(posts);
                        isLoading = false;
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                        toggleEmptyPosts();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("fetching", "Error: " + e.getMessage());
                        isLoading = false;
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
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