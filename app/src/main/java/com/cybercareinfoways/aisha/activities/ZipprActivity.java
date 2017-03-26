package com.cybercareinfoways.aisha.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.Zippr;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.LocationStorage;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.ZipprResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZipprActivity extends AppCompatActivity {
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    @BindView(R.id.etPlaceName)
    EditText etPlaceName;
    @BindView(R.id.etHnoPlotNo)
    EditText etHnoPlotNo;
    @BindView(R.id.etStreetName)
    EditText etStreetName;
    @BindView(R.id.etCity)
    EditText etCity;
    @BindView(R.id.etState)
    EditText etState;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.etPincode)
    EditText etPincode;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Geocoder geocoder;
    List<Address> addresses;
    private String etPlaceNmaeString,etHnoPlotNoString, etStreetNameString, etCityString, etStateString,userId,addressString,etPincodeString;
    Call<ZipprResponse> zipprResponseCall;
    WebApi zipprApi;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_ACESS_STORAGE=3;
    private static final int REQUEST_ACESS_CAMERA=2;
    private Uri uri;
    @BindView(R.id.imgAddedImage)
    ImageView imgAddedImage;
    private String baseCodeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zippr);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = AishaUtilities.getSharedPreffUserid(ZipprActivity.this);
        zipprApi = AishaUtilities.setupRetrofit();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(LocationStorage.getInstance().getLocation().getLatitude(), LocationStorage.getInstance().getLocation().getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException n){
            Toast.makeText(this, "Plrase try again.", Toast.LENGTH_SHORT).show();
        }
        if (addresses!=null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            txtAddress.setText(address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
        }else {
            Toast.makeText(this, "Please Restart the Device and free some space", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPlaceNmaeString = etPlaceName.getText().toString().trim();
                etHnoPlotNoString = etHnoPlotNo.getText().toString().trim();
                etStreetNameString = etStreetName.getText().toString().trim();
                etCityString = etCity.getText().toString().trim();
                etStateString = etState.getText().toString().trim();
                etPincodeString=etPincode.getText().toString().trim().trim();
                addressString = txtAddress.getText().toString();
                if (AishaUtilities.isConnectingToInternet(ZipprActivity.this)){
                    sendZipprLocation(etPlaceNmaeString,etHnoPlotNoString, etStreetNameString, etCityString, etStateString,etPincodeString,addressString);
                }else {
                    Toast.makeText(ZipprActivity.this, "Please chekc network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendZipprLocation(String etPlaceNmaeString, String etHnoPlotNoString, String etStreetNameString, String etCityString, String etStateString,String etPincodeString,String addressString) {
        /*Map<String,String> mapZippr = new HashMap<>(10);
        mapZippr.put("user_id",userId);
        mapZippr.put("latitude",""+LocationStorage.getInstance().getLocation().getLatitude());
        mapZippr.put("longitude",""+LocationStorage.getInstance().getLocation().getLongitude());
        mapZippr.put("address_name","");
        if (!TextUtils.isEmpty(etPlaceNmaeString) && !TextUtils.isEmpty(etHnoPlotNoString) && !TextUtils.isEmpty(etStreetNameString) && !TextUtils.isEmpty(etCityString) && !TextUtils.isEmpty(etStateString)){
            mapZippr.put("address_type",""+1);
        }else {
            mapZippr.put("address_type",""+2);
        }
        mapZippr.put("address_line",addressString);
        mapZippr.put("plot_number",etHnoPlotNoString);
        mapZippr.put("city",etPlaceNmaeString);
        mapZippr.put("state",etStreetNameString);
        mapZippr.put("pincode","");
        mapZippr.put("image","");*/
        Zippr zippr = new Zippr();
        zippr.setUser_id(userId);
        zippr.setLatitude(LocationStorage.getInstance().getLocation().getLatitude());
        zippr.setLongitude(LocationStorage.getInstance().getLocation().getLongitude());
        zippr.setAddress_name("");
        if (!TextUtils.isEmpty(etPlaceNmaeString) && !TextUtils.isEmpty(etHnoPlotNoString) && !TextUtils.isEmpty(etStreetNameString) && !TextUtils.isEmpty(etCityString) && !TextUtils.isEmpty(etStateString) && !TextUtils.isEmpty(etPincodeString)){
            zippr.setAddress_type(2);
        }else {
            zippr.setAddress_type(1);
        }
        zippr.setAddress_line(addressString);
        zippr.setPlot_number(etHnoPlotNoString);
        zippr.setStreet_name(etStreetNameString);
        zippr.setCity(etCityString);
        zippr.setState(etStateString);
        zippr.setPincode(etPincodeString);
        zippr.setImage(baseCodeImage);
        zipprResponseCall=zipprApi.setZipprLocation(zippr);
        zipprResponseCall.enqueue(new Callback<ZipprResponse>() {
            @Override
            public void onResponse(Call<ZipprResponse> call, Response<ZipprResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        final Dialog dialog = new Dialog(ZipprActivity.this);
                        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
                        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
                        params.width=ViewGroup.LayoutParams.MATCH_PARENT;
                        dialog.getWindow().setAttributes(params);
                        dialog.setContentView(R.layout.zippr_code_layout);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        TextView txtZipprCode = (TextView)dialog.findViewById(R.id.txtZipprCode);
                        Button btnOkay = (Button)dialog.findViewById(R.id.btnOkay);
                        txtZipprCode.setText(response.body().getZipper_code());
                        btnOkay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }else {
                        Toast.makeText(ZipprActivity.this, "Error in zippr code", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ZipprResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException){
                    Toast.makeText(ZipprActivity.this, "Connection timeout, please tryagain", Toast.LENGTH_SHORT).show();
                }else {
                    Log.v("Error",t.getMessage());
                }
            }
        });
    }
    private void handleCamera(){
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
        AlertDialog.Builder myAlertDilog = new AlertDialog.Builder(ZipprActivity.this);
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
                    if(AishaUtilities.checkPermission(Manifest.permission.CAMERA,ZipprActivity.this)){
                        openCameraApp();
                    }else{
                        AishaUtilities.requestPermission(ZipprActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_ACESS_CAMERA);
                    }
                }else{
                    openCameraApp();
                }
            }
        });
        myAlertDilog.show();
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
                        imgAddedImage.setImageBitmap(image);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        baseCodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
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
                    uri = getImageUri(ZipprActivity.this, bitmap);
                    File finalFile = new File(getRealPathFromUri(uri));
                    imgAddedImage.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    baseCodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                } else if (data.getExtras() == null) {

                    Toast.makeText(getApplicationContext(),
                            "No extras to retrieve!", Toast.LENGTH_SHORT)
                            .show();

                    BitmapDrawable thumbnail = new BitmapDrawable(
                            getResources(), data.getData().getPath());
                    imgAddedImage.setImageDrawable(thumbnail);

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
    private Uri getImageUri(ZipprActivity zipprActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(zipprActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_ACESS_STORAGE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            startDilog();
        }
        if(requestCode==REQUEST_ACESS_CAMERA && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openCameraApp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.camera_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        if (item.getItemId()==R.id.item_camera){
            handleCamera();
            return true;
        }
        return false;
    }
}
