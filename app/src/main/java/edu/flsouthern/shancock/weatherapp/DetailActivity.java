package edu.flsouthern.shancock.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // The detail Activity called via intent.  Inspect the intent for weather id.
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("WEATHER_DATE")) {

            // Retrieve information from the intent
            String date = intent.getStringExtra("WEATHER_DATE");
            String location = intent.getStringExtra("WEATHER_LOC_KEY");
            String shortDesc = intent.getStringExtra("WEATHER_SHORT_DESC");
            String max = intent.getStringExtra("WEATHER_MAX_TEMP");
            String min = intent.getStringExtra("WEATHER_MIN_TEMP");
            String humidity = intent.getStringExtra("WEATHER_HUMIDITY");
            String pressure = intent.getStringExtra("WEATHER_PRESSURE");
            String windSpeed = intent.getStringExtra("WEATHER_WIND_SPEED");
            String degrees = intent.getStringExtra("WEATHER_DEGREES");

            //Load data into view
            //For now, format data based on orientation in here. Edit later to create cleaner stlye.
            ((TextView) findViewById(R.id.detail_date_textview)).setText(date);
            ((TextView) findViewById(R.id.detail_loc_textview)).setText("Location ID:       " + location);
            ((TextView) findViewById(R.id.detail_shortDesc_textview)).setText("Cloudiness:        " + shortDesc);
            ((TextView) findViewById(R.id.detail_max_textview)).setText("Max Temp:         " + max);
            ((TextView) findViewById(R.id.detail_min_textview)).setText("Min Temp:          " + min);
            ((TextView) findViewById(R.id.detail_humidity_textview)).setText("Humidity:            " + humidity + " %");
            ((TextView) findViewById(R.id.detail_pressure_textview)).setText("Pressure:            " + pressure + " hpa");
            ((TextView) findViewById(R.id.detail_windSpeed_textview)).setText("Wind Speed:       " + windSpeed + " m/s");
            ((TextView) findViewById(R.id.detail_degrees_textview)).setText("Direction:            " + degrees + " Degrees");

        }



    }
}
