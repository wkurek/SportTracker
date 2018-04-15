package com.wkurek.sporttracker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

/**
 * Created by Wojtek on 14.04.2018.
 */

public class TrainingEntry {
    private long startTime, secondsNumber;
    private double distance;
    private List<LatLng> track;

    TrainingEntry(long startTime, long secondsNumber, double distance, String encodedTrack) {
        this.startTime = startTime;
        this.secondsNumber = secondsNumber;
        this.distance = distance;
        this.track = PolyUtil.decode(encodedTrack);
    }

    long getStartTime() {
        return startTime;
    }

    long getSecondsNumber() {
        return secondsNumber;
    }

    double getDistance() {
        return distance;
    }

    List<LatLng> getTrack() {
        return track;
    }
}
