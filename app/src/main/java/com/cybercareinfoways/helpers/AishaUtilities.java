package com.cybercareinfoways.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nutan on 01-03-2017.
 */

public class AishaUtilities {
    public static boolean checkPermission(String permission, Context context) {
        int statusCode = ContextCompat.checkSelfPermission(context, permission);
        return statusCode == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(AppCompatActivity activity, String[] permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            Toast.makeText(activity, "Application need permission", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    public static void requestPermission(Fragment fragment, String[] permission, int requestCode) {
        fragment.requestPermissions(permission, requestCode);
    }
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    public static void setSharedPreffMobile(Context context,String mobileNo){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AishaConstants.EXTRA_MOBILE,mobileNo);
        editor.commit();
    }
    public static String  getSharedPreffMobile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        String mobileNo = sharedPreferences.getString(AishaConstants.EXTRA_MOBILE,"");
        return mobileNo;
    }
    public static void setSharedPreffUserID(Context context,String userId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AishaConstants.EXTRA_USERID,userId);
        editor.commit();
    }
    public static String getSharedPreffUserid(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(AishaConstants.EXTRA_USERID,"");
        return userId;
    }
    public static void setSharedPreffUserNAme(Context context,String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AishaConstants.EXTRA_NAME,name);
        editor.commit();
    }
    public static String getSharedPreffName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(AishaConstants.EXTRA_PREFF,Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(AishaConstants.EXTRA_NAME,"");
        return name;
    }
    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static JSONArray readJsonFromRaw(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.country_code);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONArray jsonArray = new JSONArray(
                    byteArrayOutputStream.toString());
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject countryObject = jsonArray.getJSONObject(i);
//                Country c = new Country(countryObject.getString(AppConstants.COUNTRYNAME), countryObject.getString(AppConstants.COUNTRYCODE));
//                list.add(c);
//            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WebApi setupRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AishaConstants.BASEURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getSocketTime())
                .build();
        return retrofit.create(WebApi.class);
    }

    public static OkHttpClient getSocketTime() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
               // .addInterceptor(interceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return client;
    }

    public static ProgressDialog showProgressDialog(Context context, String genotpmessage) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(AishaConstants.DALOGTITLE);
        dialog.setMessage(genotpmessage);
        return dialog;
    }


    public static String convertToString(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
