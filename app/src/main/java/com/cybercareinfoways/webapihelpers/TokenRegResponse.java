package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/11/2017.
 */

public class TokenRegResponse {
    int status;
    String message;

    public TokenRegResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
