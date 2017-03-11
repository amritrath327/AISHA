package com.cybercareinfoways.aisha.services;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.TokenRegRequest;
import com.cybercareinfoways.webapihelpers.TokenRegResponse;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YELOWFLASH on 03/11/2017.
 */

public class SyncTokenService extends IntentService {

    public SyncTokenService() {
        super(SyncTokenService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("Token==>", token);
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String userID = getSharedPreferences(AishaConstants.USERPREFS, MODE_PRIVATE).getString(AishaConstants.USERID, "NA");
        if (!userID.equals("NA")) {
            TokenRegRequest request = new TokenRegRequest(userID, deviceId, token);
            WebApi retrofit = AishaUtilities.setupRetrofit();
            Call<TokenRegResponse> regResponseCall = retrofit.getRegToken(request);
            regResponseCall.enqueue(new Callback<TokenRegResponse>() {
                @Override
                public void onResponse(Call<TokenRegResponse> call, Response<TokenRegResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i(SyncTokenService.class.getName(), response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<TokenRegResponse> call, Throwable t) {

                }
            });

        }
    }
}
