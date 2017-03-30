package com.cybercareinfoways.aisha.model;

/**
 * Created by Nutan on 31-03-2017.
 */

public class Tracking {
    private double latitude;
    private double longitude;
    private String date;

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
