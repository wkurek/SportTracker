package com.wkurek.sporttracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrainingActivity extends AppCompatActivity {
    private TrackerService trackerService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.TrackerBinder binder = (TrackerService.TrackerBinder) iBinder;
            trackerService = binder.getService();
            bound = true;

            initializeUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    private TextView timeView, distanceView, velocityView, altitudeView;
    private Button pauseButton;

    private Handler uiHandler;
    private Runnable uiTask = new Runnable() {
        @Override
        public void run() {
            if(bound && trackerService != null && !trackerService.isPaused()) {
                fillUI();
            }
            uiHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        timeView = findViewById(R.id.training_time_view);
        distanceView = findViewById(R.id.training_distance_view);
        velocityView = findViewById(R.id.training_velocity_view);
        altitudeView = findViewById(R.id.training_altitude_view);

        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bound) {
                    if(trackerService.isPaused()) {
                        trackerService.resumeTraining();
                        pauseButton.setText(R.string.pause);
                    } else {
                        trackerService.pauseTraining();
                        pauseButton.setText(R.string.resume);
                    }
                }
            }
        });

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TrackerService.class);
                stopService(intent);
            }
        });

        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);

        uiHandler = new Handler();
    }

    private void initializeUI() {
        fillUI();
        if(trackerService.isPaused()) {
            pauseButton.setText(R.string.resume);
        }
    }

    private void fillUI() {
        timeView.setText(NotationGenerator.generateTimeNotation(trackerService.getSecondsNumber()));
        distanceView.setText(NotationGenerator.generateDistanceNotation(trackerService.getDistance()));
        velocityView.setText(NotationGenerator.generateVelocityNotation(trackerService.getVelocity()));
        altitudeView.setText(NotationGenerator.generateAltitudeNotation(trackerService.getAltitude()));
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(this, TrackerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        uiHandler.post(uiTask);
        super.onResume();
    }

    @Override
    protected void onPause() {
        uiHandler.removeCallbacks(uiTask);

        unbindService(serviceConnection);
        bound = false;
        super.onPause();
    }

}
