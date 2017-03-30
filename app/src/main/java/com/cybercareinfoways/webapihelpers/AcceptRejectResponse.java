package com.cybercareinfoways.webapihelpers;

/**
 * Created by Nutan on 31-03-2017.
 */

public class AcceptRejectResponse {
    private int status;
    private String mobile_number;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
