package com.cybercareinfoways.aisha.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.cybercareinfoways.helpers.AishaConstants;

/**
 * Created by Nutan on 24-03-2017.
 */

public class ShareLocaionService extends IntentService {
    boolean isTimerStarted;
    Handler handler;
    private long duration;
    public ShareLocaionService() {
        super("ShareLocaionService");
        handler=new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO GetLocation Updates
        duration=intent.getIntExtra(AishaConstants.EXTRA_DURATION,1000);
        if(!isTimerStarted){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //TODO STOP LOCATION UPDATE
                    Toast.makeText(ShareLocaionService.this,"Location Ended",Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            },duration);
        }

    }
}
