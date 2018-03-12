package com.wkurek.sporttracker;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class TrainingActivity extends AppCompatActivity implements TrainingStopListener{

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
    }


    @Override
    public void onTrainingStop(long secondsNumber, double distance, ArrayList<Location> locations) {
        Intent serviceIntent = new Intent(this, TrackerService.class);
        stopService(serviceIntent);

        Intent activityIntent = new Intent(this, SummaryActivity.class);
            activityIntent.putExtra(SummaryActivity.SECONDS_NUMBER_KEY, secondsNumber);
            activityIntent.putExtra(SummaryActivity.DISTANCE_KEY, distance);
            activityIntent.putExtra(SummaryActivity.LOCATION_ARRAY_LIST_KEY, locations);
        startActivity(activityIntent);
        this.finish();
    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new TrainingMapFragment(), getString(R.string.map_tab));
            adapter.addFragment(new TrainingConsoleFragment(), getString(R.string.training_console_tab));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1); //Set CONSOLE tab as initial one
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titleList = new ArrayList<>();

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
