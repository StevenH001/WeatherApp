package edu.flsouthern.shancock.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.flsouthern.shancock.weatherapp.Data.WeatherContract;
import edu.flsouthern.shancock.weatherapp.Data.WeatherDbHelper;


public class MainActivity extends AppCompatActivity {

    WeatherAdapter mWeatherAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_map) {
            openWeatherInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWeatherInMap(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String location = sharedPrefs.getString(getString(R.string.pref_loc_key), getString(R.string.pref_loc_default));

        Uri Location = Uri.parse("geo:0,0?q=" + location);

        //Make Intent
        Intent intent = new Intent(Intent.ACTION_VIEW, Location);

        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get DB Helper
        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(this);
        Cursor cursor = weatherDbHelper.getAllWeather();

        //Make an adapter for our data
        mWeatherAdapter = new WeatherAdapter(this, cursor, 0);

        // Set up an ArrayAdapter that will take data from a source and use it to populate the ListView it's attached to.
        ListView listView = (ListView) findViewById(R.id.listview_forecast);
        listView.setAdapter(mWeatherAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    // Find the column numbers in the cursor
                    int idx_loc_key = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
                    int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
                    int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
                    int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
                    int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
                    int idx_humidity = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
                    int idx_pressure = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
                    int idx_wind_speed = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
                    int idx_degrees = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
                    int idx_weather_id = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);


                    // Get data to pass to detail activity
                    String location = cursor.getString(idx_loc_key);
                    String date = cursor.getString(idx_date);
                    String shortDesc = cursor.getString(idx_short_desc);
                    String max = cursor.getString(idx_max_temp);
                    String min = cursor.getString(idx_min_temp);
                    String humidity = cursor.getString(idx_humidity);
                    String pressure = cursor.getString(idx_pressure);
                    int windSpeed = cursor.getInt(idx_wind_speed);
                    int degrees = cursor.getInt(idx_degrees);
                    int weatherId = cursor.getInt(idx_weather_id);


                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                    // Load data to pass
                    intent.putExtra("WEATHER_LOC_KEY", location);
                    intent.putExtra("WEATHER_SHORT_DESC", shortDesc);
                    intent.putExtra("WEATHER_DATE", date);
                    intent.putExtra("WEATHER_MAX_TEMP", max);
                    intent.putExtra("WEATHER_MIN_TEMP", min);
                    intent.putExtra("WEATHER_HUMIDITY", humidity);
                    intent.putExtra("WEATHER_PRESSURE", pressure);
                    intent.putExtra("WEATHER_WIND_SPEED", windSpeed);
                    intent.putExtra("WEATHER_DEGREES", degrees);
                    intent.putExtra("WEATHER_ID", weatherId);

                    // Run activity
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // When startng page try to get cat data
        updateWeather();
    }

    // Code to call and run task to pull Weather data from API
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String units = prefs.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_default));
        String location = prefs.getString(getString(R.string.pref_loc_key), getString(R.string.pref_loc_default));

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(units, location);
    }



    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private void getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // Get a database to use to store cats
            WeatherDbHelper weatherDbHelper = new WeatherDbHelper(MainActivity.this);

            // Empty old data
            weatherDbHelper.deleteAllWeather();


            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_LOCATION = "city";
            final String OWM_ID_KEY = "id";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            //Additional future items
            //final String OWM_DESCRIPTION = "description";
            //final String OWM_ICON = "icon";
            final String OWM_SHORT_DESC = "main";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_PRESSURE = "pressure";
            final String OWM_WIND_SPEED = "speed";
            final String OWM_DEGREES = "deg";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


            JSONObject locationObject = forecastJson.getJSONObject(OWM_LOCATION);
            String location = locationObject.getString(OWM_ID_KEY);


            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();




            for (int i = 0; i < weatherArray.length(); i++) {
                int id;
                String shortDesc;
                String date;
                String max;
                String min;
                String humidity;
                String pressure;
                String wind_speed;
                String degrees;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Convert date from long into readable form
                long dateTime;
                // Convert this to UTC time
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                date = getReadableDateString(dateTime);

                // Temperatures are in a child object called "temp".
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                max = temperatureObject.getString(OWM_MAX);
                min = temperatureObject.getString(OWM_MIN);

                // description is in a child array called "weather", which is 1 element long.
                JSONArray weatherObject = dayForecast.getJSONArray(OWM_WEATHER);
                shortDesc = weatherObject.getJSONObject(0).getString(OWM_SHORT_DESC);

                JSONArray idObject = dayForecast.getJSONArray(OWM_WEATHER);
                id = weatherObject.getJSONObject(0).getInt(OWM_ID_KEY);

                //Get Pressure object in list array
                //JSONObject pressureObject = dayForecast.getJSONObject(OWM_PRESSURE);
                pressure = dayForecast.getString(OWM_PRESSURE);

                //Get humidity object in list array
                //JSONObject humidityObject = dayForecast.getJSONObject(OWM_HUMIDITY);
                humidity = dayForecast.getString(OWM_HUMIDITY);

                //Get degrees object in list array
                //JSONObject degreeObject = dayForecast.getJSONObject(OWM_DEGREES);
                degrees = dayForecast.getString(OWM_DEGREES);

                //Get the wind speed object in list array
                //JSONObject windSpeedObject = dayForecast.getJSONObject(OWM_WIND_SPEED);
                wind_speed = dayForecast.getString(OWM_WIND_SPEED);


                //highAndLow = formatHighLows(high, low);

                weatherDbHelper.addWeatherEntry(id, location, shortDesc, date, max, min, humidity, pressure, wind_speed, degrees);
            }


        }

        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.

            if(params.length == 0)
            {
                return null;
            }



            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = params[0];
            String location = params[1];
            int numDays = 7;

            try {

                // Construct the URL for the API query
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+ location +",US&mode="+ format +"&units="+ units +"&cnt=7&appid=9aff5b4fa9ba169db4ca8af0087507d8";
                //44842


                URL url = new URL(FORECAST_BASE_URL);

                Log.v(LOG_TAG, "Built URI " + FORECAST_BASE_URL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                //TODO: Create a loop to process trough all the reader's data and fill the buffer
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString(); //TODO: Fill str with JSON str

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

/*        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mWeatherAdapter.clear();
                for(String dayForecastStr : result) {
                    mWeatherAdapter.add(dayForecastStr);
                }

                // New data is back from the server.  Hooray!
            }
        }*/
    }

}

