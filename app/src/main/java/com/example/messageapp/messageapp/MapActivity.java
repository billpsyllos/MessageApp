package com.example.messageapp.messageapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
    protected String mFriendLocationId;
    protected LatLng mFriendPosition;
    protected LatLng mCurrentUserPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFriendLocationId = getIntent().getStringExtra(ParseConstants.KEY_OBJECT_ID);

        //mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        query.include(ParseConstants.KEY_USER);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locations, ParseException e) {
                mLocations = locations;
                int i = 0;
                ParseGeoPoint[] coordinats = new ParseGeoPoint[mLocations.size()];
                String[] usernames = new String[mLocations.size()];
                Date[] dates = new Date[mLocations.size()];
                //long now = new Date().getTime();
                for (ParseObject location : mLocations){
                    usernames[i] = location.getParseUser(ParseConstants.KEY_USER).getUsername();
                    coordinats[i] = location.getParseGeoPoint(ParseConstants.KEY_COORDINATES);
                    dates[i] = location.getUpdatedAt();
                    //String convertedDate = DateUtils.getRelativeTimeSpanString(dates[i].getTime(),now, DateUtils.SECOND_IN_MILLIS).toString();
                    double longitude = coordinats[i].getLongitude();
                    double latitude = coordinats[i].getLatitude();
                    mCurrentUserPosition = new LatLng(longitude, latitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(mCurrentUserPosition)
                            .title(usernames[i]).visible(true));
                    i++;
                }
            }
        });



        ParseQuery<ParseObject> friendQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        friendQuery.include(ParseConstants.KEY_USER);
        friendQuery.getInBackground(mFriendLocationId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                ParseGeoPoint coordinates = object.getParseGeoPoint(ParseConstants.KEY_COORDINATES);
                double lat = coordinates.getLatitude();
                double longitude = coordinates.getLongitude();
                mFriendPosition = new LatLng(longitude, lat);
                Marker marker = mMap.addMarker(new MarkerOptions().position(mFriendPosition).title("You").visible(true));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(mFriendPosition).zoom(18.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);

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
