package com.burhanuday.wordpressblog.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by burhanuday on 22-11-2018.
 */
public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context){
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork!=null && activeNetwork.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }
}
