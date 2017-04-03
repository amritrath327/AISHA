package com.cybercareinfoways.aisha.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.SharedLocation;
import com.cybercareinfoways.helpers.AishaConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackAndShareLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ShowForMapReciver showForMapReciver;
    public static SharedLocation sharedLocation;
    public static com.cybercareinfoways.aisha.model.LoationRequest loationCustomeRequest;
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_and_share_location);
        showForMapReciver = new ShowForMapReciver();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    class ShowForMapReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AishaConstants.EXTRA_SHOW_IN_MAP_ACTION)){
                sharedLocation = intent.getParcelableExtra(AishaConstants.EXTRA_FRIEND_LOCATION_SHARED_CONTENT_ON_MAP);
                loationCustomeRequest = intent.getParcelableExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST);
                Location location  = intent.getParcelableExtra(AishaConstants.EXTRA_USER_REQUEST_LOCATION);
                upDateMap(location);
            }
        }
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
}
