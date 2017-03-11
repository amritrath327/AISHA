package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/12/2017.
 */

public class SimpleWebRequest {
    String user_id;

    public SimpleWebRequest(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
