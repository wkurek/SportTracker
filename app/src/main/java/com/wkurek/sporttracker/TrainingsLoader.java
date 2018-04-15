package com.wkurek.sporttracker;

import android.content.Context;
import android.database.Cursor;
import android.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader which loads trainings from SQLite database.
 * Data from Cursor are converted to List of TrainingEntries.
 */

public class TrainingsLoader extends AsyncTaskLoader<List<TrainingEntry>> {
    private List<TrainingEntry> trainings;

    TrainingsLoader(Context context) {
        super(context);
    }

    @Override
    public List<TrainingEntry> loadInBackground() {
        List<TrainingEntry> data = new ArrayList<>();

        DbHelper helper = new DbHelper(getContext());
        Cursor cursor = helper.selectAllTrainings();

        if(cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }

        int startTimeIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_START_TIME);
        int secondsNumberIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_SECONDS_NUMBER);
        int distanceIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_DISTANCE);
        int trackIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_TRACK);

        while(cursor.moveToNext()) {
            TrainingEntry trainingEntry = new TrainingEntry(cursor.getLong(startTimeIndex),
                    cursor.getLong(secondsNumberIndex), cursor.getDouble(distanceIndex),
                    cursor.getString(trackIndex));

            data.add(trainingEntry);
        }

        cursor.close();
        return data;
    }

    @Override
    public void deliverResult(List<TrainingEntry> data) {
        if(isReset()) {
            releaseResources(data);
            return;
        }

        List<TrainingEntry> oldData = trainings; //avoid deleting old data by garbage collector before swap of data
        trainings = data;

        if(isStarted()) {
            super.deliverResult(data);
        }

        if(oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if(trainings != null) {
            deliverResult(trainings);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStartLoading();

        if(trainings != null) {
            releaseResources(trainings);
            trainings = null;
        }
    }

    @Override
    public void onCanceled(List<TrainingEntry> data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(List<TrainingEntry> data) {}
}
