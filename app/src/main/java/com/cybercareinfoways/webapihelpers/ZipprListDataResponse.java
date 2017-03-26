package com.cybercareinfoways.webapihelpers;

import com.cybercareinfoways.aisha.model.ZipprListData;

import java.util.ArrayList;

/**
 * Created by Nutan on 26-03-2017.
 */

public class ZipprListDataResponse {
    private ArrayList<ZipprListData> list_zipper;
    private int status;
    private String message;

    public ArrayList<ZipprListData> getList_zipper() {
        return list_zipper;
    }

    public void setList_zipper(ArrayList<ZipprListData> list_zipper) {
        this.list_zipper = list_zipper;
    }

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
}
