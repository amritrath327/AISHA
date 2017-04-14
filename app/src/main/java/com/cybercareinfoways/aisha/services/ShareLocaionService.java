package com.cybercareinfoways.aisha.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.cybercareinfoways.aisha.model.LoationRequest;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.SharingResponse;
import com.google.android.gms.common.ConnectionResult;
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

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nutan on 24-03-2017.
 */

public class ShareLocaionService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    boolean isTimerStarted;
    Handler handler;
    private long duration;
    private static final int REQUEST_ERROR_RESOLVE = 1001;
    private static final long UPDATE_ITERVAL = 5000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_ITERVAL / 2;
    private static final int LOCATION_CODE = 100;
    private static final int RESOLUTION_CODE = 199;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    com.cybercareinfoways.aisha.model.LoationRequest locationCostomeRequest;
    private String userId;
    Call<SharingResponse> sharingResponseCall;
    private WebApi sharingLocApi;

    public ShareLocaionService() {
        super("ShareLocaionService");
        handler=new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userId= AishaUtilities.getSharedPreffUserid(ShareLocaionService.this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharingLocApi=AishaUtilities.setupRetrofit();
        userId= AishaUtilities.getSharedPreffUserid(ShareLocaionService.this);
        locationCostomeRequest = intent.getParcelableExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST);
        buildGoogleApiClient();
        googleApiClient.connect();
        //duration=intent.getIntExtra(AishaConstants.EXTRA_DURATION,1000);
        if(!isTimerStarted){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, ShareLocaionService.this);
                    Toast.makeText(ShareLocaionService.this,"Location Ended",Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            },locationCostomeRequest.getDuration() * 60 * 1000);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
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
    public void onConnected(@Nullable Bundle bundle) {
        getMyCuurentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("CONNECTION", "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("CONNECTION", "FAILED");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i("Location>>", "Current Location is" + location);
            //TODO send location to server
             sendMyLocation(userId,location,locationCostomeRequest);
            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void sendMyLocation(String userId, final Location location, final LoationRequest locationCostomeRequest) {
        Map<String,String> mapShareLocation=  new HashMap<>(4);
        mapShareLocation.put(AishaConstants.USERID,userId);
        mapShareLocation.put(AishaConstants.EXTRA_LOCATION_SHARING_ID,locationCostomeRequest.getLocation_sharing_id());
        mapShareLocation.put(AishaConstants.EXTRA_LONGITUDE,""+location.getLongitude());
        mapShareLocation.put(AishaConstants.EXTRA_LATTITUDE,""+location.getLatitude());
        sharingResponseCall  = sharingLocApi.getUpadatedLocationFromervice(mapShareLocation);
        sharingResponseCall.enqueue(new Callback<SharingResponse>() {
            @Override
            public void onResponse(Call<SharingResponse> call, Response<SharingResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        Intent intent  = new Intent();
                        intent.setAction(AishaConstants.EXTRA_SHOW_IN_MAP_ACTION);
                        intent.putParcelableArrayListExtra(AishaConstants.EXTRA_FRIEND_LOCATION_SHARED_CONTENT_ON_MAP,response.body().getLocation());
                        intent.putExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST,locationCostomeRequest);
                        intent.putExtra(AishaConstants.EXTRA_USER_REQUEST_LOCATION,location);
                        sendBroadcast(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<SharingResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(ShareLocaionService.this, AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                }else {
                    Log.e(AishaConstants.EXTRA_ERROR,t.getMessage());
                }
            }
        });
    }

    private void getMyCuurentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            sendMyLocation(userId,location,locationCostomeRequest);
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
                            if (ActivityCompat.checkSelfPermission(ShareLocaionService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ShareLocaionService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, ShareLocaionService.this);
                            break;
                    }
                }
            });

        }
    }
}
