package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.fragments.ContactsFragment;
import com.cybercareinfoways.aisha.fragments.HistoryFragment;
import com.cybercareinfoways.aisha.fragments.ProfileFragment;
import com.cybercareinfoways.aisha.fragments.ZipprFragment;
import com.cybercareinfoways.aisha.services.SyncTokenService;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.LocationStorage;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigationView;
    FragmentManager fragmentManager;
    private static final int REQUEST_ERROR_RESOLVE = 1001;
    private static final long UPDATE_ITERVAL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_ITERVAL / 2;
    private static final int LOCATION_CODE = 100;
    private static final int RESOLUTION_CODE = 199;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        Integer googleResultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (googleResultCode == ConnectionResult.SUCCESS) {
            buildGoogleApiClient();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, googleResultCode, REQUEST_ERROR_RESOLVE);
            if (dialog != null) {
                dialog.show();
            }
        }
        fragmentManager = getSupportFragmentManager();

        sendTokenServiceCall();
        fragmentManager.beginTransaction().replace(R.id.content, new HistoryFragment(), "History")
                .commit();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.history:
                        fragmentManager.beginTransaction().replace(R.id.content, new HistoryFragment(), "History")
                                .commit();
                        break;
                    case R.id.contacts:
                        fragmentManager.beginTransaction().replace(R.id.content, new ContactsFragment(), "ContactFrag")
                                .commit();
                        break;
                    case R.id.profile:
                        fragmentManager.beginTransaction().replace(R.id.content, new ProfileFragment(), "ProfileFragment")
                                .commit();
                        break;
                    case R.id.zippr:
                        fragmentManager.beginTransaction().replace(R.id.content, new ZipprFragment(), "ZipprFragment").commit();
                        break;
                }
                return true;
            }
        });
    }

    private void sendTokenServiceCall() {
        if (AishaUtilities.isConnectingToInternet(this)) {
            Intent i = new Intent(getApplicationContext(), SyncTokenService.class);
            startService(i);
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

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.home_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//        if (item.getItemId() == R.id.item_contact) {
//            Intent intent = new Intent(HomeActivity.this, ContactsActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        if (item.getItemId() == R.id.item_new) {
//            Intent intent = new Intent(HomeActivity.this, NewContactsActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return false;
//    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusCode = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (statusCode == PackageManager.PERMISSION_GRANTED) {
                getMyCuurentLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "App needs Location to work", Toast.LENGTH_SHORT).show();
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
        if (location != null) {
            Log.i("Location>>", "Current Location is" + location);
            LocationStorage.getInstance().setLocation(location);
            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void getMyCuurentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LocationStorage.getInstance().setLocation(location);
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
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, HomeActivity.this);
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(HomeActivity.this, RESOLUTION_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLUTION_CODE && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        if (requestCode == REQUEST_ERROR_RESOLVE && resultCode == RESULT_OK) {
            buildGoogleApiClient();
            googleApiClient.connect();
            Log.i("TAG", "Connected in OnACtivityResult");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMyCuurentLocation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
            Log.i("TAG", "Connected in OnStart");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }
    }
}
