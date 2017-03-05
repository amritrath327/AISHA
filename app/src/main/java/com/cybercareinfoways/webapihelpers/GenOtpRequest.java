package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/03/2017.
 */

public class GenOtpRequest {
    String mobile_number, dial_code, name;

    public GenOtpRequest(String mobile_number, String dial_code, String name) {
        this.mobile_number = mobile_number;
        this.dial_code = dial_code;
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GenOtpRequest{" +
                "mobile_number='" + mobile_number + '\'' +
                ", dial_code='" + dial_code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
