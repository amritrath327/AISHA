package com.cybercareinfoways.aisha.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.SearchAishaCodeActivity;
import com.cybercareinfoways.aisha.activities.ZipprActivity;
import com.cybercareinfoways.aisha.activities.ZipprDetilsActivity;
import com.cybercareinfoways.aisha.activities.ZipprMapActivity;
import com.cybercareinfoways.aisha.adapters.ZipprLisdtAdapter;
import com.cybercareinfoways.aisha.model.ZipprListData;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.DividerItemDecoration;
import com.cybercareinfoways.helpers.LocationStorage;
import com.cybercareinfoways.helpers.OnItemClickListner;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.ZipprListDataResponse;
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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nutan on 13-03-2017.
 */

public class ZipprFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnItemClickListner {
    private static final int REQUEST_ERROR_RESOLVE = 1001;
    private static final long UPDATE_ITERVAL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_ITERVAL / 2;
    private static final int LOCATION_CODE = 100;
    private static final int RESOLUTION_CODE = 199;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    @BindView(R.id.fab_add_zippr)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.txtNoZippr)
    TextView txtNoZippr;
    private RecyclerView rcvZippr;
    private String userId;
    private ZipprLisdtAdapter zipprLisdtAdapter;
    private ArrayList<ZipprListData> zipprListDatas;
    private WebApi zipprCodeApi;
    private Call<ZipprListDataResponse> zipprListDataResponseCall;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.zippr_fragment,container,false);
        ButterKnife.bind(this,view);
        userId = AishaUtilities.getSharedPreffUserid(getActivity());
        Integer googleResultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if (googleResultCode == ConnectionResult.SUCCESS) {
            buildGoogleApiClient();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), googleResultCode, REQUEST_ERROR_RESOLVE);
            if (dialog != null) {
                dialog.show();
            }
        }
        zipprCodeApi = AishaUtilities.setupRetrofit();
        zipprListDatas=new ArrayList<>();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Showing Codes...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationStorage.getInstance()!=null && LocationStorage.getInstance().getLocation()!=null) {
//                    Intent intent = new Intent(getActivity(), ZipprActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(getActivity(), ZipprMapActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rcvZippr=(RecyclerView)view.findViewById(R.id.rcvZippr);
        rcvZippr.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvZippr.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
//        if (AishaUtilities.isConnectingToInternet(getActivity())){
//            displayAllZipprCode(userId);
//        }
        return view;
    }


    private void displayAllZipprCode(String userId) {
        if (zipprListDatas!=null && zipprListDatas.size()>0){
            zipprListDatas.clear();
            zipprLisdtAdapter.clear();
        }
        Map<String,String> mapZipprCode = new HashMap<>(1);
        mapZipprCode.put(AishaConstants.USERID,userId);
        zipprListDataResponseCall = zipprCodeApi.getAllZipprCode(mapZipprCode);
        zipprListDataResponseCall.enqueue(new Callback<ZipprListDataResponse>() {
            @Override
            public void onResponse(Call<ZipprListDataResponse> call, Response<ZipprListDataResponse> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    if (response.body().getStatus()==1){
                        if (response.body().getList_zipper()!=null && response.body().getList_zipper().size()>0) {
                            zipprListDatas.addAll(response.body().getList_zipper());
                            Collections.reverse(zipprListDatas);
                            zipprLisdtAdapter=new ZipprLisdtAdapter(getActivity(),zipprListDatas,ZipprFragment.this);
                            rcvZippr.setAdapter(zipprLisdtAdapter);
                            txtNoZippr.setVisibility(View.GONE);
                        }else {
                            txtNoZippr.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ZipprListDataResponse> call, Throwable t) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(getActivity(), AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                }else {
                    Log.v("ERROR",t.getMessage());
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
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
    public void onLocationChanged(Location location) {
        if (location != null) {
            LocationStorage.getInstance().setLocation(location);
            Log.i("Location>>", "Current Location is" + location+"......////"+LocationStorage.getInstance().getLocation().getLatitude()+",,,,"+LocationStorage.getInstance().getLocation().getLongitude());
            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusCode = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (statusCode == PackageManager.PERMISSION_GRANTED) {
                getMyCuurentLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getActivity(), "App needs Location to work", Toast.LENGTH_SHORT).show();
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
    private void getMyCuurentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LocationStorage.getInstance().setLocation(location);
            Log.i("Location>>", "location" + location+"......////"+LocationStorage.getInstance().getLocation().getLatitude()+",,,,"+LocationStorage.getInstance().getLocation().getLongitude());
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
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, ZipprFragment.this);
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), RESOLUTION_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLUTION_CODE && resultCode == getActivity().RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (requestCode==REQUEST_ERROR_RESOLVE && resultCode==getActivity().RESULT_OK){
            buildGoogleApiClient();
            googleApiClient.connect();
            Log.i("TAG", "Connected in OnACtivityResult");
        }
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
        if (AishaUtilities.isConnectingToInternet(getActivity())){
            displayAllZipprCode(userId);
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
        if (zipprListDataResponseCall!=null){
            zipprListDataResponseCall.cancel();
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.item_search){
            Intent intent=new Intent(getActivity(), SearchAishaCodeActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void OnItemClick(View view, int pos) {
        ZipprListData zipprListData = zipprListDatas.get(pos);
        Intent intent = new Intent(getActivity(), ZipprDetilsActivity.class);
        intent.putExtra(AishaConstants.EXTRA_ZIPPR_DATA,zipprListData);
        startActivity(intent);
    }
}
