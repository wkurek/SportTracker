package com.wkurek.sporttracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SummaryActivity extends AppCompatActivity {
    public static final String SECONDS_NUMBER_KEY = "seconds number key";
    public static final String DISTANCE_KEY = "distance key";
    public static final String LOCATION_ARRAY_LIST_KEY = "Location ArrayList key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
    }
}
