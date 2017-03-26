package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 26-03-2017.
 */

public class ZipprListData implements Parcelable{
    private double latitude;
    private double longitude;
    private String address_name;
    private int address_type;
    private String address_line;
    private String plot_number;
    private String street_name;
    private String city;
    private String state;
    private String pincode;
    private String zipper_code;
    private int image_status;

    protected ZipprListData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        address_name = in.readString();
        address_type = in.readInt();
        address_line = in.readString();
        plot_number = in.readString();
        street_name = in.readString();
        city = in.readString();
        state = in.readString();
        pincode = in.readString();
        zipper_code = in.readString();
        image_status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address_name);
        dest.writeInt(address_type);
        dest.writeString(address_line);
        dest.writeString(plot_number);
        dest.writeString(street_name);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(pincode);
        dest.writeString(zipper_code);
        dest.writeInt(image_status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ZipprListData> CREATOR = new Creator<ZipprListData>() {
        @Override
        public ZipprListData createFromParcel(Parcel in) {
            return new ZipprListData(in);
        }

        @Override
        public ZipprListData[] newArray(int size) {
            return new ZipprListData[size];
        }
    };

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

    public int getAddress_type() {
        return address_type;
    }

    public void setAddress_type(int address_type) {
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

    public String getZipper_code() {
        return zipper_code;
    }

    public void setZipper_code(String zipper_code) {
        this.zipper_code = zipper_code;
    }

    public int getImage_status() {
        return image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }
}
