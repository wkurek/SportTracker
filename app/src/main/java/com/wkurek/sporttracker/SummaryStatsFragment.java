package com.wkurek.sporttracker;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Display basic information about training.
 * Including time, distance, velocity and altitude
 */
public class SummaryStatsFragment extends Fragment {


    public SummaryStatsFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments == null) return;

        TextView calendarView = view.findViewById(R.id.summary_calendar_text);
        TextView timeView = view.findViewById(R.id.summary_time_text);
        TextView distanceView = view.findViewById(R.id.summary_distance_text);
        TextView avgVelocityView = view.findViewById(R.id.summary_avg_velocity_text);
        TextView maxVelocityView = view.findViewById(R.id.summary_max_velocity_text);
        TextView avgAltitudeView = view.findViewById(R.id.summary_avg_altitude_text);
        TextView maxAltitudeView = view.findViewById(R.id.summary_max_altitude_text);

        long secondsNumber = arguments.getLong(SummaryActivity.SECONDS_NUMBER_KEY);
        double distance = arguments.getDouble(SummaryActivity.DISTANCE_KEY);

        calendarView.setText(NotationGenerator.generateDateNotation(
                arguments.getLong(SummaryActivity.START_TIME_KEY)));

        timeView.setText(NotationGenerator.generateTimeNotation(secondsNumber));
        distanceView.setText(NotationGenerator.generateDistanceNotation(distance));

        ArrayList<Location> locationList = arguments.getParcelableArrayList(
                SummaryActivity.LOCATION_ARRAY_LIST_KEY);

        double maxVelocity = 0.0, avgVelocity = 0.0, maxAltitude = 0.0, avgAltitude = 0.0;

        if(locationList != null && !locationList.isEmpty()) {
            avgVelocity = TrackerService.getAvgVelocity(distance, secondsNumber);

            double altitudeSum = 0.0;

            for(Location location : locationList) {
                if(location.getSpeed() > maxVelocity) maxVelocity = location.getSpeed();
                if(location.getAltitude() > maxAltitude) maxAltitude = location.getAltitude();

                altitudeSum += location.getAltitude();
            }

            avgAltitude = altitudeSum/locationList.size();

        }

        avgVelocityView.setText(NotationGenerator.generateVelocityNotation(avgVelocity));
        maxVelocityView.setText(NotationGenerator.generateVelocityNotation(maxVelocity));
        avgAltitudeView.setText(NotationGenerator.generateAltitudeNotation(avgAltitude));
        maxAltitudeView.setText(NotationGenerator.generateAltitudeNotation(maxAltitude));
    }
}
