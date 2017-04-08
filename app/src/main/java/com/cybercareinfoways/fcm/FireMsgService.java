package com.cybercareinfoways.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
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
    String duration;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       // Log.d("Msg//", "Message received ["+remoteMessage.getData().get("message")+"]");
        Log.v("MSJH>>>",remoteMessage.toString());


        // Create Notification
        String requestedFrom=remoteMessage.getData().get("mobile_number");

        if (!TextUtils.isEmpty(remoteMessage.getData().get("duration"))) {
            duration = remoteMessage.getData().get("duration");
        }
        String location_sharing_id=remoteMessage.getData().get("location_sharing_id");


        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_whatsapp)
                .setContentTitle("AISHA")
                .setContentText(getNameFromNumber(requestedFrom)+" wants to share your location for "+duration+" mins.")
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.arpeggio))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1410, notificationBuilder.build());


        PushData pushData=new PushData(requestedFrom,Long.parseLong(duration),location_sharing_id);
        Intent startService=new Intent(this,PushMessageService.class);
        startService.putExtra(AishaConstants.EXTRA_PUSH_DATA,pushData);
        startService(startService);

    }
    public String getNameFromNumber(String mobile) {
        String contactName = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobile));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
