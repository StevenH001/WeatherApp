package edu.flsouthern.shancock.weatherapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
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
            String shortDesc = intent.getStringExtra("WEATHER_SHORT_DESC");
            String max = intent.getStringExtra("WEATHER_MAX_TEMP");
            String min = intent.getStringExtra("WEATHER_MIN_TEMP");
            String humidity = intent.getStringExtra("WEATHER_HUMIDITY");
            String pressure = intent.getStringExtra("WEATHER_PRESSURE");
            int windSpeed = intent.getIntExtra("WEATHER_WIND_SPEED", 0);
            int degrees = intent.getIntExtra("WEATHER_DEGREES", 0);
            int weatherId = intent.getIntExtra("WEATHER_ID", 0);

            //Round the numbers to look nice
            max = Math.round(Double.parseDouble(max)) + "";
            min = Math.round(Double.parseDouble(min)) + "";
            pressure = Math.round(Double.parseDouble(pressure)) + "";

            //Get the formatted wind speed and direction from Utility.java and set the text view
            ((TextView) findViewById(R.id.detail_windSpeed_textview)).setText("Wind:            " + Utility.getFormattedWind(getApplicationContext(),degrees,windSpeed));

            //Load data into view
            //For now, format data based on orientation in here. Edit later to create cleaner stlye.
            ((TextView) findViewById(R.id.detail_date_textview)).setText(date);
            ((TextView) findViewById(R.id.detail_shortDesc_textview)).setText(shortDesc);
            ((TextView) findViewById(R.id.detail_max_textview)).setText(max);
            ((TextView) findViewById(R.id.detail_min_textview)).setText(min);
            ((TextView) findViewById(R.id.detail_humidity_textview)).setText("Humidity:     " + humidity + "%");
            ((TextView) findViewById(R.id.detail_pressure_textview)).setText("Pressure:     " + pressure + " hpa");
            ((ImageView) findViewById(R.id.detail_icon)).setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        }
    }
}
