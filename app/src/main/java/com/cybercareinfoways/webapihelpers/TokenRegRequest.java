package com.cybercareinfoways.webapihelpers;

/**
 * Created by YELOWFLASH on 03/11/2017.
 */

public class TokenRegRequest {
    String user_id, deviceId, registrationId;

    public TokenRegRequest(String user_id, String deviceId, String registrationId) {
        this.user_id = user_id;
        this.deviceId = deviceId;
        this.registrationId = registrationId;
    }
}
