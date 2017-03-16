package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 16-03-2017.
 */

public class ZipprCodeResponse implements Parcelable{
    private String message;
    private double latitude;
    private double longitude;
    private String address_name;
    private String address_type;
    private String address_line;
    private String plot_number;
    private String street_name;
    private String city;
    private String state;
    private String pincode;
    private int image_status;
    private String image_url;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public ZipprCodeResponse(){
        super();
    }

    protected ZipprCodeResponse(Parcel in) {
        message = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address_name = in.readString();
        address_type = in.readString();
        address_line = in.readString();
        plot_number = in.readString();
        street_name = in.readString();
        city = in.readString();
        state = in.readString();
        pincode = in.readString();
        image_status = in.readInt();
        image_url = in.readString();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address_name);
        dest.writeString(address_type);
        dest.writeString(address_line);
        dest.writeString(plot_number);
        dest.writeString(street_name);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(pincode);
        dest.writeInt(image_status);
        dest.writeString(image_url);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ZipprCodeResponse> CREATOR = new Creator<ZipprCodeResponse>() {
        @Override
        public ZipprCodeResponse createFromParcel(Parcel in) {
            return new ZipprCodeResponse(in);
        }

        @Override
        public ZipprCodeResponse[] newArray(int size) {
            return new ZipprCodeResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getAddress_line() {
        return address_line;
    }

    public void setAddress_line(String address_line) {
        this.address_line = address_line;
    }

    public String getPlot_number() {
        return plot_number;
    }

    public void setPlot_number(String plot_number) {
        this.plot_number = plot_number;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getImage_status() {
        return image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
