package com.wkurek.sporttracker;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class DetailsActivity extends AppCompatActivity {
    private static final int INITIAL_TAB = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.details_toolbar);
        toolbar.setTitle(R.string.details_menu_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ViewPager viewPager = findViewById(R.id.details_view_pager);
        setUpViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.details_menu_delete_icon) {
            deleteTraining();
            return true;
        }
        return false;
    }


    /**
     * Method creates PagerAdapter for ViewPager and fill it with Fragments.
     * It also sets the initial displayed tab.
     * @param viewPager ViewPager object to be set up
     */
    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        Bundle args = getIntent().getExtras();

            SummaryMapFragment summaryMapFragment = new SummaryMapFragment();
            summaryMapFragment.setArguments(args);

            SummaryStatsFragment summaryStatsFragment = new SummaryStatsFragment();
            summaryStatsFragment.setArguments(args);

        adapter.addFragment(summaryMapFragment, getString(R.string.map_tab));
        adapter.addFragment(summaryStatsFragment, getString(R.string.stats_tab));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(INITIAL_TAB);
    }

    /**
     * Delete displayed training from database.
     * Close DetailsActivity and move to MainActivity because the training does not exist.
     */
    private void deleteTraining() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(SummaryActivity.START_TIME_KEY)) {
            long startTimestamp = bundle.getLong(SummaryActivity.START_TIME_KEY);

            DbHelper dbHelper = new DbHelper(this);
            dbHelper.deleteTraining(startTimestamp);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
