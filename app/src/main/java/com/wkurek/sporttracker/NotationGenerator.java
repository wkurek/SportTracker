package com.wkurek.sporttracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Aim of class is to provide unified notation of presenting particular data.
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

    static String generateDateNotation(long secondsNumber) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY)
                .format(new Date(secondsNumber));
    }

    static String generateTrainingsNumberNotation(int trainingsNumber) {
        if(trainingsNumber != 1) {
            return String.format(Locale.GERMANY, "%d trainings", trainingsNumber);
        } else return "1 training";
    }
}
