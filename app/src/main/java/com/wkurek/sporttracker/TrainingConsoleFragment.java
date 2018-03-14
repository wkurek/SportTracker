package com.wkurek.sporttracker;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class TrainingConsoleFragment extends Fragment {
    private TrainingStopListener trainingStopListener;
    private TrackerService trackerService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.TrackerBinder binder = (TrackerService.TrackerBinder) iBinder;
            trackerService = binder.getService();
            bound = true;

            initializeUI(); //initially fill controls with data
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    private TextView timeView, distanceView, velocityView, altitudeView, avgVelocityView;
    private Button pauseButton;

    private Handler uiHandler;
    private Runnable uiTask = new Runnable() {
        @Override
        public void run() {
            if(bound && trackerService != null && !trackerService.isPaused()) {
                fillControls();
            }
            uiHandler.postDelayed(this, 1000);
        }
    };


    public TrainingConsoleFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        trainingStopListener = (TrainingStopListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_console, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        timeView = view.findViewById(R.id.training_time_view);
        distanceView = view.findViewById(R.id.training_distance_view);
        velocityView = view.findViewById(R.id.training_velocity_view);
        altitudeView = view.findViewById(R.id.training_altitude_view);
        avgVelocityView = view.findViewById(R.id.training_avg_velocity_view);

        pauseButton = view.findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bound) {
                    if(trackerService.isPaused()) {
                        trackerService.resumeTraining();
                        pauseButton.setText(R.string.pause);
                    } else {
                        fillControls(); //fill controls with data for the moment of clicking PAUSE
                        trackerService.pauseTraining();
                        pauseButton.setText(R.string.resume);
                    }
                }
            }
        });

        Button stopButton = view.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bound && trackerService != null) {
                    trainingStopListener.onTrainingStop(trackerService.getStartTime(),
                            trackerService.getSecondsNumber(), trackerService.getDistance(),
                            trackerService.getLocationList());
                }
            }
        });

        uiHandler = new Handler();

    }

    private void initializeUI() {
        fillControls();
        if(trackerService.isPaused()) {
            pauseButton.setText(R.string.resume);
        }
    }

    private void fillControls() {
        timeView.setText(NotationGenerator.generateTimeNotation(trackerService.getSecondsNumber()));
        distanceView.setText(NotationGenerator.generateDistanceNotation(trackerService.getDistance()));
        velocityView.setText(NotationGenerator.generateVelocityNotation(trackerService.getVelocity()));
        altitudeView.setText(NotationGenerator.generateAltitudeNotation(trackerService.getAltitude()));
        avgVelocityView.setText(NotationGenerator.generateVelocityNotation(trackerService.getAvgVelocity()));
    }


    @Override
    public void onResume() {
        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        uiHandler.post(uiTask);
        super.onResume();
    }

    @Override
    public void onPause() {
        uiHandler.removeCallbacks(uiTask);

        getActivity().unbindService(serviceConnection);
        bound = false;
        super.onPause();
    }

}
