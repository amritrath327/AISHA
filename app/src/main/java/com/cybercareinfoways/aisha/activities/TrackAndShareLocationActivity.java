package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.fragments.ZipprFragment;
import com.cybercareinfoways.aisha.model.SharedLocation;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.StoreCurrentLocation;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.Result;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackAndShareLocationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    private ShowForMapReciver showForMapReciver;
    public static SharedLocation sharedLocation;
    public static com.cybercareinfoways.aisha.model.LoationRequest loationCustomeRequest;
    public static Location location;
    private ImageView imgUserMarker,imgUserfriendMrker,imgMeetingPointMarker;
    private String addresValue;
    private WebApi webApi;
    private static final int REQUEST_ERROR_RESOLVE = 1001;
    private static final long UPDATE_ITERVAL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_ITERVAL / 2;
    private static final int LOCATION_CODE = 100;
    private static final int RESOLUTION_CODE = 199;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private String userId;
    Call<Result> resultCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_and_share_location);
        imgUserMarker = (ImageView)findViewById(R.id.imgUserMarker);
        imgUserfriendMrker = (ImageView)findViewById(R.id.imgUserfriendMrker);
        imgMeetingPointMarker = (ImageView)findViewById(R.id.imgMeetingPointMarker);
        webApi= AishaUtilities.setupRetrofit();
        userId = AishaUtilities.getSharedPreffUserid(this);
        Integer googleResultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TrackAndShareLocationActivity.this);
        if (googleResultCode == ConnectionResult.SUCCESS) {
            buildGoogleApiClient();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TrackAndShareLocationActivity.this, googleResultCode, REQUEST_ERROR_RESOLVE);
            if (dialog != null) {
                dialog.show();
            }
        }
        showForMapReciver = new ShowForMapReciver();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.containerMap,supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
        imgUserMarker.setOnClickListener(this);
        imgUserfriendMrker.setOnClickListener(this);
        imgMeetingPointMarker.setOnClickListener(this);
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
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(TrackAndShareLocationActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_ITERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setSmallestDisplacement(10.0f);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.imgUserMarker){
            if (mMap!=null){
                if (location!=null){
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_marker);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Me").icon(icon)).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 15);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        }
        if (v.getId()==R.id.imgUserfriendMrker){
            if (mMap!=null){
                if (sharedLocation!=null){
                    LatLng friendLocation = new LatLng(sharedLocation.getTrack().get(0).getLatitude(), sharedLocation.getTrack().get(0).getLongitude());
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_friend_marker);
                    mMap.addMarker(new MarkerOptions().position(friendLocation).title("Friend").icon(icon)).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(friendLocation));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(friendLocation, 15);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        }
        if (v.getId()==R.id.imgMeetingPointMarker){
            final Dialog locationDialog = new Dialog(TrackAndShareLocationActivity.this);
            WindowManager.LayoutParams params=locationDialog.getWindow().getAttributes();
            params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width=ViewGroup.LayoutParams.MATCH_PARENT;
            locationDialog.getWindow().setAttributes(params);
            locationDialog.setContentView(R.layout.location_dialog);
            EditText etMeetingAddress = (EditText)locationDialog.findViewById(R.id.etMeetingAddress);
            addresValue = etMeetingAddress.getText().toString().trim();
            LatLng latLng = getLocationFromAddress(TrackAndShareLocationActivity.this,addresValue);
            if (latLng!=null){
                 sendMeetingPoint(latLng.latitude,latLng.longitude,locationDialog);
            }else {
                Toast.makeText(this, "Location Couldn't traced.", Toast.LENGTH_SHORT).show();
            }
            Button btnCurrentLocaton = (Button)locationDialog.findViewById(R.id.btnCurrentLocaton);
            btnCurrentLocaton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StoreCurrentLocation.getInstance()!=null && StoreCurrentLocation.getInstance().getLocation()!=null){
                        sendMeetingPoint(StoreCurrentLocation.getInstance().getLocation().getLatitude(),StoreCurrentLocation.getInstance().getLocation().getLongitude(),locationDialog);
                    }else {
                        Toast.makeText(TrackAndShareLocationActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            locationDialog.show();
        }
    }

    private void sendMeetingPoint(final double latitude, final double longitude, final Dialog locationDialog) {
        Map<String,String> mapSendMeetingPoint = new HashMap<>(4);
        mapSendMeetingPoint.put(AishaConstants.USERID,userId);
        mapSendMeetingPoint.put(AishaConstants.EXTRA_LOCATION_SHARING_ID,sharedLocation.getLocation_sharing_id());
        mapSendMeetingPoint.put(AishaConstants.EXTRA_LATTITUDE,""+latitude);
        mapSendMeetingPoint.put(AishaConstants.EXTRA_LONGITUDE,""+longitude);
        resultCall=webApi.sendMeetingPoint(mapSendMeetingPoint);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()){
                    if (locationDialog!=null && locationDialog.isShowing()){
                        locationDialog.dismiss();
                    }
                    if (response.body().getStatus()==1){
                        LatLng meetingPoint = new LatLng(latitude,longitude);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_meeting_marker);
                        mMap.addMarker(new MarkerOptions().position(meetingPoint).title("Meeting Point").icon(icon)).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(meetingPoint));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(meetingPoint, 15);
                        mMap.animateCamera(cameraUpdate);
                        Toast.makeText(TrackAndShareLocationActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(TrackAndShareLocationActivity.this, "Tryagain", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(TrackAndShareLocationActivity.this, "Tryagain", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (locationDialog!=null && locationDialog.isShowing()){
                    locationDialog.dismiss();
                }
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(TrackAndShareLocationActivity.this, AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                }else {
                    Log.v("ERROR",t.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusCode = ContextCompat.checkSelfPermission(TrackAndShareLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (statusCode == PackageManager.PERMISSION_GRANTED) {
                getMyCuurentLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(TrackAndShareLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(TrackAndShareLocationActivity.this, "App needs Location to work", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
            }
        } else {
            getMyCuurentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("LOCATION", "SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("CONNECTION", "FAILED");
    }

    @Override
    public void onLocationChanged(Location location) {
        StoreCurrentLocation.getInstance().setLocation(location);
        Log.i("Location>>", "Current Location is" + location+"......////"+StoreCurrentLocation.getInstance().getLocation().getLatitude()+",,,,"+StoreCurrentLocation.getInstance().getLocation().getLongitude());
        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
    private void getMyCuurentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            StoreCurrentLocation.getInstance().setLocation(location);
            Log.i("Location>>", "location" + location+"......////"+StoreCurrentLocation.getInstance().getLocation().getLatitude()+",,,,"+StoreCurrentLocation.getInstance().getLocation().getLongitude());
        } else {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            if (ActivityCompat.checkSelfPermission(TrackAndShareLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackAndShareLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, TrackAndShareLocationActivity.this);
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(TrackAndShareLocationActivity.this, RESOLUTION_CODE);
                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        }
    }


    class ShowForMapReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AishaConstants.EXTRA_SHOW_IN_MAP_ACTION)){
                sharedLocation = intent.getParcelableExtra(AishaConstants.EXTRA_FRIEND_LOCATION_SHARED_CONTENT_ON_MAP);
                loationCustomeRequest = intent.getParcelableExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST);
                location  = intent.getParcelableExtra(AishaConstants.EXTRA_USER_REQUEST_LOCATION);
                upDateMap(location,sharedLocation,loationCustomeRequest);
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
    public void upDateMap(Location location,SharedLocation sharedLocation,com.cybercareinfoways.aisha.model.LoationRequest loationCustomeRequest ){
        if (mMap!=null && location!=null && sharedLocation!=null ){
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Me")).showInfoWindow();
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 15);
            //mMap.animateCamera(cameraUpdate);

            LatLng friendLocation = new LatLng(sharedLocation.getTrack().get(0).getLatitude(), sharedLocation.getTrack().get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(friendLocation).title("Friend")).showInfoWindow();
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(friendLocation));
//            CameraUpdate cameraUpdateFrnd = CameraUpdateFactory.newLatLngZoom(friendLocation, 15);
            //mMap.animateCamera(cameraUpdateFrnd);
        }
    }
    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            getMyCuurentLocation();
        }
    }

    @Override
    public void onStart() {
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
            Log.i("TAG", "Connected in OnStart");
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }
        super.onStop();
    }
}
