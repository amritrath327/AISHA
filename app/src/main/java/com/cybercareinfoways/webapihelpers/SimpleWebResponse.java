package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/15/2017.
 */

public class SimpleWebResponse {
    int status;
    String message;

    public SimpleWebResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

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
