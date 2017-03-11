package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AishaConstants;

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
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, AddMobileActivity.class);
                    startActivity(intent);
                }
                finish();

            }
        }, 3000);
    }

    private boolean isLogedin() {
        String userId = getSharedPreferences(AishaConstants.USERPREFS, MODE_PRIVATE)
                .getString(AishaConstants.USERID, "na");
        return !userId.equals("na");
    }

}