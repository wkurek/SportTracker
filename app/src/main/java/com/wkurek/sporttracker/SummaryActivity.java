package com.wkurek.sporttracker;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    public static final String SECONDS_NUMBER_KEY = "seconds number key";
    public static final String START_TIME_KEY = "start time key";
    public static final String DISTANCE_KEY = "distance key";
    public static final String LOCATION_ARRAY_LIST_KEY = "Location ArrayList key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = findViewById(R.id.summary_toolbar);
        toolbar.setTitle(R.string.summary_menu_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Provide back navigation to MainActivity
        }

        ViewPager viewPager = findViewById(R.id.summary_view_pager);
        setUpViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.summary_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.summary_menu_delete_icon) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return false;
    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();

            SummaryStatsFragment summaryStatsFragment = new SummaryStatsFragment();
            summaryStatsFragment.setArguments(bundle);

        //TODO: add proper fragments to adapter
        //pagerAdapter.addFragment(summaryStatsFragment, getString(R.string.map_tab));
        pagerAdapter.addFragment(summaryStatsFragment, getString(R.string.stats_tab));
        //pagerAdapter.addFragment(summaryStatsFragment, getString(R.string.chart_tab));

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1); //Set middle tab as an initial one
    }
}
