package com.wkurek.sporttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Class whose aim is to help in communication with SQLite database.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SportTracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_QUERY = "CREATE TABLE " + TrainingContract.TABLE_NAME +
            " (" + BaseColumns._ID + " INTEGER PRIMARY KEY, " + TrainingContract.COLUMN_NAME_START_TIME +
            " DATE NOT NULL, " + TrainingContract.COLUMN_NAME_SECONDS_NUMBER + " INTEGER NOT NULL, " +
            TrainingContract.COLUMN_NAME_DISTANCE + " DOUBLE NOT NULL, " + TrainingContract.COLUMN_NAME_TRACK +
            " TEXT NOT NULL, " + TrainingContract.COLUMN_NAME_LOCATIONS + " TEXT NOT NULL)";

    private static final String SQL_DROP_QUERY =  "DROP TABLE IF EXISTS " + TrainingContract.TABLE_NAME;


    /**
     * Class consists of public accessible constant variables which could be used while forming SQL queries.
     */
    public static class TrainingContract implements BaseColumns{
        private TrainingContract() {}

        public static final String TABLE_NAME = "trainings";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_SECONDS_NUMBER = "seconds_number";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_TRACK = "track";
        public static final String COLUMN_NAME_LOCATIONS = "locations";
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_QUERY); //When new version od DB appears drop existing Db
        onCreate(sqLiteDatabase); // and create a new one
    }
}
