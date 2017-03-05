package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/03/2017.
 */

public class GenOtpResponse {
    int status, verified;
    String user_id;
    String mobile_number;
    int otp;

    public GenOtpResponse(int status, int verified, String user_id, String mobile_number, int otp) {
        this.status = status;
        this.verified = verified;
        this.user_id = user_id;
        this.mobile_number = mobile_number;
        this.otp = otp;
    }

    public int getStatus() {
        return status;
    }

    public int getVerified() {
        return verified;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public int getOtp() {
        return otp;
    }
}
