package com.cybercareinfoways.aisha.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.InvitationActivity;
import com.cybercareinfoways.aisha.activities.NewContactsActivity;
import com.cybercareinfoways.aisha.adapters.ContactAdapter;
import com.cybercareinfoways.aisha.adapters.UserAvailableAdapter;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.aisha.model.UserRequest;
import com.cybercareinfoways.aisha.model.UserResponse;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.OnItemClickListner;
import com.cybercareinfoways.helpers.TextDrawable;
import com.cybercareinfoways.helpers.UserClickListener;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.LocationRequestResponse;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YELOWFLASH on 03/11/2017.
 */

public class ContactsFragment extends Fragment implements View.OnClickListener, UserClickListener {
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    private static final int READCONTACT_CODE = 201;
    private static final int REQUEST_ACESS_STORAGE = 3;
    private static final int REQUEST_ACESS_CAMERA = 2;
    @BindView(R.id.rcvContacts)
    RecyclerView rcvContacts;
    @BindView(R.id.imgWhastapp)
    ImageView imgWhastapp;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.imgMore)
    ImageView imgMore;
    ShareLinkContent content;
    @BindView(R.id.relUserBoard)
    RelativeLayout relUserBoard;
    @BindView(R.id.linUserNAme)
    LinearLayout linUserNAme;
    @BindView(R.id.imgArrow)
    ImageView imgArrow;
    int countLayout = 1;
    @BindView(R.id.imgSmallUserpic)
    ImageView imgSmallUserpic;
    @BindView(R.id.imgUserPic)
    ImageView imgUserPic;
    @BindView(R.id.framePic)
    FrameLayout framePic;
    @BindView(R.id.txtUserName)
    TextView txtUserName;
    @BindView(R.id.txtSmallUsername)
    TextView txtSmallUsername;
    @BindView(R.id.rcvAvailableUsers)
    RecyclerView rcvAvailableUsers;
    @BindView(R.id.txtCircularContacts)
    TextView txtCircularContacts;
    ProgressDialog dilogAvailableUser;
    private Uri uri;
    //    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    private ContactAdapter contactAdapter;
    private ArrayList<Contacts> cotactList, contactsDataList;
    private ArrayList<UserData> userAvilableList;
    private String appLinkUrl;
    private String userName;
    private Call<UserResponse> userResponseCall;
    private Call<LocationRequestResponse> locationSharingResponseCall;
    private WebApi webApi;
    private String userId;
    private UserAvailableAdapter userAvailableAdapter;
    private int durationTime;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_contacts, container, false);
        ButterKnife.bind(this, v);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getActivity().getApplicationContext(), getActivity().getIntent());
        if (targetUrl != null) {
            //Log.v("Activity??", "App Link Target URL: " + targetUrl.toString());
            appLinkUrl = targetUrl.toString();
        }
        rcvContacts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        cotactList = new ArrayList();
        contactsDataList = new ArrayList<>();
        rcvAvailableUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvAvailableUsers.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        userId = AishaUtilities.getSharedPreffUserid(getActivity());
        webApi = AishaUtilities.setupRetrofit();
        userAvilableList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, getActivity())) {
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
        relUserBoard.setOnClickListener(this);
        framePic.setOnClickListener(this);
        userName = AishaUtilities.getSharedPreffName(getActivity());
        TextDrawable textDrawable = new TextDrawable(userName, AishaUtilities.dpToPx(getActivity(), 30));
        imgSmallUserpic.setImageDrawable(textDrawable);
        imgUserPic.setImageDrawable(textDrawable);
        txtSmallUsername.setText(userName);
        txtUserName.setText(userName);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READCONTACT_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
        }
        if (requestCode == REQUEST_ACESS_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDilog();
        }
        if (requestCode == REQUEST_ACESS_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCameraApp();
        }
    }

    private void getAllContacts() {
        new ContactTask(ContactsFragment.this).execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgWhastapp) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing on whatsapp");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(sendIntent);
            } else {
                Toast.makeText(getActivity(), "Your device don't have Whastsapp.", Toast.LENGTH_SHORT).show();
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
        if (view.getId() == R.id.relUserBoard) {
            if (countLayout == 1) {
                linUserNAme.setVisibility(View.VISIBLE);
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_colap_arrow));
                countLayout++;
            } else {
                linUserNAme.setVisibility(View.GONE);
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_exp_arrow));
                countLayout--;
            }
        }
        if (view.getId() == R.id.framePic) {
            handleCameraForPickingPhoto();
        }
    }

    private void handleCameraForPickingPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getActivity())) {
                startDilog();
            } else {
                AishaUtilities.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ACESS_STORAGE);
            }
        } else {
            startDilog();
        }
    }

    private void startDilog() {
        AlertDialog.Builder myAlertDilog = new AlertDialog.Builder(getActivity());
        myAlertDilog.setTitle("Upload picture option..");
        myAlertDilog.setMessage("Where to upload picture????");
        myAlertDilog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent picIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                picIntent.setType("image/*");
                picIntent.putExtra("return_data", true);
                startActivityForResult(picIntent, GALLERY_REQUEST);
            }
        });
        myAlertDilog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (AishaUtilities.checkPermission(Manifest.permission.CAMERA, getActivity())) {
                        openCameraApp();
                    } else {
                        AishaUtilities.requestPermission(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_ACESS_CAMERA);
                    }
                } else {
                    openCameraApp();
                }
            }
        });
        AlertDialog alertDialog = myAlertDilog.create();
        alertDialog.show();
    }

    private void openCameraApp() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(picIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    try {
                        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, options);
                        options.inSampleSize = calculateInSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        Bitmap image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, options);
                        imgUserPic.setImageBitmap(image);
                        imgSmallUserpic.setImageBitmap(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    uri = getImageUri(getActivity(), bitmap);
                    File finalFile = new File(getRealPathFromUri(uri));
                    imgUserPic.setImageBitmap(bitmap);
                    imgSmallUserpic.setImageBitmap(bitmap);
                } else if (data.getExtras() == null) {

                    Toast.makeText(getActivity().getApplicationContext(),
                            "No extras to retrieve!", Toast.LENGTH_SHORT)
                            .show();

                    BitmapDrawable thumbnail = new BitmapDrawable(
                            getResources(), data.getData().getPath());
                    imgUserPic.setImageDrawable(thumbnail);
                    imgSmallUserpic.setImageDrawable(thumbnail);
                }

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Uri getImageUri(Activity mainActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(mainActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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

    private void showAvailableContacts() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUser_id(userId);
        ArrayList<Contacts> contactses = new ArrayList<>(contactsDataList.size());
        for (int i = 0; i < contactsDataList.size(); i++) {
            Contacts contacts = new Contacts();
            contacts.setMobile(contactsDataList.get(i).getMobile());
            //contacts.setMobile("9668452233");
            contactses.add(contacts);
        }
        userRequest.setContacts(contactses);
        userResponseCall = webApi.getAvailableUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (dilogAvailableUser.isShowing()) {
                    dilogAvailableUser.dismiss();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 1) {
                        txtCircularContacts.setText("" + response.body().getContacts().size());
                        userAvilableList.addAll(response.body().getContacts());
                        if (userAvilableList != null && userAvilableList.size() > 0) {
                            userAvailableAdapter = new UserAvailableAdapter(getActivity(), userAvilableList);
                            rcvAvailableUsers.setAdapter(userAvailableAdapter);
                            userAvailableAdapter.setOnUSerClicked(ContactsFragment.this);
                        } else {
                            Toast.makeText(getActivity(), "No AISHA contacts found.", Toast.LENGTH_SHORT).show();
                            txtCircularContacts.setText("0");
                        }
                    } else {
                        Toast.makeText(getActivity(), "No AISHA contacts found.", Toast.LENGTH_SHORT).show();
                        txtCircularContacts.setText("0");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please try agaiin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (dilogAvailableUser.isShowing()) {
                    dilogAvailableUser.dismiss();
                }
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(AishaConstants.EXTRA_ERROR, t.getMessage());
                }
            }
        });
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

    @Override
    public void onUserCliked(View view, final int position) {
        final Dialog durationDilog = new Dialog(getActivity());
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
//                if (checkedId==R.id.rbtn_fifteen){
//                    durationTime = 15;
//                    rbtn_Fifteen.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_thirty){
//                    durationTime = 30;
//                    rbtn_thirty.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_fortyfive){
//                    durationTime = 45;
//                    rbtn_fortyFive.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_sixty){
//                    durationTime = 60;
//                    rbtn_Sixty.setChecked(true);
//                }
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
//                if (checkedId==R.id.rbtn_fifteen){
//                    durationTime = 15;
//                    rbtn_Fifteen.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_thirty){
//                    durationTime = 30;
//                    rbtn_thirty.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_fortyfive){
//                    durationTime = 45;
//                    rbtn_fortyFive.setChecked(true);
//                }
//                if (checkedId==R.id.rbtn_sixty){
//                    durationTime = 60;
//                    rbtn_Sixty.setChecked(true);
//                }
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
                        || rbtn_2hr.isChecked() || rbtn_3hr.isChecked() || rbtn_4hr.isChecked() || rbtn_5hr.isChecked()) {
                    requestLocation(durationTime, durationDilog, position);
                } else {
                    Toast.makeText(getActivity(), "Please select time duration", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocation(int durationTime, final Dialog durationDilog, int pos) {
        UserData userData = userAvilableList.get(pos);
        Map<String, String> mapSendLocation = new HashMap<>(3);
        mapSendLocation.put(AishaConstants.USERID, userId);
        mapSendLocation.put(AishaConstants.EXTRA_MOBILE_NUMBER, userData.getMobile());
        mapSendLocation.put(AishaConstants.EXTRA_DURATION, "" + durationTime);
        locationSharingResponseCall = webApi.sendLocationRequest(mapSendLocation);
        locationSharingResponseCall.enqueue(new Callback<LocationRequestResponse>() {
            @Override
            public void onResponse(Call<LocationRequestResponse> call, Response<LocationRequestResponse> response) {
                if (response.isSuccessful()) {
                    if (durationDilog.isShowing()) {
                        durationDilog.dismiss();
                    }
                    if (response.body().getStatus() == 1) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Please tryagain.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationRequestResponse> call, Throwable t) {
                if (durationDilog.isShowing()) {
                    durationDilog.dismiss();
                }
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), "Conection timeout", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("ERROR", t.getMessage());
                }
            }
        });
    }

    public class ContactTask extends AsyncTask<Void, Void, ArrayList<Contacts>> implements OnItemClickListner {
        private WeakReference<ContactsFragment> contactsActivityWeakReference;

        public ContactTask(ContactsFragment fragment) {
            contactsActivityWeakReference = new WeakReference<ContactsFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            if (contactsActivityWeakReference.get() != null) {
                if (contactses != null && contactses.size() > 0) {
                    contactAdapter = new ContactAdapter(getActivity(), contactses);
                    rcvContacts.setAdapter(contactAdapter);
                    contactAdapter.setOnItemclickListner(this);
                    contactsDataList.addAll(contactses);
                    if (AishaUtilities.isConnectingToInternet(getActivity())) {
                        showAvailableContacts();
                    }
                }
            }
        }

        @Override
        public void OnItemClick(View view, int pos) {
            Contacts contacts = contactsDataList.get(pos);
            Intent intent = new Intent(getActivity(), InvitationActivity.class);
            intent.putExtra(AishaConstants.EXTRA_INVITATION, contacts);
            startActivity(intent);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


}
