package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/05/2017.
 */

public class VerifyOtpResponse {
    int status, verified;
    String user_id, message;

    public VerifyOtpResponse(int status, int verified, String user_id, String message) {
        this.status = status;
        this.verified = verified;
        this.user_id = user_id;
        this.message = message;
    }

    public String getMessage() {
        return message;
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
}
