package com.wkurek.sporttracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Background Task which aim is to insert training into local SQLite database.
 * Class gets an instance of open and writable SQLite database inside its constructor.
 */

public class TrainingSaveTask extends AsyncTask<Long, Void, Void> {
    private SQLiteDatabase database;

    private ArrayList<Location> locations;
    private double distance;

    TrainingSaveTask(SQLiteDatabase database, ArrayList<Location> locations, double distance) {
        this.database = database;
        this.locations = locations;
        this.distance = distance;
    }


    @Override
    protected Void doInBackground(Long... longs) {
        if(database == null || !database.isOpen() || database.isReadOnly()
                || longs.length < 2 || locations == null) return null;


        //Generate List of LatLng objects required to PolyUtil encode method
        List<LatLng> latLngList = new ArrayList<>();
        for(Location location : locations) {
            latLngList.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        ContentValues values = new ContentValues();
        values.put(DbHelper.TrainingContract.COLUMN_NAME_START_TIME, longs[0]);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_SECONDS_NUMBER, longs[1]);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_DISTANCE, distance);
        values.put(DbHelper.TrainingContract.COLUMN_NAME_LOCATIONS,
                JSONLocationGenerator.generateJSONArray(locations).toString());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_TRACK, PolyUtil.encode(latLngList));

        database.insert(DbHelper.TrainingContract.TABLE_NAME, null, values);
        return null;
    }
}
