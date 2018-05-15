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

    /**
     * Generates training track in form of LatLng object List
     * @return List of LatLng objects representing geographical points
     * @throws NullPointerException
     */
    List<LatLng> getTrack() throws NullPointerException {
        if(locations.isEmpty()) throw new NullPointerException("Empty locations list.");

        List<LatLng> latLngList = new ArrayList<>();
        for(Geolocation location : locations) {
            latLngList.add(location.getLatLng());
        }
        return latLngList;
    }

    /**
     * Encodes training track to format suitable to be stored in database.
     * @return encoded training track
     */
    String getEncodedTrack() {
        return PolyUtil.encode(this.getTrack());
    }

    /**
     * Generates JSONArray that represents Geolocation objects that are stored in {@link #locations}
     * @return JSONARRay representing Geolocation objects that are stored in {@link #locations}
     */
    String getJSONLocations() {
        return JSONLocationGenerator.generateJSONArray(this.locations).toString();
    }

    ArrayList<Geolocation> getLocations() {
        return locations;
    }
}
