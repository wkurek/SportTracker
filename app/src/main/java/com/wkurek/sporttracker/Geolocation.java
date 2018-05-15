package com.wkurek.sporttracker;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class representing a single geographical point captured during training.
 * Contains only required fields.
 * Based on Location class but allows to be created using constructor.
 */

public class Geolocation implements Parcelable{
    private double lat, lng, altitude, velocity;
    private long timestamp;

    public Geolocation() {
        this(0.0, 0.0, 0.0, 0.0, 0);
    }

    Geolocation(double lat, double lng, double altitude, double velocity, long timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;

        this.velocity = velocity;
        this.timestamp = timestamp;
    }

    Geolocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.altitude = location.getAltitude();
        this.velocity = location.getSpeed();
        this.timestamp = location.getTime();
    }

    double getLatitude() {
        return lat;
    }

    double getLongitude() {
        return lng;
    }

    double getAltitude() {
        return altitude;
    }

    double getSpeed() {
        return velocity;
    }

    public long getTime() {
        return timestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.altitude);
        dest.writeDouble(this.velocity);
        dest.writeLong(this.timestamp);
    }

    private Geolocation(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.altitude = in.readDouble();
        this.velocity = in.readDouble();
        this.timestamp = in.readLong();
    }

    public static final Creator<Geolocation> CREATOR = new Creator<Geolocation>() {
        @Override
        public Geolocation createFromParcel(Parcel source) {
            return new Geolocation(source);
        }

        @Override
        public Geolocation[] newArray(int size) {
            return new Geolocation[size];
        }
    };

    LatLng getLatLng() {
        return new LatLng(this.lat, this.lng);
    }

    /**
     * Calculates distance to function parameter in meters.
     * @param location geographical point to which we calculate distance
     * @return distance to function parameter in meters
     */
    float distanceTo(Location location) {
        float results[] = new float[3];
        Location.distanceBetween(this.lat, this.lng, location.getLatitude(),
                location.getLongitude(), results);
        return results[0];
    }
}
