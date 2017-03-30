package com.cybercareinfoways.fcm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 26-03-2017.
 */

public class PushData  implements Parcelable{
    public PushData(String requestedFrom, long duration,String location_sharing_id) {
        this.requestedFrom = requestedFrom;
        this.duration = duration;
        this.location_sharing_id=location_sharing_id;
    }

    private String requestedFrom;

    public String getLocation_sharing_id() {
        return location_sharing_id;
    }

    public void setLocation_sharing_id(String location_sharing_id) {
        this.location_sharing_id = location_sharing_id;
    }

    private String location_sharing_id;

    protected PushData(Parcel in) {
        requestedFrom = in.readString();
        duration = in.readLong();
        location_sharing_id=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestedFrom);
        dest.writeLong(duration);
        dest.writeString(location_sharing_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushData> CREATOR = new Creator<PushData>() {
        @Override
        public PushData createFromParcel(Parcel in) {
            return new PushData(in);
        }

        @Override
        public PushData[] newArray(int size) {
            return new PushData[size];
        }
    };

    public String getRequestedFrom() {
        return requestedFrom;
    }

    public void setRequestedFrom(String requestedFrom) {
        this.requestedFrom = requestedFrom;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long duration;
}
