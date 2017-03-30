package com.cybercareinfoways.aisha.model;

/**
 * Created by Nutan on 31-03-2017.
 */

public class User {
    private String dial_code;
    private String mobile_number;
    private String name;
    private String otp;
    private int verified;
    private String date;
    private String image;

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
