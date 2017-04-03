package com.cybercareinfoways.aisha.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AishaConstants;

public class ShowInMapActivity extends AppCompatActivity {
    private ShowForMapReciver showForMapReciver;
    //public static SharedLocation sharedLocation;
    //public static com.cybercareinfoways.aisha.model.LoationRequest loationCustomeRequest;
    //public static Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_in_map);
        showForMapReciver = new ShowForMapReciver();
        //TODO map work
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AishaConstants.EXTRA_SHOW_IN_MAP_ACTION);
        registerReceiver(showForMapReciver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(showForMapReciver);
    }
    public void upDateMap(Location location){

    }


    class ShowForMapReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AishaConstants.EXTRA_SHOW_IN_MAP_ACTION)){
               // sharedLocation = intent.getParcelableExtra(AishaConstants.EXTRA_FRIEND_LOCATION_SHARED_CONTENT_ON_MAP);
                //loationCustomeRequest = intent.getParcelableExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST);
                 Location location  = intent.getParcelableExtra(AishaConstants.EXTRA_USER_REQUEST_LOCATION);
                 upDateMap(location);
            }
        }
    }
}
