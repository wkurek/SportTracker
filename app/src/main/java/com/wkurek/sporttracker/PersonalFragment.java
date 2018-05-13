package com.wkurek.sporttracker;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {
    private static final String TAG = PersonalFragment.class.getSimpleName();

    public PersonalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DbHelper helper = new DbHelper(getActivity());

        Cursor totalCursor = helper.selectPeriodSummary(0);
        if(totalCursor.getCount() == 1) fillTotalSummary(view, totalCursor);

        Cursor monthCursor = helper.selectPeriodSummary(getCurrentMonthTimestamp());
        if(monthCursor.getCount() == 1) fillMonthSummary(view, monthCursor);

        Cursor personalBestsCursor = helper.selectPersonalBests();
        if(personalBestsCursor.getCount() == 1) fillHallOfFame(view, personalBestsCursor);
    }

    private void fillTotalSummary(View layout, Cursor cursor) {
        fillSummary(layout, cursor, R.id.personal_total_trainings_number,
                R.id.personal_total_distance, R.id.personal_total_time);
    }

    private void fillMonthSummary(View layout, Cursor cursor) {
        fillSummary(layout, cursor, R.id.personal_monthly_trainings_number,
                R.id.personal_monthly_distance, R.id.personal_monthly_time);
    }

    private void fillSummary(View layout, Cursor cursor, int trainingsNumberViewId, int distanceViewId, int timeViewId) {
        cursor.moveToFirst();

        int trainingsNumberIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_TRAININGS_NUMBER);
        int secondsNumberSumIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_SUM_SECONDS_NUMBER);
        int distanceSumIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_SUM_DISTANCE);

        TextView trainingsNumberView = layout.findViewById(trainingsNumberViewId);
        TextView distanceView = layout.findViewById(distanceViewId);
        TextView timeView = layout.findViewById(timeViewId);

        trainingsNumberView.setText(NotationGenerator.generateTrainingsNumberNotation(cursor.getInt(trainingsNumberIndex)));
        distanceView.setText(NotationGenerator.generateDistanceNotation(cursor.getDouble(distanceSumIndex)));
        timeView.setText(NotationGenerator.generateTimeNotation(cursor.getLong(secondsNumberSumIndex)));

        cursor.close();
    }

    private void fillHallOfFame(View layout, Cursor cursor) {
        cursor.moveToFirst();

        int secondsNumberMaxIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_MAX_SECONDS_NUMBER);
        int distanceMaxIndex = cursor.getColumnIndex(DbHelper.TrainingContract.COLUMN_NAME_MAX_DISTANCE);

        TextView distanceView = layout.findViewById(R.id.personal_best_distance);
        TextView timeView = layout.findViewById(R.id.personal_best_time);

        distanceView.setText(NotationGenerator.generateDistanceNotation(cursor.getDouble(distanceMaxIndex)));
        timeView.setText(NotationGenerator.generateTimeNotation(cursor.getLong(secondsNumberMaxIndex)));

        cursor.close();
    }

    /**
     * Function which calculates the timestamp of the first day of current month.
     * @return timestamp of the first day of current month
     */
    private long getCurrentMonthTimestamp() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));

        return calendar.getTimeInMillis();
    }
}
