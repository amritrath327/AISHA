package com.cybercareinfoways.aisha.services;

import android.app.IntentService;
import android.content.Intent;

import com.cybercareinfoways.aisha.activities.VerifyOTPActivity;
import com.cybercareinfoways.helpers.AppConstants;

/**
 * Created by YELOWFLASH on 03/02/2017.
 */

public class VerifyOTPService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    public VerifyOTPService() {
        super("VerifyOTPService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getExtras().getString(AppConstants.OTP);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(VerifyOTPActivity.OTPReceiver.ACTION);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(AppConstants.OTP, otp);
//            broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
            sendBroadcast(broadcastIntent);
            stopSelf();
        }
    }
}
