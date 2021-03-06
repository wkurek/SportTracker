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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


public class TrainingMapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = TrainingMapFragment.class.getSimpleName();
    private static final int DELAY_TRAINING_MAP_FILL_MS = 2500;

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

    private GoogleMap map;

    private Handler uiHandler;
    private Runnable uiTask = new Runnable() {
        @Override
        public void run() {
            if(bound && trackerService != null && !trackerService.isPaused() && map != null) {
                if(trackerService.getLatLngList() != null
                        && trackerService.getLatLngList().size() > 1) {
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.training_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        uiHandler.post(uiTask);
    }

    /**
     * Method fills GoogleMap with training data in form of PolyLine.
     */
    private void fillMap() {
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(getResources().getColor(R.color.colorAccent));

        List<LatLng> latLngList = trackerService.getLatLngList();
        for(LatLng latLng : latLngList) {
            polylineOptions.add(latLng);
        }

        map.addPolyline(polylineOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(latLngList.size() - 1), 16));

    }

    /**
     * Method clears map.
     */
    private void clearMap() {
        map.clear();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onPause() {
        uiHandler.removeCallbacks(uiTask);

        getActivity().unbindService(serviceConnection);
        bound = false;
        super.onPause();
    }

}
