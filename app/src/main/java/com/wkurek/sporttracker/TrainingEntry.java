package com.wkurek.sporttracker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents record in "trainings" table in database.
 * It is used when inserting and fetching trainings.
 *
 * Class creates interface that simplify conversion between different data formats
 * which are used in database (JSON, PolyLine encoding) and JAVA data types.
 */

class TrainingEntry {
    private ArrayList<Geolocation> locations;
    private long startTimestamp, secondsNumber;
    private double distance;

    TrainingEntry(ArrayList<Geolocation> locations, long startTimestamp, long secondsNumber,
                         double distance) {
        this.startTimestamp = startTimestamp;
        this.secondsNumber = secondsNumber;
        this.distance = distance;
        this.locations = locations;
    }

    TrainingEntry(String json, long startTimestamp, long secondsNumber,
                         double distance) throws JSONException {
        this.startTimestamp = startTimestamp;
        this.secondsNumber = secondsNumber;
        this.distance = distance;
        this.locations = JSONLocationGenerator.generateLocationList(json);
    }

    long getStartTime() {
        return startTimestamp;
    }

    long getSecondsNumber() {
        return secondsNumber;
    }

    double getDistance() {
        return distance;
    }

    List<LatLng> getTrack() throws NullPointerException {
        if(locations.isEmpty()) throw new NullPointerException("Empty locations list.");

        List<LatLng> latLngList = new ArrayList<>();
        for(Geolocation location : locations) {
            latLngList.add(location.getLatLng());
        }
        return latLngList;
    }

    String getEncodedTrack() {
        return PolyUtil.encode(this.getTrack());
    }

    String getJSONLocations() {
        return JSONLocationGenerator.generateJSONArray(this.locations).toString();
    }

    ArrayList<Geolocation> getLocations() {
        return locations;
    }
}
