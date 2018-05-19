package com.wkurek.sporttracker;

import android.content.ContentValues;
import android.os.AsyncTask;

/**
 * Background Task which aim is to insert training into local SQLite database.
 */

public class TrainingSaveTask extends AsyncTask<Void, Void, Void> {
    private DbHelper dbHelper;
    private TrainingEntry trainingEntry;

    TrainingSaveTask(DbHelper dbHelper, TrainingEntry trainingEntry) {
        this.dbHelper = dbHelper;
        this.trainingEntry = trainingEntry;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.TrainingContract.COLUMN_NAME_START_TIME, trainingEntry.getStartTime());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_SECONDS_NUMBER, trainingEntry.getSecondsNumber());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_DISTANCE, trainingEntry.getDistance());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_LOCATIONS, trainingEntry.getJSONLocations());
        values.put(DbHelper.TrainingContract.COLUMN_NAME_TRACK, trainingEntry.getEncodedTrack());

        dbHelper.insertTraining(values);
        return null;
    }
}
