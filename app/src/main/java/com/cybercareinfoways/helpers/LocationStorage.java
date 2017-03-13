package com.cybercareinfoways.helpers;

import android.location.Location;

/**
 * Created by Nutan on 13-03-2017.
 */
public class LocationStorage {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static LocationStorage getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(LocationStorage ourInstance) {
        LocationStorage.ourInstance = ourInstance;
    }

    private static LocationStorage ourInstance = new LocationStorage();

    public static LocationStorage getInstance() {
        return ourInstance;
    }

    private LocationStorage() {
    }
}
