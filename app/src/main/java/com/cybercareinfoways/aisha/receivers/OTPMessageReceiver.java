package com.cybercareinfoways.aisha.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.cybercareinfoways.aisha.services.VerifyOTPService;
import com.cybercareinfoways.helpers.AppConstants;

/**
 * Created by YELOWFLASH on 03/02/2017.
 */

public class OTPMessageReceiver extends BroadcastReceiver {
    private static final String TAG = OTPMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");
            SmsMessage smsMessage = null;
            for (Object obj : pdusObj) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) obj, "3ggp");
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                }
            }
            if (smsMessage != null) {
                String senderAddress = smsMessage.getDisplayOriginatingAddress();
                String message = smsMessage.getDisplayMessageBody();
                if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                    Log.e(TAG, "SMS is not for our app!");
                    return;
                }

                // verification code from sms
                String verificationCode = getVerificationCode(message);

                Log.e(TAG, "OTP received: " + verificationCode);

                Intent hhtpIntent = new Intent(context, VerifyOTPService.class);
                hhtpIntent.putExtra(AppConstants.OTP, verificationCode);
                context.startService(hhtpIntent);
            }
        }
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(AppConstants.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 1;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
