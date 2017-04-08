package com.cybercareinfoways.helpers;

import android.location.Location;

/**
 * Created by Nutan on 09-04-2017.
 */

public class StoreCurrentLocation {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static StoreCurrentLocation getOurInstance() {
        return ourInstance;
    }

    private static final StoreCurrentLocation ourInstance = new StoreCurrentLocation();

    public static StoreCurrentLocation getInstance() {
        return ourInstance;
    }

    private StoreCurrentLocation() {
    }
}
