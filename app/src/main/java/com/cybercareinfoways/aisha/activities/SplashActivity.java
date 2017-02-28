package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cybercareinfoways.aisha.R;

public class SplashActivity extends AppCompatActivity  {
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,AddMobileActivity.class);
                startActivity(intent);
            }
        },5000);
    }

}