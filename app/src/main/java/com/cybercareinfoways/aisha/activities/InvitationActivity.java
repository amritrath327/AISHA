package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.TextDrawable;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvitationActivity extends AppCompatActivity {
    private static final int REQUEST_SEND_SMS = 201;
    private Contacts contacts;
    @BindView(R.id.imgContactprofilePic)
    ImageView imgContactprofilePic;
    @BindView(R.id.txtContactName)
    TextView txtContactName;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.txtNametext)
    TextView txtNametext;
    @BindView(R.id.btnWhatsapp)
    Button btnWhatsapp;
    @BindView(R.id.btnSms)
    Button btnSms;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contacts = getIntent().getParcelableExtra(AishaConstants.EXTRA_INVITATION);
        Log.v("CONATCTYYYY",contacts.toString());
        if (!TextUtils.isEmpty(contacts.getContactName())) {
            getSupportActionBar().setTitle(contacts.getContactName());
            TextDrawable textDrawable = new TextDrawable(contacts.getContactName(), AishaUtilities.dpToPx(InvitationActivity.this,50));
            imgContactprofilePic.setImageDrawable(textDrawable);
            txtContactName.setText(contacts.getContactName());
            txtNametext.setText("Invite "+ contacts.getContactName()+ " To AISHA");
        }
        txtNumber.setText(contacts.getMobile());
        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + contacts.getMobile());
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.putExtra("sms_body", "DownloAD Aisha");
                i.setPackage("com.whatsapp");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }else {
                    Toast.makeText(InvitationActivity.this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (AishaUtilities.checkPermission(Manifest.permission.SEND_SMS, InvitationActivity.this)) {
                        sendSms(contacts.getMobile(), AishaConstants.EXTRA_SMS);
                    } else {
                        AishaUtilities.requestPermission(InvitationActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                    }
                }else {
                    sendSms(contacts.getMobile(), AishaConstants.EXTRA_SMS);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_SEND_SMS && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            sendSms(contacts.getMobile(), AishaConstants.EXTRA_SMS);
        }
    }

    private void sendSms(String mobile, String s) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobile, null,s, null, null);
            Toast.makeText(InvitationActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(InvitationActivity.this, "Message not Sent", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
}
