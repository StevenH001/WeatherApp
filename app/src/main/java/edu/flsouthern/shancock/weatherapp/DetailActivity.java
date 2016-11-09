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
            String location = intent.getStringExtra("WEATHER_LOC_KEY");
            String date = intent.getStringExtra("WEATHER_DATE");
            String shortDesc = intent.getStringExtra("WEATHER_SHORT_DESC");
            String max = intent.getStringExtra("WEATHER_MAX_TEMP");
            String min = intent.getStringExtra("WEATHER_MIN_TEMP");
            String humidity = intent.getStringExtra("WEATHER_HUMIDITY");
            String pressure = intent.getStringExtra("WEATHER_PRESSURE");
            String windSpeed = intent.getStringExtra("WEATHER_WIND_SPEED");
            String degrees = intent.getStringExtra("WEATHER_DEGREES");

            // For now hack it all into a single string to display
            // Later use nice layout and styling

            String details = date + "\n\n";
            details += "Short Description: " + shortDesc + "\n";
            details += "Location: " + location + "\n";
            details += "Max Temp: " + max + "\n";
            details += "Min Temp: " + min + "\n";
            details += "Humidity: " + humidity + "\n";
            details += "Pressure: " + pressure + "\n";
            details += "Wind Speed: " + windSpeed + "\n";
            details += "Degrees: " + degrees + "\n";

            // Load data into view
            ((TextView) findViewById(R.id.detail_text))
                    .setText(details);
        }



    }
}
