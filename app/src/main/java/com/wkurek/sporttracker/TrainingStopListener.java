package com.wkurek.sporttracker;

import android.location.Location;

import java.util.ArrayList;

/**
 * Interface which is implemented by TrainingActivity.
 * It's purpose is to make possible to stop TrackerService started in Activity from Fragment.
 */

public interface TrainingStopListener {
    void onTrainingStop(long startTime, long secondsNumber, double distance, ArrayList<Geolocation> locations);
}
