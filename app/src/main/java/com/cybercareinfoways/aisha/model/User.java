package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 31-03-2017.
 */

public class User implements Parcelable{
    private String dial_code;
    private String mobile_number;
    private String name;
    private String otp;
    private int verified;
    private String date;
    private String image;

    protected User(Parcel in) {
        dial_code = in.readString();
        mobile_number = in.readString();
        name = in.readString();
        otp = in.readString();
        verified = in.readInt();
        date = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dial_code);
        dest.writeString(mobile_number);
        dest.writeString(name);
        dest.writeString(otp);
        dest.writeInt(verified);
        dest.writeString(date);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
