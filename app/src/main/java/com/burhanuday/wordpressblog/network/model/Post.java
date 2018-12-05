package com.burhanuday.wordpressblog.network.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by burhanuday on 18-11-2018.
 */
public class Post extends BaseResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("slug")
    private String slug;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private Title title;

    @SerializedName("content")
    private Content content;

    @SerializedName("excerpt")
    private Excerpt excerpt;

    @SerializedName("_embedded")
    private JsonObject embedded;

    public class Title{
        @SerializedName("rendered")
        String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public class Content{
        @SerializedName("rendered")
        String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public class Excerpt{
        @SerializedName("rendered")
        String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }

    public JsonObject getEmbedded() {
        return embedded;
    }

    public void setEmbedded(JsonObject embedded) {
        this.embedded = embedded;
    }
}
