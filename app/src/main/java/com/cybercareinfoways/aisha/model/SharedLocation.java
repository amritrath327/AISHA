package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Nutan on 31-03-2017.
 */

public class SharedLocation implements Parcelable{
    private String location_sharing_id;
    private User user_id;
    private ArrayList<Tracking> track;


    protected SharedLocation(Parcel in) {
        location_sharing_id = in.readString();
        track = in.createTypedArrayList(Tracking.CREATOR);
    }

    public static final Creator<SharedLocation> CREATOR = new Creator<SharedLocation>() {
        @Override
        public SharedLocation createFromParcel(Parcel in) {
            return new SharedLocation(in);
        }

        @Override
        public SharedLocation[] newArray(int size) {
            return new SharedLocation[size];
        }
    };

    public String getLocation_sharing_id() {
        return location_sharing_id;
    }

    public void setLocation_sharing_id(String location_sharing_id) {
        this.location_sharing_id = location_sharing_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public ArrayList<Tracking> getTrack() {
        return track;
    }

    public void setTrack(ArrayList<Tracking> track) {
        this.track = track;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location_sharing_id);
        dest.writeTypedList(track);
    }
}
