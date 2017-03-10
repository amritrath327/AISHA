package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

import retrofit2.http.Path;

/**
 * Created by Nutan on 01-03-2017.
 */

public class Contacts implements Parcelable{
    private  String contactimage;
    private String contactName;
    private String mobile;
    public Contacts(){
        super();
    }
    protected Contacts(Parcel in) {
        contactimage = in.readString();
        contactName = in.readString();
        mobile = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactimage);
        dest.writeString(contactName);
        dest.writeString(mobile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contacts> CREATOR = new Creator<Contacts>() {
        @Override
        public Contacts createFromParcel(Parcel in) {
            return new Contacts(in);
        }

        @Override
        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };

    public String getContactimage() {
        return contactimage;
    }

    public void setContactimage(String contactimage) {
        this.contactimage = contactimage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
