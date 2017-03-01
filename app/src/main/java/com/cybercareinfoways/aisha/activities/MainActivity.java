package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.adapters.ContactAdapter;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.helpers.AishaUtilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int READCONTACT_CODE = 201;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ContactAdapter contactAdapter;
    private ArrayList<Contacts> cotactList;
    @BindView(R.id.rcvContacts)
    RecyclerView rcvContacts;
    @BindView(R.id.imgWhastapp)
    ImageView imgWhastapp;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.imgMore)
    ImageView imgMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        rcvContacts.setLayoutManager(new GridLayoutManager(this,3));
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, this)) {
                getAllContacts();
            }else{
                AishaUtilities.requestPermission(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},READCONTACT_CODE);
            }
        }else{
            getAllContacts();
        }
        imgWhastapp.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgMore.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==READCONTACT_CODE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            getAllContacts();
        }
    }

    private void getAllContacts() {
        cotactList = new ArrayList();
        Contacts contacts;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contacts = new Contacts();
                    contacts.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    cotactList.add(contacts);
                }
            }
        }
        contactAdapter = new ContactAdapter(MainActivity.this,cotactList);
        rcvContacts.setAdapter(contactAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.imgWhastapp){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing on whatsapp");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(getPackageManager())!= null){
                startActivity(sendIntent);
            }else {
                Toast.makeText(MainActivity.this,"Your device don't have Whastsapp.",Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId()==R.id.imgMore){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is the text that will be shared.");
            startActivity(Intent.createChooser(sharingIntent,"Share using"));
        }
    }
}
