package com.wkurek.sporttracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Aim of class is to provide unified notation of presenting data.
 */

class NotationGenerator {
    /**
     * Generate standard training duration notation.
     * @param secondsNumber number of seconds
     * @return standard notation of training time
     */
    static String generateTimeNotation(long secondsNumber) {
        long hours = secondsNumber/3600;
        long minutes = (secondsNumber%3600)/60;
        long secs = secondsNumber%60;

        return String.format(Locale.GERMANY,"%d:%02d:%02d", hours, minutes,secs);
    }

    /**
     * Generate standard distance notation.
     * @param distance training distance in meters
     * @return standard notation of training distance in km
     */
    static String generateDistanceNotation(double distance) {
        return String.format(Locale.GERMANY, "%.2f km", distance/1000);
    }
    /**
     * Generate standard velocity notation.
     * @param velocity training velocity in meters per second
     * @return standard notation of velocity distance in km/h
     */
    static String generateVelocityNotation(double velocity) {
        return String.format(Locale.GERMANY, "%.2f km/h", velocity*3.6);
    }
    /**
     * Generate standard altitude notation.
     * @param altitude training altitude in meters
     * @return standard notation of training altitude in meters
     */
    static String generateAltitudeNotation(double altitude) {
        return String.format(Locale.GERMANY, "%.2f m", altitude);
    }

    /**
     * Generate standard date notation of training.
     * @param secondsNumber training start timestamp
     * @return standard date notation of training
     */
    static String generateDateNotation(long secondsNumber) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMANY)
                .format(new Date(secondsNumber));
    }

    /**
     * Generate standard trainings number notation
     * @param trainingsNumber number of trainings
     * @return standard trainings number notation
     */
    static String generateTrainingsNumberNotation(int trainingsNumber) {
        if(trainingsNumber != 1) {
            return String.format(Locale.GERMANY, "%d trainings", trainingsNumber);
        } else return "1 training";
    }
}
