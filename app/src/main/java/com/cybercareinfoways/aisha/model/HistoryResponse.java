package com.cybercareinfoways.aisha.model;

import java.util.ArrayList;

/**
 * Created by amritrath on 25/04/17.
 */

public class HistoryResponse {
    int status;
    String message;
    ArrayList<HistoryData> history;

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

    public ArrayList<HistoryData> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<HistoryData> history) {
        this.history = history;
    }

    public class HistoryData {
        String location_sharing_id;
        String mobile_number;
        double time_left;
        int image_status;
        double user_latitude, user_longitude;
        int meetingpoint_status;
        double meetingpoint_longitude, meetingpoint_latitude;


        public String getLocation_sharing_id() {
            return location_sharing_id;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public double getTime_left() {
            return time_left;
        }

        public int getImage_status() {
            return image_status;
        }

        public double getUser_latitude() {
            return user_latitude;
        }

        public double getUser_longitude() {
            return user_longitude;
        }

        public int getMeetingpoint_status() {
            return meetingpoint_status;
        }

        public double getMeetingpoint_longitude() {
            return meetingpoint_longitude;
        }

        public double getMeetingpoint_latitude() {
            return meetingpoint_latitude;
        }
    }
}

