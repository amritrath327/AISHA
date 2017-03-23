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
       Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_whatsapp)
                .setContentTitle("Aisha")
                .setContentText("Request from "+ requestedFrom +" for "+ duration+ " min.")
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.arpeggio))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1410, notificationBuilder.build());
        Intent intent1=new Intent();
        intent1.setAction(AishaConstants.EXTRA_ACTION_REQUEST_SHARE_LOCAION);
        com.cybercareinfoways.aisha.model.LoationRequest request=new com.cybercareinfoways.aisha.model.LoationRequest(requestedFrom,0);
        intent1.putExtra(AishaConstants.EXTRA_REQUEST_LOCATION,request);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
    }
}
