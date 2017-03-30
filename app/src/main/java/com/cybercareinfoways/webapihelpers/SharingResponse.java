package com.cybercareinfoways.webapihelpers;

import com.cybercareinfoways.aisha.model.SharedLocation;

import java.util.ArrayList;

/**
 * Created by Nutan on 31-03-2017.
 */

public class SharingResponse {
    private int status;
    private String message;
    private ArrayList<SharedLocation> location;

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

    public ArrayList<SharedLocation> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<SharedLocation> location) {
        this.location = location;
    }
}
