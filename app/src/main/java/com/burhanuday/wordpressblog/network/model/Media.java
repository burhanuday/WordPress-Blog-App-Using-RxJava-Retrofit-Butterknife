package com.burhanuday.wordpressblog.network.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by burhanuday on 18-11-2018.
 */
public class Media {

    @SerializedName("guid")
    private JsonObject guid;

    public JsonObject getGuid() {
        return guid;
    }

    public void setGuid(JsonObject guid) {
        this.guid = guid;
    }
}
