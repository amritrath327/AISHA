package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.adapters.UserAvailableAdapter;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.aisha.model.UserRequest;
import com.cybercareinfoways.aisha.model.UserResponse;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewContactsActivity extends AppCompatActivity {
    private ArrayList<Contacts> cotactList,contactDataList;
    private static final int READCONTACT_CODE = 201;
    private String userId;
    private Call<UserResponse>userResponseCall;
    private WebApi webApi;
    private Toolbar toolbar;
    private ArrayList<UserData> userAvilableList;
    private RecyclerView rcvAvailableUsers;
    private TextView txtNocontact;
    private UserAvailableAdapter userAvailableAdapter;
    ProgressDialog dilogAvailableUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contacts);
        userId=AishaUtilities.getSharedPreffUserid(NewContactsActivity.this);
        webApi=AishaUtilities.setupRetrofit();
        contactDataList = new ArrayList<>();
        userAvilableList = new ArrayList<>();
        rcvAvailableUsers=(RecyclerView)findViewById(R.id.rcvAvailableUsers);
        rcvAvailableUsers.setLayoutManager(new LinearLayoutManager(NewContactsActivity.this));
        rcvAvailableUsers.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        txtNocontact=(TextView)findViewById(R.id.txtNocontact);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, this)) {
                getAllContacts();
            }else{
                AishaUtilities.requestPermission(this,new String[]{Manifest.permission.READ_CONTACTS},READCONTACT_CODE);
            }
        }else{
            getAllContacts();
        }
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New AISHA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showAvailableContacts() {
        UserRequest userRequest=new UserRequest();
        userRequest.setUser_id(userId);
        //for (int i=0;i<contactDataList.size();i++){
            Contacts contacts=new Contacts();
            //contacts.setMobile(contactDataList.get(i).getMobile());
             contacts.setMobile("9668452233");
            contactDataList.add(contacts);
        //}
        userRequest.setContacts(contactDataList);
        userResponseCall = webApi.getAvailableUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (dilogAvailableUser.isShowing()){
                    dilogAvailableUser.dismiss();
                }
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        userAvilableList.addAll(response.body().getContacts());
                        if (userAvilableList!=null && userAvilableList.size()>0){
                            userAvailableAdapter  = new UserAvailableAdapter(NewContactsActivity.this,userAvilableList);
                            rcvAvailableUsers.setAdapter(userAvailableAdapter);
                            txtNocontact.setVisibility(View.GONE);
                        }else {
                            txtNocontact.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    Toast.makeText(NewContactsActivity.this,"Please try agaiin",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (dilogAvailableUser.isShowing()){
                    dilogAvailableUser.dismiss();
                }
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(NewContactsActivity.this, AishaConstants.CONNECYION_TIME_OUT,Toast.LENGTH_SHORT).show();
                }else {
                    Log.v(AishaConstants.EXTRA_ERROR,t.getMessage());
                }
            }
        });
    }

    private void getAllContacts() {
        new ContactTask(NewContactsActivity.this).execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==READCONTACT_CODE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            getAllContacts();
        }
    }
    public class ContactTask extends AsyncTask<Void,Void,ArrayList<Contacts>>{
        private WeakReference<NewContactsActivity> newContactsActivityWeakReference;
        public ContactTask(NewContactsActivity newContactsActivity){
            newContactsActivityWeakReference = new WeakReference<NewContactsActivity>(newContactsActivity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cotactList = new ArrayList();
            dilogAvailableUser = new ProgressDialog(NewContactsActivity.this);
            dilogAvailableUser.setMessage("Please wait...Showing Your Contacts in AISHA");
            dilogAvailableUser.setCanceledOnTouchOutside(false);
            dilogAvailableUser.show();
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... params) {
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
                            contacts.setMobile(phoneNumber);
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
            return cotactList;
        }

        @Override
        protected void onPostExecute(ArrayList<Contacts> contactses) {
            super.onPostExecute(contactses);
            if (newContactsActivityWeakReference.get()!=null) {
                if (contactses != null && contactses.size() > 0) {
                    contactDataList.addAll(contactses);
                    if (AishaUtilities.isConnectingToInternet(NewContactsActivity.this)) {
                        showAvailableContacts();
                    }
                }
            }
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

    @Override
    protected void onStop() {
        if (dilogAvailableUser!=null || dilogAvailableUser.isShowing()){
            dilogAvailableUser.cancel();
        }
        if (userResponseCall!=null){
            userResponseCall.cancel();
        }
        super.onStop();
    }
}