package edu.flsouthern.shancock.weatherapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.flsouthern.shancock.weatherapp.Data.WeatherContract.WeatherEntry;

/**
 * Manages a local database for weather data.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold weather info
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY, " +
                WeatherEntry.COLUMN_LOC_KEY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MIN_TEMP + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // Add a new cat to DB
    public void addWeatherEntry (int weather_id, String location_id, String short_desc, String date, String max, String min,
                                 String humidity, String pressure, String wind, String degrees) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_LOC_KEY, location_id);
        values.put(WeatherEntry.COLUMN_DATE, date);
        values.put(WeatherEntry.COLUMN_WEATHER_ID, weather_id);
        values.put(WeatherEntry.COLUMN_SHORT_DESC, short_desc);
        values.put(WeatherEntry.COLUMN_MIN_TEMP, min);
        values.put(WeatherEntry.COLUMN_MAX_TEMP, max);
        values.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
        values.put(WeatherEntry.COLUMN_PRESSURE, pressure);
        values.put(WeatherEntry.COLUMN_WIND_SPEED, wind);
        values.put(WeatherEntry.COLUMN_DEGREES, degrees);

        // Insert Row
        db.insert(WeatherEntry.TABLE_NAME, null, values);
        db.close(); // Close database connection
    }

    // Get a single day of weather
    public Cursor getWeatherDay(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WeatherEntry._ID,
                WeatherEntry.COLUMN_LOC_KEY,
                WeatherEntry.COLUMN_DATE,
                WeatherEntry.COLUMN_WEATHER_ID,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES
        };

        // Filter results WHERE "weather_id" = id #
        String selection = WeatherEntry.COLUMN_WEATHER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c = db.query(
                WeatherEntry.TABLE_NAME,              // The table to query
                projection,                       // The columns to return
                selection,                        // The columns for the WHERE clause
                selectionArgs,                    // The values for the WHERE clause
                null,                             // don't group the rows
                null,                             // don't filter by row groups
                null                              // No sort order
        );

        return c;
    }

    // Retrieve all weather data
    public Cursor getAllWeather() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WeatherEntry._ID,
                WeatherEntry.COLUMN_LOC_KEY,
                WeatherEntry.COLUMN_DATE,
                WeatherEntry.COLUMN_WEATHER_ID,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES
        };

        Cursor c = db.query(
                WeatherEntry.TABLE_NAME,              // The table to query
                projection,                       // The columns to return
                null,                             // no where clause columns
                null,                             // no where clause values
                null,                             // don't group the rows
                null,                             // don't filter by row groups
                null                              // No sort order
        );

        return c;
    }

    // Deleting all weather entries
    public void deleteAllWeather() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Issue SQL statement
        db.delete(WeatherEntry.TABLE_NAME, null, null);

        db.close();
    }


}
