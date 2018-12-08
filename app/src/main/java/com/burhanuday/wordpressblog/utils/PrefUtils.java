package com.burhanuday.wordpressblog.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by burhanuday on 18-11-2018.
 */
public class PrefUtils {

    private static final String LATEST_POST_TITLE = "latest_post_title";

    public static int getLatestId(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(LATEST_POST_TITLE, 0);
    }

    public static void saveLatestId(Context context, int id){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(LATEST_POST_TITLE, id).apply();
    }
}