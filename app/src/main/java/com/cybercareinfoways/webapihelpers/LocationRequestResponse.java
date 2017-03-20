package com.cybercareinfoways.webapihelpers;


/**
 * Created by Nutan on 20-03-2017.
 */

public class LocationRequestResponse {
    private int status;
    private String message;

    public String getLocaton_sharing_id() {
        return locaton_sharing_id;
    }

    public void setLocaton_sharing_id(String locaton_sharing_id) {
        this.locaton_sharing_id = locaton_sharing_id;
    }

    private String locaton_sharing_id;

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
