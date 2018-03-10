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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingMapFragment extends Fragment {
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

    TextView textView;

    Handler uiChangesHandler;
    Runnable uiUpdateTask = new Runnable() {
        @Override
        public void run() {
            if(bound && trackerService != null && !trackerService.isPaused()) {
                textView.setText(NotationGenerator.generateTimeNotation(trackerService.getSecondsNumber()));
            }
            uiChangesHandler.postDelayed(this, 1000);
        }
    };


    public TrainingMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        uiChangesHandler = new Handler();
        return inflater.inflate(R.layout.fragment_training_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        textView = view.findViewById(R.id.training_map_text);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        uiChangesHandler.post(uiUpdateTask);
    }

    @Override
    public void onPause() {
        uiChangesHandler.removeCallbacks(uiUpdateTask);

        getActivity().unbindService(serviceConnection);
        bound = false;

        super.onPause();
    }
}
