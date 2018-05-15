package com.wkurek.sporttracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Main purpose of this class is to generate JSON notation of Location object Container.
 * JSON representation of collection is stored in database.
 */

class JSONLocationGenerator {
    private static final String TIME_KEY = "time";
    private static final String VELOCITY_KEY = "velocity";
    private static final String ALTITUDE_KEY = "altitude";
    private static final String LAT_KEY = "lat";
    private static final String LNG_KEY = "lng";

    /**
     * Method convert single Geolocation object to JSONObject.
     * @param location object to be converted to JSONObject
     * @return JSONObject representing passed Geolocation object
     */
    private static JSONObject generateJSONObject(Geolocation location) {
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

    /**
     * Method converts List of Geolocation object to JSONArray which is suitable to be stored in database.
     * @param locations list of Geolocation objects
     * @return JSONArray representing list of Geolocation objects
     */
    static JSONArray generateJSONArray(List<Geolocation> locations) {
        if(locations  == null || locations.isEmpty()) return null;
        JSONArray jsonArray = new JSONArray();

        for(Geolocation location : locations) {
            jsonArray.put(generateJSONObject(location));
        }

        return jsonArray;
    }

    /**
     * Generates List of Geolocation objects basing on json literal.
     * Reverse operation to {@link #generateJSONArray(List)}
     * @param json string data in JSON format representing list of Geolocation objects
     * @return List of Geolocation objects based on json literal
     * @throws org.json.JSONException
     */
    static ArrayList<Geolocation> generateLocationList(String json) throws org.json.JSONException {
        if(json.isEmpty()) throw new org.json.JSONException("Empty JSON string.");
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Geolocation> locations = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Geolocation location = new Geolocation(jsonObject.getDouble(LAT_KEY),
                    jsonObject.getDouble(LNG_KEY), jsonObject.getDouble(ALTITUDE_KEY),
                    jsonObject.getDouble(VELOCITY_KEY), jsonObject.getLong(TIME_KEY));

            locations.add(location);
        }

        return locations;
    }

}
