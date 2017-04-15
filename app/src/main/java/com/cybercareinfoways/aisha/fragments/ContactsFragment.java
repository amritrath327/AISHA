package com.cybercareinfoways.aisha.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.NewContactsActivity;
import com.cybercareinfoways.aisha.activities.TrackAndShareLocationActivity;
import com.cybercareinfoways.aisha.adapters.UserAvailableAdapter;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.aisha.model.LoationRequest;
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.aisha.model.UserRequest;
import com.cybercareinfoways.aisha.model.UserResponse;
import com.cybercareinfoways.aisha.services.ShareLocaionService;
import com.cybercareinfoways.fcm.PushData;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.UserClickListener;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.AcceptRejectResponse;
import com.cybercareinfoways.webapihelpers.LocationRequestResponse;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YELOWFLASH on 03/11/2017.
 */

public class ContactsFragment extends Fragment implements UserClickListener,UserAvailableAdapter.LocationAcceptListner{
    private static final int READCONTACT_CODE = 201;
    ProgressDialog dilogAvailableUser;
    private ArrayList<Contacts> cotactList, contactDataList;
    private String userId;
    private Call<UserResponse>userResponseCall;
    private WebApi webApi;
    private ArrayList<UserData> userAvilableList;
    private RecyclerView rcvAvailableUsers;
    private TextView txtNocontact;
    private UserAvailableAdapter userAvailableAdapter;
    private Call<LocationRequestResponse> locationSharingResponseCall;
    private int durationTime;
    private RequestReceiver requestReceiver;
    private Call<AcceptRejectResponse> acceptRejectResponseCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_fragment, container, false);
        ButterKnife.bind(this, v);
        userId=AishaUtilities.getSharedPreffUserid(getActivity());
        webApi=AishaUtilities.setupRetrofit();
        contactDataList = new ArrayList<>();
        userAvilableList = new ArrayList<>();
        rcvAvailableUsers=(RecyclerView)v.findViewById(R.id.rcvAvailableUsers);
        rcvAvailableUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvAvailableUsers.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        txtNocontact=(TextView)v.findViewById(R.id.txtNocontact);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, getActivity())) {
                getAllContacts();
            }else{
                AishaUtilities.requestPermission(this,new String[]{Manifest.permission.READ_CONTACTS},READCONTACT_CODE);
            }
        }else{
            getAllContacts();
        }
        requestReceiver = new RequestReceiver();
        return v;
    }
    private void showAvailableContacts() {
        UserRequest userRequest=new UserRequest();
        userRequest.setUser_id(userId);
        ArrayList<Contacts>contactses=new ArrayList<>(contactDataList.size());
        for (int i=0;i<contactDataList.size();i++) {
            Contacts contacts = new Contacts();
            contacts.setMobile(contactDataList.get(i).getMobile());
            //contacts.setMobile("9668452233");
            //contacts.setMobile("7504891196");
            contactses.add(contacts);
        }
        userRequest.setContacts(contactses);
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
                            userAvailableAdapter  = new UserAvailableAdapter(getActivity(),userAvilableList);
                            rcvAvailableUsers.setAdapter(userAvailableAdapter);
                            userAvailableAdapter.setListner(ContactsFragment.this);
                            userAvailableAdapter.setOnUSerClicked(ContactsFragment.this);
                            txtNocontact.setVisibility(View.GONE);
                        }else {
                            txtNocontact.setVisibility(View.VISIBLE);
                        }
                    }else {
                        txtNocontact.setVisibility(View.VISIBLE);
                        txtNocontact.setText(response.body().getMessage());
                    }
                }else {
                    Toast.makeText(getActivity(),"Please try agaiin",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (dilogAvailableUser.isShowing()){
                    dilogAvailableUser.dismiss();
                }
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(getActivity(), AishaConstants.CONNECYION_TIME_OUT,Toast.LENGTH_SHORT).show();
                }else {
                    Log.v(AishaConstants.EXTRA_ERROR,t.getMessage());
                }
            }
        });
    }

    private void getAllContacts() {
        new ContactTask(ContactsFragment.this).execute();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==READCONTACT_CODE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            getAllContacts();
        }
    }

    @Override
    public void onStop() {
        if (dilogAvailableUser != null || dilogAvailableUser.isShowing()) {
            dilogAvailableUser.cancel();
        }
        if (userResponseCall != null) {
            userResponseCall.cancel();
        }
        super.onStop();
    }

    public String getNameFromNumber(String mobile) {
        String contactName = null;
        ContentResolver cr = getActivity().getContentResolver();
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
    public void onUserCliked(View view, final int position) {
        final Dialog durationDilog = new Dialog(getActivity());
        WindowManager.LayoutParams params=durationDilog.getWindow().getAttributes();
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width=ViewGroup.LayoutParams.MATCH_PARENT;
        durationDilog.getWindow().setAttributes(params);
        durationDilog.setContentView(R.layout.duration_layout);
        final RadioGroup durationGroup = (RadioGroup) durationDilog.findViewById(R.id.duration_group);
        final RadioGroup durationGroup2 = (RadioGroup) durationDilog.findViewById(R.id.duration_group2);
        final RadioButton rbtn_Fifteen = (RadioButton) durationDilog.findViewById(R.id.rbtn_fifteen);
        final RadioButton rbtn_thirty = (RadioButton) durationDilog.findViewById(R.id.rbtn_thirty);
        final RadioButton rbtn_fortyFive = (RadioButton) durationDilog.findViewById(R.id.rbtn_fortyfive);
        final RadioButton rbtn_Sixty = (RadioButton) durationDilog.findViewById(R.id.rbtn_sixty);
        final RadioButton rbtn_2hr = (RadioButton) durationDilog.findViewById(R.id.rbtn_2hr);
        final RadioButton rbtn_3hr = (RadioButton) durationDilog.findViewById(R.id.rbtn_3hr);
        final RadioButton rbtn_4hr = (RadioButton) durationDilog.findViewById(R.id.rbtn_4hr);
        final RadioButton rbtn_5hr = (RadioButton) durationDilog.findViewById(R.id.rbtn_5hr);
        Button btnRequest = (Button) durationDilog.findViewById(R.id.btnRequest);
        durationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_fifteen:
                        durationTime = 15;
                        durationGroup2.clearCheck();
                        break;
                    case R.id.rbtn_thirty:
                        durationTime = 30;
                        durationGroup2.clearCheck();
                        break;
                    case R.id.rbtn_fortyfive:
                        durationTime = 45;
                        durationGroup2.clearCheck();
                        break;
                    case R.id.rbtn_sixty:
                        durationTime = 60;
                        durationGroup2.clearCheck();
                        break;

                }
            }
        });

        durationGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_2hr:
                        durationTime = 120;
                        durationGroup.clearCheck();
                        break;
                    case R.id.rbtn_3hr:
                        durationTime = 180;
                        durationGroup.clearCheck();
                        break;
                    case R.id.rbtn_4hr:
                        durationTime = 240;
                        durationGroup.clearCheck();
                        break;
                    case R.id.rbtn_5hr:
                        durationTime = 300;
                        durationGroup.clearCheck();
                        break;

                }
            }
        });
        durationDilog.show();
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbtn_Fifteen.isChecked() || rbtn_thirty.isChecked() || rbtn_fortyFive.isChecked() || rbtn_Sixty.isChecked()
                        || rbtn_2hr.isChecked() || rbtn_3hr.isChecked() || rbtn_4hr.isChecked() || rbtn_5hr.isChecked() ){
                    requestLocation(durationTime,durationDilog,position);
                }else {
                    Toast.makeText(getActivity(), "Please select time duration", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocation(int durationTime, final Dialog durationDilog, int pos) {
        UserData userData = userAvilableList.get(pos);
        Map<String,String> mapSendLocation = new HashMap<>(3);
        mapSendLocation.put(AishaConstants.USERID,userId);
        mapSendLocation.put(AishaConstants.EXTRA_MOBILE_NUMBER,userData.getMobile());
        mapSendLocation.put(AishaConstants.EXTRA_DURATION,""+durationTime);
        locationSharingResponseCall = webApi.sendLocationRequest(mapSendLocation);
        locationSharingResponseCall.enqueue(new Callback<LocationRequestResponse>() {
            @Override
            public void onResponse(Call<LocationRequestResponse> call, Response<LocationRequestResponse> response) {
                if (response.isSuccessful()){
                    if (durationDilog.isShowing()){
                        durationDilog.dismiss();
                    }
                    if (response.body().getStatus()==1){
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "Please tryagain.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationRequestResponse> call, Throwable t) {
                if (durationDilog.isShowing()){
                    durationDilog.dismiss();
                }
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(getActivity(), "Connection timeout", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.v("ERROR",t.getMessage());
                }
            }
        });
    }

    @Override
    public void onAccept(final LoationRequest loationRequest) {
        Map<String,String> mapAcceptOrReject = new HashMap<>(3);
        mapAcceptOrReject.put(AishaConstants.USERID,userId);
        mapAcceptOrReject.put(AishaConstants.EXTRA_LOCATION_SHARING_ID,loationRequest.getLocation_sharing_id());
        mapAcceptOrReject.put(AishaConstants.EXTRA_ACKOLEDGEMENT,"1");
        acceptRejectResponseCall = webApi.acceptORreject(mapAcceptOrReject);
        acceptRejectResponseCall.enqueue(new Callback<AcceptRejectResponse>() {
            @Override
            public void onResponse(Call<AcceptRejectResponse> call, Response<AcceptRejectResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ShareLocaionService.class);
                        intent.putExtra(AishaConstants.EXTRA_SEND_LOCATION_REQUEST,loationRequest);
                        getActivity().startService(intent);
                        Intent intent1 = new Intent(getActivity(), TrackAndShareLocationActivity.class);
                        startActivity(intent1);
                        userAvailableAdapter.onRequestAccepted(loationRequest);

                    }else {
                        Toast.makeText(getActivity(), AishaConstants.TRYAGAIN, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), AishaConstants.TRYAGAIN, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AcceptRejectResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(getActivity(), AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                }else {
                    Log.e(AishaConstants.EXTRA_ERROR,t.getMessage());
                }
            }
        });
    }

    @Override
    public void onReject(LoationRequest loationRequest) {
        Map<String,String> mapAcceptOrReject = new HashMap<>(3);
        mapAcceptOrReject.put(AishaConstants.USERID,userId);
        mapAcceptOrReject.put(AishaConstants.EXTRA_LOCATION_SHARING_ID,loationRequest.getLocation_sharing_id());
        mapAcceptOrReject.put(AishaConstants.EXTRA_ACKOLEDGEMENT,"0");
        acceptRejectResponseCall = webApi.acceptORreject(mapAcceptOrReject);
        acceptRejectResponseCall.enqueue(new Callback<AcceptRejectResponse>() {
            @Override
            public void onResponse(Call<AcceptRejectResponse> call, Response<AcceptRejectResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getActivity(), AishaConstants.TRYAGAIN, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), AishaConstants.TRYAGAIN, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AcceptRejectResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(getActivity(), AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                }else {
                    Log.e(AishaConstants.EXTRA_ERROR,t.getMessage());
                }
            }
        });
    }

    public class ContactTask extends AsyncTask<Void,Void,ArrayList<Contacts>>{
        private WeakReference<ContactsFragment> contactsFragmentWeakReference;
        public ContactTask(ContactsFragment contactsFragment){
            contactsFragmentWeakReference = new WeakReference<ContactsFragment>(contactsFragment);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cotactList = new ArrayList();
            dilogAvailableUser = new ProgressDialog(getActivity());
            dilogAvailableUser.setMessage("Please wait...Showing Your Contacts in AISHA");
            dilogAvailableUser.setCanceledOnTouchOutside(false);
            dilogAvailableUser.show();
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... params) {
            Contacts contacts;
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor!=null && cursor.getCount() > 0) {
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
                        if (phoneCursor!=null && phoneCursor.getCount()>0) {
                            if (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contacts.setMobile(phoneNumber);
                            }
                        }
                        if (phoneCursor!=null) {
                            phoneCursor.close();
                        }

                        Cursor emailCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (emailCursor!=null && emailCursor.getCount()>0) {
                            while (emailCursor.moveToNext()) {
                                String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            }
                        }
                        if (emailCursor!=null){
                            emailCursor.close();
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
            if (contactsFragmentWeakReference.get()!=null) {
                if (contactses != null && contactses.size() > 0) {
                    contactDataList.addAll(contactses);
                    if (AishaUtilities.isConnectingToInternet(getActivity())) {
                        showAvailableContacts();
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.item_new) {
            Intent intent = new Intent(getActivity(), NewContactsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter();
        filter.addAction(AishaConstants.EXTRA_ACTION_REQUEST_SHARE_LOCAION);
        (getActivity()).registerReceiver(requestReceiver,filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        (getActivity()).unregisterReceiver(requestReceiver);

    }

    class RequestReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AishaConstants.EXTRA_ACTION_REQUEST_SHARE_LOCAION)){
              //String requestFrom=intent.getStringExtra(AishaConstants.EXTRA_MOBILE);
                //com.cybercareinfoways.aisha.model.LoationRequest request=intent.getParcelableExtra(AishaConstants.EXTRA_USER_REQUEST_LOCATION);
                PushData data=intent.getParcelableExtra(AishaConstants.EXTRA_PUSH_DATA);
                com.cybercareinfoways.aisha.model.LoationRequest request=new LoationRequest(data.getRequestedFrom(),data.getDuration(),data.getLocation_sharing_id());
                userAvailableAdapter.addRequest(request);
                abortBroadcast();
            }
        }
    }
}
