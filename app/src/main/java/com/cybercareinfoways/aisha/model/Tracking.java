package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 31-03-2017.
 */

public class Tracking implements Parcelable{
    private double latitude;
    private double longitude;
    private String date;

    protected Tracking(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tracking> CREATOR = new Creator<Tracking>() {
        @Override
        public Tracking createFromParcel(Parcel in) {
            return new Tracking(in);
        }

        @Override
        public Tracking[] newArray(int size) {
            return new Tracking[size];
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
