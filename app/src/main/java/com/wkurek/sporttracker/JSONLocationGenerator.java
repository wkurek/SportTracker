package com.wkurek.sporttracker;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Main purpose of this class is to generate JSON notation of Location object Container.
 * JSON representation of collection is stored in database.
 */

class JSONLocationGenerator {
    static final String TIME_KEY = "time";
    static final String VELOCITY_KEY = "velocity";
    static final String ALTITUDE_KEY = "altitude";
    static final String LAT_KEY = "lat";
    static final String LNG_KEY = "lng";

    private static JSONObject generateJSONObject(Location location) {
        JSONObject object = new JSONObject();
        try {
            object.put(TIME_KEY, location.getTime());
            object.put(VELOCITY_KEY, location.getSpeed());
            object.put(ALTITUDE_KEY, location.getAltitude());
            object.put(LAT_KEY, location.getLatitude());
            object.put(LNG_KEY, location.getLongitude());
        } catch (JSONException e) {
            return null;
        }
        return object;
    }

    static JSONArray generateJSONArray(List<Location> locations) {
        if(locations  == null || locations.isEmpty()) return null;
        JSONArray jsonArray = new JSONArray();

        for(Location location : locations) {
            jsonArray.put(generateJSONObject(location));
        }

        return jsonArray;
    }

}
