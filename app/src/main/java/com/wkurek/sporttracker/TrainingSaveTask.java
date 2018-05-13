package com.wkurek.sporttracker;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Background Task which aim is to insert training into local SQLite database.
 */

public class TrainingSaveTask extends AsyncTask<Long, Void, Void> {
    private DbHelper dbHelper;
    private ArrayList<Geolocation> locations;
    private double distance;

    TrainingSaveTask(DbHelper dbHelper, ArrayList<Geolocation> locations, double distance) {
        this.dbHelper = dbHelper;
        this.locations = locations;
        this.distance = distance;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        if(longs.length < 2 || locations == null) return null;


        //Generate List of LatLng objects required to PolyUtil encode method
        List<LatLng> latLngList = new ArrayList<>();
        for(Geolocation location : locations) {
            latLngList.add(location.getLatLng());
        }

        ContentValues values = new ContentValues();
        values.put(DbHelper.TrainingContract.COLUMN_NAME_START_TIME, longs[0]);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_SECONDS_NUMBER, longs[1]);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_DISTANCE, distance);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_LOCATIONS,
                JSONLocationGenerator.generateJSONArray(locations).toString());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_TRACK, PolyUtil.encode(latLngList));

        dbHelper.insertTraining(values);
        return null;
    }
}
