package com.cybercareinfoways.helpers;

import android.content.Context;
import android.util.Log;

import com.cybercareinfoways.aisha.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by YELOWFLASH on 02/28/2017.
 */

public class AppHelper {
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
