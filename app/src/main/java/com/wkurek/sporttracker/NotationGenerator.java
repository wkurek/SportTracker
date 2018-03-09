package com.wkurek.sporttracker;

import java.util.Locale;

/**
 * Created by Wojtek on 09.03.2018.
 */

class NotationGenerator {
    static String generateTimeNotation(long secondsNumber) {
        long hours = secondsNumber/3600;
        long minutes = (secondsNumber%3600)/60;
        long secs = secondsNumber%60;

        return String.format(Locale.GERMANY,"%d:%02d:%02d", hours, minutes,secs);
    }

    static String generateDistanceNotation(double distance) {
        return String.format(Locale.GERMANY, "%.2f km", distance/1000);
    }

    static String generateVelocityNotation(double velocity) {
        return String.format(Locale.GERMANY, "%.2f km/h", velocity*3.6);
    }

    static String generateAltitudeNotation(double altitude) {
        return String.format(Locale.GERMANY, "%.2f m", altitude);
    }

}
