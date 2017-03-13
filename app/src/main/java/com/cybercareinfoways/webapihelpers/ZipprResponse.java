package com.cybercareinfoways.webapihelpers;

/**
 * Created by Nutan on 13-03-2017.
 */

public class ZipprResponse {
    private int status;
    private String zipper_code;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getZipper_code() {
        return zipper_code;
    }

    public void setZipper_code(String zipper_code) {
        this.zipper_code = zipper_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
