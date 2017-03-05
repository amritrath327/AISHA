package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AppConstants;

public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogedin()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, AddMobileActivity.class);
                    startActivity(intent);
                }
                finish();

            }
        }, 5000);
    }

    private boolean isLogedin() {
        String userId = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE)
                .getString(AppConstants.USERID, "na");
        return !userId.equals("na");
    }

}