package com.cybercareinfoways.helpers;

import com.cybercareinfoways.aisha.model.HistoryResponse;
import com.cybercareinfoways.aisha.model.ZipprCodeResponse;
import com.cybercareinfoways.aisha.model.UserRequest;
import com.cybercareinfoways.aisha.model.UserResponse;
import com.cybercareinfoways.aisha.model.Zippr;
import com.cybercareinfoways.webapihelpers.AcceptRejectResponse;
import com.cybercareinfoways.webapihelpers.GenOtpRequest;
import com.cybercareinfoways.webapihelpers.GenOtpResponse;
import com.cybercareinfoways.webapihelpers.ProfileResponse;
import com.cybercareinfoways.webapihelpers.Result;
import com.cybercareinfoways.webapihelpers.SharingResponse;
import com.cybercareinfoways.webapihelpers.SimpleWebRequest;
import com.cybercareinfoways.webapihelpers.SimpleWebResponse;
import com.cybercareinfoways.webapihelpers.TokenRegRequest;
import com.cybercareinfoways.webapihelpers.TokenRegResponse;
import com.cybercareinfoways.webapihelpers.LocationRequestResponse;
import com.cybercareinfoways.webapihelpers.UpdateProfileRequest;
import com.cybercareinfoways.webapihelpers.VerifyOtpRequest;
import com.cybercareinfoways.webapihelpers.VerifyOtpResponse;
import com.cybercareinfoways.webapihelpers.ZipprListDataResponse;
import com.cybercareinfoways.webapihelpers.ZipprResponse;

import java.util.Map;

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

    @POST("profile_update")
    Call<SimpleWebResponse> updateProfile(@Body UpdateProfileRequest request);

    @POST("zipper")
    Call<ZipprResponse> setZipprLocation(@Body Zippr zippr);
    //Call<ZipprResponse> setZipprLocation(@Body Map<String,String> object);
    @POST("search_zipper")
    Call<ZipprCodeResponse> getAddressFromCode(@Body Map<String,String> object);
    @POST("location_request")
    Call<LocationRequestResponse> sendLocationRequest(@Body Map<String,String> object);
    @POST("zipper_list")
    Call<ZipprListDataResponse> getAllZipprCode(@Body Map<String,String> object);
    @POST("location_request_action")
    Call<AcceptRejectResponse> acceptORreject(@Body Map<String,String> object);
    @POST("location_sharing")
    Call<SharingResponse> getUpadatedLocationFromervice(@Body Map<String,String> object);
    @POST("meeting_point")
    Call<Result> sendMeetingPoint(@Body Map<String,String> object);

    @POST("history")
    Call<HistoryResponse> getHiostry(@Body SimpleWebRequest simpleWebRequest);
}
