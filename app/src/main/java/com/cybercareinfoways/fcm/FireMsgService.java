package com.cybercareinfoways.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.HomeActivity;
import com.cybercareinfoways.helpers.AishaConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Nutan on 03/09/2017.
 */

public class FireMsgService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       // Log.d("Msg//", "Message received ["+remoteMessage.getData().get("message")+"]");
        Log.v("MSJH>>>",remoteMessage.toString());


        // Create Notification
        String requestedFrom=remoteMessage.getData().get("mobile_number");
        String duration= remoteMessage.getData().get("duration");
        PushData pushData=new PushData(requestedFrom,Long.parseLong(duration));
        Intent startService=new Intent(this,PushMessageService.class);
        startService.putExtra(AishaConstants.EXTRA_PUSH_DATA,pushData);
        startService(startService);

    }
}
