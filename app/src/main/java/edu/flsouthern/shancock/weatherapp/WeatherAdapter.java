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
    public WeatherAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // This is here to do any data formatting and massaging we might need
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_loc_key = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);

        // Pull data from cursor
        String name = cursor.getString(idx_loc_key);

        // SKIP FOR NOW, assemble data in meaningful way

        return name;
    }

    // Remember that these views are reused as needed.
    //Creates the empty basic view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    // This is where we fill-in the views with the contents of the cursor.
    //Adds the data and information into the view that newView just created.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
