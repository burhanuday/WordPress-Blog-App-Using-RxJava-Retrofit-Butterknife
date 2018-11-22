package com.burhanuday.wordpressblog.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.model.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burhanuday on 18-11-2018.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Post> posts;
    protected boolean showLoader;
    private static final int VIEWTYPE_ITEM = 1;
    private static final int VIEWTYPE_LOADER = 2;
    protected LayoutInflater mInflater;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_LOADER){
            Log.i("adapter", "loader viewtype found");
            View view = mInflater.inflate(R.layout.loader_item_layout, parent, false);
            return new LoaderViewHolder(view);
        }else if (viewType == VIEWTYPE_ITEM){
            View itemView = mInflater.inflate(R.layout.post_list_row, parent, false);
            return new MyViewHolder(itemView);
        }
        throw new IllegalArgumentException("Invalid viewType " + viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof LoaderViewHolder){
            Log.i("adapter", "instance of loader");
            LoaderViewHolder loaderViewHolder = (LoaderViewHolder) viewHolder;
            if (showLoader){
                Log.i("adapter", "loader visibility set to true");
                loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            }else {
                Log.i("adapter", "loader visibility set to false");
                loaderViewHolder.mProgressBar.setVisibility(View.GONE);
            }
            return;
        }

        MyViewHolder holder = (MyViewHolder)viewHolder;
        Post post = posts.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.excerpt.setText(Html.fromHtml(post.getExcerpt().getRendered(), Html.FROM_HTML_MODE_COMPACT));
            holder.timestamp.setText(Html.fromHtml(formatDate(post.getDate()), Html.FROM_HTML_MODE_COMPACT));
            holder.title.setText(Html.fromHtml(post.getTitle().getRendered(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.excerpt.setText(Html.fromHtml(post.getExcerpt().getRendered()));
            holder.timestamp.setText(Html.fromHtml(formatDate(post.getDate())));
            holder.title.setText(Html.fromHtml(post.getTitle().getRendered()));
        }
        holder.thumbnail.setVisibility(View.VISIBLE);
        try {
            JsonObject embedded = post.getEmbedded();
            JsonArray featured = embedded.getAsJsonArray("wp:featuredmedia");
            JsonObject featured_1 = featured.get(0).getAsJsonObject();
            JsonObject mediaDetails = featured_1.getAsJsonObject("media_details");
            JsonObject sizes = mediaDetails.getAsJsonObject("sizes");
            JsonObject thumbnail = sizes.getAsJsonObject("thumbnail");
            String sourceUrl = thumbnail.get("source_url").getAsString();
            Log.i("sourceUrl", sourceUrl);
            if (!sourceUrl.isEmpty()){
                GlideApp.with(context)
                        .load(sourceUrl)
                        .fitCenter()
                        .placeholder(R.drawable.burhanuday_logo)
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.thumbnail);
            }else {
                holder.thumbnail.setVisibility(View.GONE);
            }

        }catch (JsonIOException je){
            je.printStackTrace();
            //Toast.makeText(context, je.getMessage(), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public int getItemViewType(int position) {
        // loader can't be at position 0
        // loader can only be at the last position
        if (position != 0 && position == getItemCount() - 1) {
            return VIEWTYPE_LOADER;
        }

        return VIEWTYPE_ITEM;
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    @Override
    public long getItemId(int position) {
        if (position!=0 && position == getItemCount()-1){
            return -1;
        }
        return posts.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if (posts == null || posts.size() == 0){
            return 0;
        }
        return posts.size()+1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_excerpt)
        TextView excerpt;

        @BindView(R.id.tv_timestamp)
        TextView timestamp;

        @BindView(R.id.tv_title)
        TextView title;

        @BindView(R.id.iv_post_image)
        ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressbar)
        ProgressBar mProgressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Chooses random color defined in res/array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    /**
     * Formatting timestamp to `MMM d` format
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}
