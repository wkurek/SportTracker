package com.wkurek.sporttracker;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Wojtek on 10.03.2018.
 */

public interface TrainingStopListener {
    void onTrainingStop(long startTime, long secondsNumber, double distance, ArrayList<Location> locations);
}
