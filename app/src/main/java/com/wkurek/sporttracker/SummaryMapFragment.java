package com.wkurek.sporttracker;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


/**
 * Fragment which presents track to user on GoogleMap.
 */
public class SummaryMapFragment extends Fragment implements OnMapReadyCallback{
    private static final String MAP_VIEW_BUNDLE_KEY = "MapView Bundle";

    private MapView mapView;
    private List<Location> locationList;

    public SummaryMapFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        locationList = getArguments().getParcelableArrayList(SummaryActivity.LOCATION_ARRAY_LIST_KEY);

        mapView = view.findViewById(R.id.summary_map);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(locationList != null && locationList.size() > 1) {
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(getResources().getColor(R.color.colorAccent));

            for(Location location : locationList) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                latLngBuilder.include(latLng);
                polylineOptions.add(latLng);
            }

            googleMap.addPolyline(polylineOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 100));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if(mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
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
