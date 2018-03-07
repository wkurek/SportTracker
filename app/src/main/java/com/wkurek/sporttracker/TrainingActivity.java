package com.wkurek.sporttracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrainingActivity extends AppCompatActivity {
    private static final String TAG = TrainingActivity.class.getSimpleName();

    private TrackerService trackerService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.TrackerBinder binder = (TrackerService.TrackerBinder) iBinder;
            trackerService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, TrackerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unbindService(serviceConnection);
        bound = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, TrackerService.class);
        stopService(intent);

        super.onDestroy();
    }
}
