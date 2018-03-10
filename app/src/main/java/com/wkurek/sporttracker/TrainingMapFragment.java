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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.PolylineOptions;


public class TrainingMapFragment extends Fragment implements OnMapReadyCallback{
    private static final int DELAY_TRAINING_MAP_FILL_MS = 2500;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapView Bundle key";

    private TrackerService trackerService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.TrackerBinder binder = (TrackerService.TrackerBinder) iBinder;
            trackerService = binder.getService();
            bound = true;

            if(map != null) fillMap();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    private MapView mapView;
    private GoogleMap map;

    private Handler uiHandler;
    private Runnable uiTask = new Runnable() {
        @Override
        public void run() {
            if(bound && trackerService != null && !trackerService.isPaused() && map != null) {
                if(trackerService.getLatLngList() != null) {
                    clearMap();
                    fillMap();
                }
            }
            uiHandler.postDelayed(this, DELAY_TRAINING_MAP_FILL_MS);
        }
    };

    public TrainingMapFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        uiHandler = new Handler();
        return inflater.inflate(R.layout.fragment_training_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.training_map);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null)   {
            mapViewBundle= savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        uiHandler.post(uiTask);
    }

    private void fillMap() {
        //TODO; customize polly line
        PolylineOptions polylineOptions = new PolylineOptions().addAll(trackerService.getLatLngList());
        map.addPolyline(polylineOptions);
    }

    private void clearMap() {
        map.clear();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(bound) fillMap();
    }

    @Override
    public void onPause() {
        uiHandler.removeCallbacks(uiTask);

        getActivity().unbindService(serviceConnection);
        bound = false;

        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if(mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

}
