package com.burhanuday.wordpressblog.network.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by burhanuday on 20-11-2018.
 */
public class Category extends BaseResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("link")
    private String link;

    @SerializedName("name")
    private String name;

    @SerializedName("_links")
    private JsonObject _links;

    @SerializedName("count")
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonObject get_links() {
        return _links;
    }

    public void set_links(JsonObject _links) {
        this._links = _links;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }



}
