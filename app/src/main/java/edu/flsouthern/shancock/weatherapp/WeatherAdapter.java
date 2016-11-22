package edu.flsouthern.shancock.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.flsouthern.shancock.weatherapp.Data.WeatherContract;

/**
 * Created by Steven Hancock on 11/3/2016.
 */
public class WeatherAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView dateView;
        public final TextView shortDescView;
        public final TextView highView;
        public final TextView lowView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            shortDescView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public WeatherAdapter(Context context, Cursor c, int flags) {super (context, c, flags);}

    // Remember that these views are reused as needed.
    //Creates the empty basic view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        if(cursor.getPosition() == 0) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_forecast_today, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            return view;
        }

        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_forecast, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    // This is where we fill-in the views with the contents of the cursor.
    //Adds the data and information into the view that newView just created.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Find the column numbers in the cursor
        int idx_weather_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_weather_shortDesc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
        int idx_weather_high = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_weather_low = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);

        // Get data to pass to detail activity
        String date = cursor.getString(idx_weather_date);
        String shortDesc = cursor.getString(idx_weather_shortDesc);
        String high = cursor.getString(idx_weather_high);
        String low = cursor.getString(idx_weather_low);

        viewHolder.dateView.setText(date);
        viewHolder.shortDescView.setText(shortDesc);
        viewHolder.highView.setText(Math.round(Double.parseDouble(high)) + "");
        viewHolder.lowView.setText(Math.round(Double.parseDouble(low)) + "");
    }
}
