package com.wkurek.sporttracker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.wkurek.sporttracker.MainActivity.CHANNEL_ID;

public class TrackerService extends Service {
    public static final long LOCATION_REQUEST_INTERVAL_IN_MS = 1500;
    public static final long LOCATION_REQUEST_FASTEST_INTERVAL_IN_MS = LOCATION_REQUEST_INTERVAL_IN_MS/2;
    public static final float LOCATION_REQUEST_SMALLEST_DISPLACEMENT = 6;
    private static final int SERVICE_NOTIFICATION_ID = 41;

    TrackerBinder binder = new TrackerBinder();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private ArrayList<Location> locationList = new ArrayList<>();
    private long startTime;
    private long pauseTime = 0, delay = 0, pausedSecondsNumber = 0;
    private boolean paused = false;
    private double distance = 0.0;

    public TrackerService() {
    }

    class TrackerBinder extends Binder {
        TrackerService getService() {
            return TrackerService.this;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        startTime = System.currentTimeMillis();

        Notification notification = getServiceNotification();
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                Looper.myLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL_IN_MS);
        locationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL_IN_MS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(LOCATION_REQUEST_SMALLEST_DISPLACEMENT);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(!locationList.isEmpty()) {
                    Location lastLocation = locationList.get(locationList.size()-1);
                    distance += lastLocation.distanceTo(locationResult.getLastLocation());
                }
                locationList.add(locationResult.getLastLocation());
            }
        };
    }

    void pauseTraining() {
        pauseTime = System.currentTimeMillis();
        pausedSecondsNumber = getSecondsNumber();
        paused = true;
    }

    boolean isPaused() {
        return paused;
    }

    void resumeTraining() {
        delay += System.currentTimeMillis() - pauseTime;
        paused = false;
    }

    long getSecondsNumber() {
        return paused ? pausedSecondsNumber : (System.currentTimeMillis() - startTime - delay)/1000;
    }

    double getDistance() {
        return distance;
    }

    double getVelocity() {
        if(locationList.isEmpty()) return 0.0;

        Location lastLocation = locationList.get(locationList.size()-1);
        if(System.currentTimeMillis() - lastLocation.getTime() > 2*LOCATION_REQUEST_INTERVAL_IN_MS)
            return 0.0;
        return lastLocation.getSpeed();
    }

    double getAltitude() {
        if(locationList.isEmpty()) return 0.0;

        Location lastLocation = locationList.get(locationList.size()-1);
        return lastLocation.getAltitude();
    }

    long getStartTime() {
        return startTime;
    }

    double getAvgVelocity() {
        if(locationList.size() < 2) return 0.0;
        return getAvgVelocity(distance, getSecondsNumber());
    }

    ArrayList<LatLng> getLatLngList() {
        if(locationList.isEmpty()) return null;

        ArrayList<LatLng> latLngList = new ArrayList<>();
        for(Location location : locationList) {
            latLngList.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return latLngList;
    }

    ArrayList<Location> getLocationList() {
        return locationList.isEmpty() ? null : locationList;
    }

    public static double getAvgVelocity(double distance, long secondsNumber) {
        return distance/secondsNumber;
    }

    @TargetApi(Build.VERSION_CODES.O)
    Notification getServiceNotification() {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.service_notification_title))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            return builder.build();
        } else {
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.service_notification_title))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent);

            return builder.build();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
