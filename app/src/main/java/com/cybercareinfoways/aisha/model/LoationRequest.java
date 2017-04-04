package com.cybercareinfoways.aisha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nutan on 24-03-2017.
 */

public class LoationRequest implements Parcelable{
    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    public String getLocation_sharing_id() {
        return location_sharing_id;
    }

    public void setLocation_sharing_id(String location_sharing_id) {
        this.location_sharing_id = location_sharing_id;
    }
    private boolean isAccepted;
    public void setRequestAccepted(boolean accepted){
        this.isAccepted=accepted;
    }

    public boolean getIsAccpted(){
        return isAccepted;
    }

    public LoationRequest(String requestFrom, long duration, String location_sharing_id) {
        this.requestFrom = requestFrom;
        this.duration = duration;
        this.location_sharing_id=location_sharing_id;

    }

    public String requestFrom,location_sharing_id;
    public long duration;

    protected LoationRequest(Parcel in) {
        requestFrom = in.readString();
        duration = in.readLong();
        location_sharing_id=in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestFrom);
        dest.writeLong(duration);
        dest.writeString(location_sharing_id);
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
