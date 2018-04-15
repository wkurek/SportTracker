package com.wkurek.sporttracker;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
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
        trainings = trainingEntries;
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<TrainingEntry>> loader) {}

    public class TrainingArchiveAdapter extends RecyclerView.Adapter<TrainingArchiveAdapter.TrainingViewHolder> {
        private List<TrainingEntry> trainings;

        class TrainingViewHolder extends RecyclerView.ViewHolder {
            TextView dateView, timeView, distanceView;
            TrainingEntry trainingEntry;
            private MapFragment mapFragment;

            TrainingViewHolder(View view) {
                super(view);

                dateView = view.findViewById(R.id.training_card_date);
                timeView = view.findViewById(R.id.training_card_time);
                distanceView = view.findViewById(R.id.training_card_distance);
            }

            void addMapFragment(OnMapReadyCallback callback) {
                if(mapFragment == null) {
                    mapFragment = MapFragment.newInstance();
                    mapFragment.getMapAsync(callback);
                }

                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().add(R.id.training_card_map, mapFragment).commit();
            }

            void removeMapFragment() {
                if(mapFragment != null) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    fragmentManager.beginTransaction().remove(mapFragment).commit();
                    mapFragment = null;
                }
            }
        }

        TrainingArchiveAdapter(List<TrainingEntry> trainings) {
            this.trainings = trainings;
        }


        @Override
        public TrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View trainingCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_card,
                    parent, false);

            return new TrainingViewHolder(trainingCard);
        }

        @Override
        public void onBindViewHolder(TrainingViewHolder holder, int position) {
            TrainingEntry trainingEntry = trainings.get(position);
            holder.trainingEntry = trainingEntry;

            holder.dateView.setText(NotationGenerator.generateDateNotation(
                    trainingEntry.getStartTime()));
            holder.timeView.setText(NotationGenerator.generateTimeNotation(
                    trainingEntry.getSecondsNumber()));
            holder.distanceView.setText(NotationGenerator.generateDistanceNotation(
                    trainingEntry.getDistance()));
        }

        @Override
        public void onViewAttachedToWindow(final TrainingViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            if(holder != null && holder.trainingEntry != null) {
                holder.addMapFragment(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        //TODO: implement this method
                        PolylineOptions polylineOptions = new PolylineOptions();
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for(LatLng latLng : holder.trainingEntry.getTrack()) {
                            boundsBuilder.include(latLng);
                            polylineOptions.add(latLng);
                        }

                        googleMap.addPolyline(polylineOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return trainings.size();
        }

        @Override
        public void onViewDetachedFromWindow(TrainingViewHolder holder) {
            super.onViewDetachedFromWindow(holder);

            if(holder != null) {
                holder.removeMapFragment();
            }
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ARCHIVE_TRAININGS_LOADER_ID, null, this);
    }
}
