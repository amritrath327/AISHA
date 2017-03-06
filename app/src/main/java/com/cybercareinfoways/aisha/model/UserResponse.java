package com.cybercareinfoways.aisha.model;

import java.util.ArrayList;

/**
 * Created by Nutan on 06-03-2017.
 */

public class UserResponse {
    private int status;
    private String message;
    private ArrayList<UserData> contacts;

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

    public ArrayList<UserData> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<UserData> contacts) {
        this.contacts = contacts;
    }
}
