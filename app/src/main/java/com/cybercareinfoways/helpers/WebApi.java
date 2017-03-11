package com.cybercareinfoways.helpers;

import com.cybercareinfoways.aisha.model.UserRequest;
import com.cybercareinfoways.aisha.model.UserResponse;
import com.cybercareinfoways.webapihelpers.GenOtpRequest;
import com.cybercareinfoways.webapihelpers.GenOtpResponse;
import com.cybercareinfoways.webapihelpers.ProfileResponse;
import com.cybercareinfoways.webapihelpers.SimpleWebRequest;
import com.cybercareinfoways.webapihelpers.TokenRegRequest;
import com.cybercareinfoways.webapihelpers.TokenRegResponse;
import com.cybercareinfoways.webapihelpers.VerifyOtpRequest;
import com.cybercareinfoways.webapihelpers.VerifyOtpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by YELOWFLASH on 03/03/2017.
 */

public interface WebApi {
    @POST("user")
    Call<GenOtpResponse> getGenOtpResponseCall(@Body GenOtpRequest request);

    @POST("user/verify")
    Call<VerifyOtpResponse> getVerifyOtpResponseCall(@Body VerifyOtpRequest request);

    @POST("contact")
    Call<UserResponse> getAvailableUser(@Body UserRequest userRequest);

    @POST("device")
    Call<TokenRegResponse> getRegToken(@Body TokenRegRequest regRequest);

    @POST("profile")
    Call<ProfileResponse> getProfile(@Body SimpleWebRequest request);
}
