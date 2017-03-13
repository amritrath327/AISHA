package com.cybercareinfoways.aisha.activities;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.Zippr;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.LocationStorage;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.ZipprResponse;

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
        zippr.setAddress_name("Patia");
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
        zippr.setImage("");
        zipprResponseCall=zipprApi.setZipprLocation(zippr);
        zipprResponseCall.enqueue(new Callback<ZipprResponse>() {
            @Override
            public void onResponse(Call<ZipprResponse> call, Response<ZipprResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus()==1){
                        final Dialog dialog = new Dialog(ZipprActivity.this);
                        dialog.setContentView(R.layout.zippr_code_layout);
                        dialog.setCanceledOnTouchOutside(false);
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
