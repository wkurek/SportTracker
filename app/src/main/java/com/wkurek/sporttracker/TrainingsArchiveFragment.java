package com.wkurek.sporttracker;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Fragment which presents saved trainings.
 * Trainings archive is presented in form of RecyclerView.
 */
public class TrainingsArchiveFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TrainingEntry>> {
    private static final int ARCHIVE_TRAININGS_LOADER_ID = 41;
    private static final String TAG = TrainingArchiveAdapter.class.getSimpleName();

    private List<TrainingEntry> trainings = new ArrayList<>();
    private TrainingArchiveAdapter adapter;

    @Override
    public Loader<List<TrainingEntry>> onCreateLoader(int i, Bundle bundle) {
        return new TrainingsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<TrainingEntry>> loader, List<TrainingEntry> trainingEntries) {
        trainings.clear();
        trainings.addAll(trainingEntries);

        Log.i(TAG, String.format(Locale.GERMANY, "Trainings number: %d", trainings.size()));
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<TrainingEntry>> loader) {}

    class TrainingArchiveAdapter extends RecyclerView.Adapter<TrainingArchiveAdapter.TrainingViewHolder> {
        private List<TrainingEntry> trainings;

        class TrainingViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
            private TrainingEntry trainingEntry;
            private GoogleMap googleMap;
            TextView dateView, timeView, distanceView, velocityView;
            MapView mapView;

            TrainingViewHolder(View layout) {
                super(layout);
                dateView = layout.findViewById(R.id.training_card_date);
                timeView = layout.findViewById(R.id.training_card_time);
                distanceView = layout.findViewById(R.id.training_card_distance);
                velocityView = layout.findViewById(R.id.training_card_velocity);

                mapView = layout.findViewById(R.id.training_card_map_view);
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }

            void setTrainingEntry(TrainingEntry trainingEntry) {
                this.trainingEntry = trainingEntry;
                if(googleMap != null) showTrackOnMap();
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                this.googleMap = googleMap;

                //GoogleMap settings
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setMapToolbarEnabled(false);
                mapView.setClickable(false);

                if(trainingEntry != null) showTrackOnMap();
            }

            private void showTrackOnMap() {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(getResources().getColor(R.color.colorAccent));

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for(LatLng latLng : trainingEntry.getTrack()) {
                    boundsBuilder.include(latLng);
                    polylineOptions.add(latLng);
                }

                googleMap.addPolyline(polylineOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));
            }
        }

        TrainingArchiveAdapter(List<TrainingEntry> trainings) {
            this.trainings = trainings;
        }

        @NonNull
        @Override
        public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View trainingCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_card,
                    parent, false);

            return new TrainingViewHolder(trainingCard);
        }

        @Override
        public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
            TrainingEntry trainingEntry = trainings.get(position);
            holder.setTrainingEntry(trainingEntry);

            holder.dateView.setText(NotationGenerator.generateDateNotation(
                    trainingEntry.getStartTime()));
            holder.timeView.setText(NotationGenerator.generateTimeNotation(
                    trainingEntry.getSecondsNumber()));
            holder.distanceView.setText(NotationGenerator.generateDistanceNotation(
                    trainingEntry.getDistance()));
            holder.velocityView.setText(NotationGenerator.generateVelocityNotation(
                    TrackerService.getAvgVelocity(trainingEntry.getDistance(),
                            trainingEntry.getSecondsNumber())));
        }

        @Override
        public int getItemCount() {
            return trainings.size();
        }

    }

    public TrainingsArchiveFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_trainings_archive, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.archive_recycler_view);
        adapter = new TrainingArchiveAdapter(trainings);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.archive_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTrainingEntries();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTrainingEntries();
    }

    private void refreshTrainingEntries() {
        Log.i(TAG, "Refreshing trainings data set.");
        getLoaderManager().restartLoader(ARCHIVE_TRAININGS_LOADER_ID, null, this);
    }
}
