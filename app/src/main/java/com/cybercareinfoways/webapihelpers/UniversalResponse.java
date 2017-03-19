package com.cybercareinfoways.webapihelpers;


/**
 * Created by Nutan on 20-03-2017.
 */

public class UniversalResponse {
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
