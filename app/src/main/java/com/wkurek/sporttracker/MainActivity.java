package com.wkurek.sporttracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "tracker service channel";
    private static final String CHANNEL_GROUP_ID = "tracker channel group";
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 21;
    private static final int REQUEST_CHANGE_LOCATION_SETTINGS_CODE = 0x1;

    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;

    private DrawerLayout drawerLayout;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsClient = new SettingsClient(this);
        createLocationRequest();
        createLocationSettingsRequest();

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //Set up NavigationDrawer
        drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                replaceFragment(currentFragment);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        final NavigationView navigationView = findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_nav_personal: {
                        currentFragment = new PersonalFragment();
                        break;
                    }
                    case R.id.main_nav_trainings: {
                        currentFragment = new TrainingsArchiveFragment();
                        break;
                    }
                    default: {
                        currentFragment = null;
                        return false;
                    }
                }
                item.setChecked(true); //mark drawer option as checked
                drawerLayout.closeDrawers(); //close drawer after selecting option

                return true;
            }
        });

        //Set up training archive as an initial tab
        navigationView.setCheckedItem(R.id.main_nav_trainings);
        navigationView.getMenu().performIdentifierAction(R.id.main_nav_trainings, 0);
        replaceFragment(currentFragment);

        FloatingActionButton floatingActionButton = findViewById(R.id.main_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLocationPermission()) {
                    startTraining();
                } else requestLocationPermission();
            }
        });

        createNotificationChannel();
    }

    /**
     * Method does swap transaction between two Fragments.
     * @param fragment new Fragment to be placed in FrameLayout
     */
    private void replaceFragment(Fragment fragment) {
        if(fragment == null) return;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void startTraining() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(
                new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        startTrainingActivity();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                requestLocationSettings(e);
            }
        });
    }

    private void requestLocationSettings(@NonNull Exception e) {
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode){
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                Log.i(TAG, "Location settings are not provided. Attempting to upgrade.");
                try {
                    ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(this , REQUEST_CHANGE_LOCATION_SETTINGS_CODE);
                } catch (IntentSender.SendIntentException e1) {
                    Log.i(TAG, e1.getMessage());
                }
                break;
            }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                Log.i(TAG, "Location setting change unavailable.");
                break;
            }
        }
    }

    /**
     * Method starts TrainingActivity and the training itself.
     */
    private void startTrainingActivity() {
        Intent intent = new Intent(this, TrainingActivity.class);
        startActivity(intent);
    }

    /**
     * Method creates LocationRequest object which represents app requirements of location permissions.
     */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TrackerService.LOCATION_REQUEST_INTERVAL_IN_MS);
        locationRequest.setFastestInterval(TrackerService.LOCATION_REQUEST_FASTEST_INTERVAL_IN_MS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    /**
     * Method checks if required location permissions are granted.
     * @return whether location permissions are granted
     */
    private boolean checkLocationPermission() {
        int status = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method is invoked when urgent location permissions are not granted.
     * It's aim is to request user for these permissions.
     */
    private void requestLocationPermission() {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(shouldShowRationale) {
            Log.i(TAG, "Show location rationale to user.");
            showToast(this, R.string.location_permission_rationale);
        }

        Log.i(TAG, "Request location permission.");
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if(grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location permission granted.");
                startTraining();
            } else {
                Log.i(TAG, "Location permission not granted.");
                showToast(this, R.string.location_permission_denied);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHANGE_LOCATION_SETTINGS_CODE) {
            switch(resultCode) {
                case Activity.RESULT_OK: {
                    startTrainingActivity();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Log.i(TAG, "Cannot acquire required location settings.");
                    break;
                }
            }
        }
    }

    /**
     * Method creates NotificationChannel which is used for displaying notifications on devices
     * with Android Oreo and newer.
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            if(manager != null) {
                CharSequence channelGroupName = getString(R.string.channel_group_name);
                manager.createNotificationChannelGroup(new NotificationChannelGroup(CHANNEL_GROUP_ID,
                        channelGroupName));

                CharSequence channelName = getString(R.string.channel_name);
                String channelDescription = getString(R.string.channel_description);
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setGroup(CHANNEL_GROUP_ID);
                channel.setDescription(channelDescription);
                channel.enableLights(true);
                channel.setLightColor(Color.MAGENTA);

                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Method shows Toast with passed text.
     * @param context Contex in which this Toast is showed
     * @param resId id of message string resource
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
