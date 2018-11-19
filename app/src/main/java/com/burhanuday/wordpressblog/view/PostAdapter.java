package com.burhanuday.wordpressblog.view;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.model.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burhanuday on 18-11-2018.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private Context context;
    private List<Post> posts;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
            if (!featured_1.toString().isEmpty()){
                Glide.with(context).load(sourceUrl).into(holder.thumbnail);
            }else {
                holder.thumbnail.setVisibility(View.GONE);
            }

        }catch (JsonIOException je){
            je.printStackTrace();
            Toast.makeText(context, je.getMessage(), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public int getItemCount() {
        return posts.size();
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

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
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
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
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
        /*
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse("2018-04-10T04:00:00.000Z", inputFormatter);
        String formattedDate = outputFormatter.format(date);
        System.out.println(formattedDate); // prints 10-04-2018
        */

        return "";
    }
}
