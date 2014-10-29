package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;


public class MapActivity extends Activity {

    private GoogleMap mMap;
    protected List<ParseObject> mLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        query.include(ParseConstants.KEY_USER);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locations, ParseException e) {
                mLocations = locations;
                int i = 0;
                ParseGeoPoint[] coordinats = new ParseGeoPoint[mLocations.size()];
                String[] usernames = new String[mLocations.size()];
                Date[] dates = new Date[mLocations.size()];
                long now = new Date().getTime();
                for (ParseObject location : mLocations){
                    usernames[i] = location.getParseUser(ParseConstants.KEY_USER).getUsername();
                    coordinats[i] = location.getParseGeoPoint(ParseConstants.KEY_COORDINATES);
                    dates[i] = location.getUpdatedAt();
                    String convertedDate = DateUtils.getRelativeTimeSpanString(dates[i].getTime(),now,DateUtils.SECOND_IN_MILLIS).toString();
                    double longitude = coordinats[i].getLongitude();
                    double latitude = coordinats[i].getLatitude();
                    LatLng position = new LatLng(longitude, latitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                            .title(usernames[i] + " was here " + convertedDate));
                    i++;
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
