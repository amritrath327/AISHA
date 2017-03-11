package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/12/2017.
 */

public class ProfileResponse {
    int status, image_status;

    String message, image_url, name;

    public ProfileResponse(int status, int image_status, String message, String image_url, String name) {
        this.status = status;
        this.image_status = image_status;
        this.message = message;
        this.image_url = image_url;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getImage_status() {
        return image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
