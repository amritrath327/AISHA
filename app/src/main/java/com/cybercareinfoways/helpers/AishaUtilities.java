package com.cybercareinfoways.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

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
}
