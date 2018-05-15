package com.wkurek.sporttracker;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;


public class TrainingActivity extends AppCompatActivity implements TrainingStopListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);

        ViewPager viewPager = findViewById(R.id.training_view_pager);
        setUpViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.training_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.training_toolbar);
        setSupportActionBar(toolbar);
    }


    /**
     * Method stops training and TrackerService that provides training data.
     * Training data are passed to SummaryActivity
     * @param startTime training start timestamp
     * @param secondsNumber number of seconds training lasted
     * @param distance training distance in meters
     * @param locations List of Geolocation object representing geographical points
     */
    @Override
    public void onTrainingStop(long startTime, long secondsNumber, double distance, ArrayList<Geolocation> locations) {
        Intent serviceIntent = new Intent(this, TrackerService.class);
        stopService(serviceIntent);

        Intent activityIntent = new Intent(this, SummaryActivity.class);
            activityIntent.putExtra(SummaryActivity.SECONDS_NUMBER_KEY, secondsNumber);
            activityIntent.putExtra(SummaryActivity.START_TIME_KEY, startTime);
            activityIntent.putExtra(SummaryActivity.DISTANCE_KEY, distance);
            activityIntent.putExtra(SummaryActivity.LOCATION_ARRAY_LIST_KEY, locations);
        startActivity(activityIntent);
        this.finish();
    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new TrainingMapFragment(), getString(R.string.map_tab));
            adapter.addFragment(new TrainingConsoleFragment(), getString(R.string.console_tab));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1); //Set CONSOLE tab as initial one
    }

}
