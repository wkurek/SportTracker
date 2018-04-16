package com.wkurek.sporttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Locale;

/**
 * Class whose aim is to help in communication with SQLite database.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
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
    static class TrainingContract implements BaseColumns{
        private TrainingContract() {}

        static final String TABLE_NAME = "trainings";
        static final String COLUMN_NAME_START_TIME = "start_time";
        static final String COLUMN_NAME_SECONDS_NUMBER = "seconds_number";
        static final String COLUMN_NAME_DISTANCE = "distance";
        static final String COLUMN_NAME_TRACK = "track";
        static final String COLUMN_NAME_LOCATIONS = "locations";

        static final String COLUMN_NAME_MAX_DISTANCE = "max_distance";
        static final String COLUMN_NAME_MAX_SECONDS_NUMBER = "max_seconds_number";

        static final String COLUMN_NAME_TRAININGS_NUMBER = "training_number";
        static final String COLUMN_NAME_SUM_DISTANCE = "distance_sum";
        static final String COLUMN_NAME_SUM_SECONDS_NUMBER = "seconds_number_sum";
    }

    DbHelper(Context context) {
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

   private Cursor select(String table, String[] columns,  String orderBy) {
       SQLiteDatabase database = this.getReadableDatabase();
       database.enableWriteAheadLogging();
       return database.query(table, columns, null, null, null, null, orderBy);
   }

   Cursor selectAllTrainings() {
        String[] columns = new String[] {TrainingContract.COLUMN_NAME_DISTANCE,
                TrainingContract.COLUMN_NAME_START_TIME, TrainingContract.COLUMN_NAME_LOCATIONS,
                TrainingContract.COLUMN_NAME_SECONDS_NUMBER, TrainingContract.COLUMN_NAME_TRACK};
        String orderBy = String.format(Locale.GERMANY, "%s DESC", TrainingContract.COLUMN_NAME_START_TIME);

        return select(TrainingContract.TABLE_NAME, columns, orderBy);
   }

   void insertTraining(ContentValues values) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.enableWriteAheadLogging();
        long result = database.insert(TrainingContract.TABLE_NAME, null, values);

        if(result > 0) {
            Log.i(TAG, "Training inserted to database correctly.");
        } else Log.i(TAG, "Error while inserting training to database.");
   }

   Cursor selectPeriodSummary(long startTime) {
       String query = String.format(Locale.GERMANY, "SELECT COUNT(%s) AS %s, SUM(%s) AS %s, SUM(%s) AS %s FROM %s WHERE %s > %d;",
               BaseColumns._ID, TrainingContract.COLUMN_NAME_TRAININGS_NUMBER, TrainingContract.COLUMN_NAME_DISTANCE,
               TrainingContract.COLUMN_NAME_SUM_DISTANCE, TrainingContract.COLUMN_NAME_SECONDS_NUMBER,
               TrainingContract.COLUMN_NAME_SUM_SECONDS_NUMBER, TrainingContract.TABLE_NAME,
               TrainingContract.COLUMN_NAME_START_TIME, startTime);

       SQLiteDatabase database = this.getReadableDatabase();
       database.enableWriteAheadLogging();

       return database.rawQuery(query, null);
   }

   Cursor selectPersonalBests() {
       String query = String.format(Locale.GERMANY, "SELECT MAX(%s) AS %s, MAX(%s) AS %s FROM %s;",
               TrainingContract.COLUMN_NAME_DISTANCE, TrainingContract.COLUMN_NAME_MAX_DISTANCE,
               TrainingContract.COLUMN_NAME_SECONDS_NUMBER, TrainingContract.COLUMN_NAME_MAX_SECONDS_NUMBER,
               TrainingContract.TABLE_NAME);

       SQLiteDatabase database = this.getReadableDatabase();
       database.enableWriteAheadLogging();

       return database.rawQuery(query, null);
   }
}
