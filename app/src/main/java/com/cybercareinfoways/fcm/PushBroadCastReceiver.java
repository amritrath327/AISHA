package com.cybercareinfoways.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.HomeActivity;
import com.cybercareinfoways.helpers.AishaConstants;

/**
 * Created by Nutan on 26-03-2017.
 */

public class PushBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()== AishaConstants.EXTRA_ACTION_REQUEST_SHARE_LOCAION){
            PushData data=intent.getParcelableExtra(AishaConstants.EXTRA_PUSH_DATA);
           /* Intent intent1 = new  (context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1410,
                    intent1, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                    NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_whatsapp)
                    .setContentTitle("Aisha")
                    .setContentText("Request from "+ data.getRequestedFrom() +" for "+ data.getDuration()+ " min.")
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.arpeggio))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

           // notificationManager.notify(1410, notificationBuilder.build());*/

        }
    }
}
