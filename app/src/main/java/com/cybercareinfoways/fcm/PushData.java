package com.cybercareinfoways.fcm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 26-03-2017.
 */

public class PushData  implements Parcelable{
    public PushData(String requestedFrom, long duration) {
        this.requestedFrom = requestedFrom;
        this.duration = duration;
    }

    private String requestedFrom;

    protected PushData(Parcel in) {
        requestedFrom = in.readString();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestedFrom);
        dest.writeLong(duration);
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
