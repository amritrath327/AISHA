package com.cybercareinfoways.fcm;

import android.app.IntentService;
import android.content.Intent;

import com.cybercareinfoways.helpers.AishaConstants;

/**
 * Created by Nutan on 26-03-2017.
 */

public class PushMessageService extends IntentService{
    public PushMessageService() {
        super("PushMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PushData data=intent.getParcelableExtra(AishaConstants.EXTRA_PUSH_DATA);
        Intent intent1=new Intent();
        intent1.putExtra(AishaConstants.EXTRA_PUSH_DATA,data);
        intent1.setAction(AishaConstants.EXTRA_ACTION_REQUEST_SHARE_LOCAION);
        sendOrderedBroadcast(intent1,null);
    }
}
