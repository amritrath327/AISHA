package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 24-03-2017.
 */

public class LoationRequest implements Parcelable{
    public LoationRequest(String requestFrom, long duration) {
        this.requestFrom = requestFrom;
        this.duration = duration;
    }

    public String requestFrom;
    public long duration;

    protected LoationRequest(Parcel in) {
        requestFrom = in.readString();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestFrom);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoationRequest> CREATOR = new Creator<LoationRequest>() {
        @Override
        public LoationRequest createFromParcel(Parcel in) {
            return new LoationRequest(in);
        }

        @Override
        public LoationRequest[] newArray(int size) {
            return new LoationRequest[size];
        }
    };

    public String getRequestFrom() {
        return requestFrom;
    }

    public long getDuration() {
        return duration;
    }
}
