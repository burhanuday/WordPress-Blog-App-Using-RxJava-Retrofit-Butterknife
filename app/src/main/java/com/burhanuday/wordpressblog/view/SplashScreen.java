package com.burhanuday.wordpressblog.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.view.Home;

public class SplashScreen extends AppCompatActivity {

    /**
     * Stay visible on the screen for DISPLAY_TIME seconds then finish
     * @param savedInstanceState
     */

    private static final Long DISPLAY_TIME = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, DISPLAY_TIME);

    }
}
