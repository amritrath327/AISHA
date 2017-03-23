package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.adapters.ContactAdapter;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.OnItemClickListner;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewContactsActivity extends AppCompatActivity implements  View.OnClickListener, OnItemClickListner {
    private static final int READCONTACT_CODE = 201;
    @BindView(R.id.rcvContacts)
    RecyclerView rcvContacts;
    @BindView(R.id.imgWhastapp)
    ImageView imgWhastapp;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.imgMore)
    ImageView imgMore;
    ShareLinkContent content;
    ProgressDialog dilogAvailableUser;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ContactAdapter contactAdapter;
    private ArrayList<Contacts> cotactList, contactsDataList;
    private String appLinkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contacts);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), getIntent());
        if (targetUrl != null) {
            //Log.v("Activity??", "App Link Target URL: " + targetUrl.toString());
            appLinkUrl = targetUrl.toString();
        }
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New AISHA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rcvContacts.setLayoutManager(new GridLayoutManager(NewContactsActivity.this, 3));
        cotactList = new ArrayList();
        contactsDataList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, NewContactsActivity.this)) {
                getAllContacts();
            } else {
                AishaUtilities.requestPermission(this, new String[]{Manifest.permission.READ_CONTACTS}, READCONTACT_CODE);
            }
        } else {
            getAllContacts();
        }
        imgWhastapp.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgMore.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READCONTACT_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
        }
    }

    private void getAllContacts() {
        new ContactTask(NewContactsActivity.this).execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgWhastapp) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing on whatsapp");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            } else {
                Toast.makeText(NewContactsActivity.this, "Your device don't have Whastsapp.", Toast.LENGTH_SHORT).show();
            }
        }
        if (view.getId() == R.id.imgFacebook) {
            String previewImageUrl;

            //appLinkUrl = "https://www.mydomain.com/myapplink";
            String link = appLinkUrl;
            previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        //.setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(this, content);
            }
        }
        if (view.getId() == R.id.imgMore) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is the text that will be shared.");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }
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

    @Override
    public void onStop() {
        if (dilogAvailableUser != null || dilogAvailableUser.isShowing()) {
            dilogAvailableUser.cancel();
        }
        super.onStop();
    }

    @Override
    public void OnItemClick(View view, int pos) {
        Contacts contacts = contactsDataList.get(pos);
        Intent intent = new Intent(NewContactsActivity.this, InvitationActivity.class);
        intent.putExtra(AishaConstants.EXTRA_INVITATION, contacts);
        startActivity(intent);
    }

    public class ContactTask extends AsyncTask<Void, Void, ArrayList<Contacts>>  {
        private WeakReference<NewContactsActivity> newContactsActivityWeakReference;

        public ContactTask(NewContactsActivity newContactsActivity) {
            newContactsActivityWeakReference = new WeakReference<NewContactsActivity>(newContactsActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dilogAvailableUser = new ProgressDialog(NewContactsActivity.this);
            dilogAvailableUser.setMessage("Please wait...Showing Your Contacts");
            dilogAvailableUser.setCanceledOnTouchOutside(false);
            dilogAvailableUser.show();
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... params) {
            Contacts contacts;
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor != null && cursor.getCount() > 0) {
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
                        if (phoneCursor != null && phoneCursor.getCount() > 0) {
                            if (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contacts.setMobile(phoneNumber);
                            }
                        }
                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }

                        Cursor emailCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (emailCursor != null && emailCursor.getCount() > 0) {
                            while (emailCursor.moveToNext()) {
                                String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            }
                        }
                        if (emailCursor != null) {
                            emailCursor.close();
                        }
                        cotactList.add(contacts);
                    }
                }
            }
            return cotactList.size() > 0 ? cotactList : null;
        }

        @Override
        protected void onPostExecute(ArrayList<Contacts> contactses) {
            super.onPostExecute(contactses);
            if (newContactsActivityWeakReference.get() != null) {
                if (dilogAvailableUser!=null && dilogAvailableUser.isShowing()){
                    dilogAvailableUser.dismiss();
                }
                if (contactses != null && contactses.size() > 0) {
                    contactAdapter = new ContactAdapter(NewContactsActivity.this, contactses);
                    rcvContacts.setAdapter(contactAdapter);
                    contactAdapter.setOnItemclickListner(NewContactsActivity.this);
                    contactsDataList.addAll(contactses);
                }
            }
        }


    }



}
