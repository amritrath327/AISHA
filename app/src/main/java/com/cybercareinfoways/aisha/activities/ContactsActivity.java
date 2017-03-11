package com.cybercareinfoways.aisha.activities;

import android.Manifest;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
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
import com.cybercareinfoways.helpers.WebApi;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    private static final int READCONTACT_CODE = 201;
    private static final int REQUEST_ACESS_STORAGE=3;
    private static final int REQUEST_ACESS_CAMERA=2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    private ContactAdapter contactAdapter;
    private ArrayList<Contacts> cotactList, contactsDataList;
    private ArrayList<UserData> userAvilableList;
    private String appLinkUrl;
    private String userName;
    private Call<UserResponse> userResponseCall;
    private WebApi webApi;
    private String userId;
    private UserAvailableAdapter userAvailableAdapter;

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

    //https://fb.me/674228709423325
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
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
        rcvContacts.setLayoutManager(new GridLayoutManager(this,3));
        cotactList = new ArrayList();
        contactsDataList=new ArrayList<>();
        rcvAvailableUsers.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
        rcvAvailableUsers.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        userId=AishaUtilities.getSharedPreffUserid(ContactsActivity.this);
        webApi=AishaUtilities.setupRetrofit();
        userAvilableList = new ArrayList<>();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.READ_CONTACTS, this)) {
                getAllContacts();
            }else{
                AishaUtilities.requestPermission(this,new String[]{Manifest.permission.READ_CONTACTS},READCONTACT_CODE);
            }
        }else{
            getAllContacts();
        }
        imgWhastapp.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgMore.setOnClickListener(this);
        relUserBoard.setOnClickListener(this);
        framePic.setOnClickListener(this);
        userName = AishaUtilities.getSharedPreffName(ContactsActivity.this);
        TextDrawable textDrawable=new TextDrawable(userName,AishaUtilities.dpToPx(ContactsActivity.this,30));
        imgSmallUserpic.setImageDrawable(textDrawable);
        imgUserPic.setImageDrawable(textDrawable);
        txtSmallUsername.setText(userName);
        txtUserName.setText(userName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==READCONTACT_CODE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            getAllContacts();
        }
        if(requestCode==REQUEST_ACESS_STORAGE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            startDilog();
        }
        if(requestCode==REQUEST_ACESS_CAMERA && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openCameraApp();
        }
    }

    private void getAllContacts() {
        new ContactTask(ContactsActivity.this).execute();
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
                Toast.makeText(ContactsActivity.this,"Your device don't have Whastsapp.",Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId()==R.id.imgFacebook){
            String  previewImageUrl;

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
        if (v.getId()==R.id.imgMore){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is the text that will be shared.");
            startActivity(Intent.createChooser(sharingIntent,"Share using"));
        }
        if (v.getId()==R.id.relUserBoard){
                if (countLayout==1){
                    linUserNAme.setVisibility(View.VISIBLE);
                    imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_colap_arrow));
                    countLayout++;
                }else {
                    linUserNAme.setVisibility(View.GONE);
                    imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_exp_arrow));
                    countLayout--;
                }
        }
        if (v.getId()==R.id.framePic){
            handleCameraForPickingPhoto();
        }
    }

    private void handleCameraForPickingPhoto() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (AishaUtilities.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
                startDilog();
            }else{
                AishaUtilities.requestPermission(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACESS_STORAGE);
            }
        }else{
            startDilog();
        }
    }

    private void startDilog() {
        AlertDialog.Builder myAlertDilog = new AlertDialog.Builder(ContactsActivity.this);
        myAlertDilog.setTitle("Upload picture option..");
        myAlertDilog.setMessage("Where to upload picture????");
        myAlertDilog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent picIntent = new Intent(Intent.ACTION_GET_CONTENT,null);
                picIntent.setType("image/*");
                picIntent.putExtra("return_data",true);
                startActivityForResult(picIntent,GALLERY_REQUEST);
            }
        });
        myAlertDilog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(AishaUtilities.checkPermission(Manifest.permission.CAMERA,ContactsActivity.this)){
                        openCameraApp();
                    }else{
                        AishaUtilities.requestPermission(ContactsActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_ACESS_CAMERA);
                    }
                }else{
                    openCameraApp();
                }
            }
        });
        AlertDialog alertDialog = myAlertDilog.create();
        alertDialog.show();
    }

    private void openCameraApp() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(picIntent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    try {
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        options.inSampleSize =calculateInSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        imgUserPic.setImageBitmap(image);
                        imgSmallUserpic.setImageBitmap(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    uri = getImageUri(ContactsActivity.this, bitmap);
                    File finalFile = new File(getRealPathFromUri(uri));
                    imgUserPic.setImageBitmap(bitmap);
                    imgSmallUserpic.setImageBitmap(bitmap);
                } else if (data.getExtras() == null) {

                    Toast.makeText(getApplicationContext(),
                            "No extras to retrieve!", Toast.LENGTH_SHORT)
                            .show();

                    BitmapDrawable thumbnail = new BitmapDrawable(
                            getResources(), data.getData().getPath());
                    imgUserPic.setImageDrawable(thumbnail);
                    imgSmallUserpic.setImageDrawable(thumbnail);
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Uri getImageUri(ContactsActivity mainActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(mainActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public String getNameFromNumber(String mobile) {
         String contactName = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobile));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if(cursor != null && !cursor.isClosed()) {
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
            //contacts.setMobile("9668452233");
//            if (contactsDataList.get(i).getMobile().length()>0) {
//                contacts.setMobile(contactsDataList.get(i).getMobile().substring(contactsDataList.get(i).getMobile().length() - 10).toString());
//            }else {
            contacts.setMobile(contactsDataList.get(i).getMobile());
//            }
            contactses.add(contacts);
        }
        //}
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
                            userAvailableAdapter = new UserAvailableAdapter(ContactsActivity.this, userAvilableList);
                            rcvAvailableUsers.setAdapter(userAvailableAdapter);
                        } else {
                            Toast.makeText(ContactsActivity.this, "No AISHA contacts found.", Toast.LENGTH_SHORT).show();
                            txtCircularContacts.setText("0");
                        }
                    } else {
                        Toast.makeText(ContactsActivity.this, "No AISHA contacts found.", Toast.LENGTH_SHORT).show();
                        txtCircularContacts.setText("0");
                    }
                } else {
                    Toast.makeText(ContactsActivity.this, "Please try agaiin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (dilogAvailableUser.isShowing()) {
                    dilogAvailableUser.dismiss();
                }
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(ContactsActivity.this, AishaConstants.CONNECYION_TIME_OUT, Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(AishaConstants.EXTRA_ERROR, t.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        if (dilogAvailableUser != null || dilogAvailableUser.isShowing()) {
            dilogAvailableUser.cancel();
        }
        if (userResponseCall != null) {
            userResponseCall.cancel();
        }
        super.onStop();
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

    public class ContactTask extends AsyncTask<Void,Void,ArrayList<Contacts>> implements OnItemClickListner {
        private WeakReference<ContactsActivity> contactsFragmentReference;
        public ContactTask(ContactsActivity contactsActivity){
            contactsFragmentReference = new WeakReference<ContactsActivity>(contactsActivity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dilogAvailableUser = new ProgressDialog(ContactsActivity.this);
            dilogAvailableUser.setMessage("Please wait...Showing Your Contacts in AISHA");
            dilogAvailableUser.setCanceledOnTouchOutside(false);
            dilogAvailableUser.show();
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... params) {
            Contacts contacts;
            ContentResolver contentResolver = getContentResolver();
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
            return cotactList.size()>0?cotactList:null;
        }

        @Override
        protected void onPostExecute(ArrayList<Contacts> contactses) {
            super.onPostExecute(contactses);
            if (contactsFragmentReference.get() != null) {
                if (contactses != null && contactses.size() > 0) {
                    contactAdapter = new ContactAdapter(ContactsActivity.this, contactses);
                    rcvContacts.setAdapter(contactAdapter);
                    contactAdapter.setOnItemclickListner(this);
                    contactsDataList.addAll(contactses);
                    if (AishaUtilities.isConnectingToInternet(ContactsActivity.this)) {
                        showAvailableContacts();
                    }
                }
            }
        }

        @Override
        public void OnItemClick(View view, int pos) {
            Contacts contacts = contactsDataList.get(pos);
            Intent intent = new Intent(ContactsActivity.this,InvitationActivity.class);
            intent.putExtra(AishaConstants.EXTRA_INVITATION,contacts);
            startActivity(intent);
        }
    }
}
