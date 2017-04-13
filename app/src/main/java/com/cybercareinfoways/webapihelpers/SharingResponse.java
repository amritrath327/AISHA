package com.cybercareinfoways.webapihelpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybercareinfoways.aisha.model.SharedLocation;

import java.util.ArrayList;

/**
 * Created by Nutan on 31-03-2017.
 */

public class SharingResponse implements Parcelable{
    private int status;
    private String message;
    private ArrayList<SharedLocation> location;

    protected SharingResponse(Parcel in) {
        status = in.readInt();
        message = in.readString();
        location = in.createTypedArrayList(SharedLocation.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(message);
        dest.writeTypedList(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SharingResponse> CREATOR = new Creator<SharingResponse>() {
        @Override
        public SharingResponse createFromParcel(Parcel in) {
            return new SharingResponse(in);
        }

        @Override
        public SharingResponse[] newArray(int size) {
            return new SharingResponse[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<SharedLocation> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<SharedLocation> location) {
        this.location = location;
    }
}
