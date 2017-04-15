package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.ZipprCodeResponse;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ZipprFoundActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final int REQUEST_SMS = 202;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtcode)
    TextView txtcode;
    @BindView(R.id.txtZipprAddress)
    TextView txtZipprAddress;
    private ZipprCodeResponse zipprCodeResponse;
    private String zipprCode;
    SupportMapFragment supportMapFragment;
    @BindView(R.id.imgShareWhatapp)
    ImageView imgShareWhatapp;
    @BindView(R.id.imgShareFacebook)
    ImageView imgShareFacebook;
    @BindView(R.id.imgShareSms)
    ImageView imgShareSms;
    @BindView(R.id.imgShareemail)
    ImageView imgShareemail;
    String appLinkUrl;
    private String addressString;
    private BottomSheetBehavior<View> behavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zippr_found);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(120);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        FacebookSdk.sdkInitialize(getApplicationContext());
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            //Log.v("Activity??", "App Link Target URL: " + targetUrl.toString());
            appLinkUrl = targetUrl.toString();
        }
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zipprCodeResponse = getIntent().getParcelableExtra(AishaConstants.EXTRA_ZIPPR);
        zipprCode = getIntent().getStringExtra(AishaConstants.EXTRA_ZIPPR_CODE);
        txtcode.setText(zipprCode);
        if (zipprCodeResponse.getAddress_type().equals("1")){
            txtZipprAddress.setText(zipprCodeResponse.getAddress_line());
            addressString=zipprCodeResponse.getAddress_line();
        }else {
            txtZipprAddress.setText(zipprCodeResponse.getAddress_name()+","+ zipprCodeResponse.getPlot_number()+","+ zipprCodeResponse.getCity()+","+ zipprCodeResponse.getState()+","+ zipprCodeResponse.getPincode());
            addressString=zipprCodeResponse.getAddress_name()+","+ zipprCodeResponse.getPlot_number()+","+ zipprCodeResponse.getCity()+","+ zipprCodeResponse.getState()+","+ zipprCodeResponse.getPincode();
        }
        supportMapFragment=new SupportMapFragment();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        imgShareWhatapp.setOnClickListener(this);
        imgShareFacebook.setOnClickListener(this);
        imgShareSms.setOnClickListener(this);
        imgShareemail.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        if (item.getItemId()==R.id.item_share){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Aisha Zippr code is  "+ zipprCode);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            return true;
        }
        if (item.getItemId()==R.id.item_delete){

        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng zipprLocation = new LatLng(zipprCodeResponse.getLatitude(), zipprCodeResponse.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(zipprLocation).title(addressString));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(zipprLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.imgShareWhatapp){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, zipprCode);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(getPackageManager())!= null){
                startActivity(sendIntent);
            }else {
                Toast.makeText(ZipprFoundActivity.this,"Your device don't have Whastsapp.",Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId()==R.id.imgShareFacebook){
            String  previewImageUrl;

            //appLinkUrl = "https://www.mydomain.com/myapplink";
            previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        //.setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(this, content);
            }
        }
        if (v.getId()==R.id.imgShareSms){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (AishaUtilities.checkPermission(Manifest.permission.SEND_SMS, ZipprFoundActivity.this)) {
                    sendSms();
                }else {
                    AishaUtilities.requestPermission(ZipprFoundActivity.this,new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);
                }
            }else {
                sendSms();
            }

        }
        if (v.getId()==R.id.imgShareemail){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Aisha Zippr code::");
            intent.putExtra(Intent.EXTRA_TEXT, zipprCode );
            try {
                startActivity(Intent.createChooser(intent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ZipprFoundActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms() {
        Intent sendIntent = new Intent(Intent.ACTION_MAIN);
        sendIntent.putExtra("sms_body", zipprCode);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_SMS && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            sendSms();
        }
    }
}
