package com.cybercareinfoways.aisha.model;

import java.util.ArrayList;

/**
 * Created by Nutan on 06-03-2017.
 */

public class UserRequest {
    private String user_id;
    private ArrayList<Contacts> contacts;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contacts> contacts) {
        this.contacts = contacts;
    }
}
