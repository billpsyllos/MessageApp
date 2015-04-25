package com.example.messageapp.messageapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FindFriendsFragment extends Fragment {

    public static final String TAG = FindFriendsFragment.class.getSimpleName();
    protected GridView mGridView;
    protected List<ParseObject> mLocations;
    protected List<ParseObject> mCurrentUserLocation;
    protected String[] senderObjectId;
    protected double currentUserlong;
    protected double currentUserlat;
    protected double guestLat;
    protected double guestLong;
    protected ParseRelation<ParseUser> mPicturesRelation;
    protected ParseUser mCurrentUser;
    protected String[] userIds;
    protected String[] usernames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_friends,
                container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);


        //get current user coordinates
        ParseQuery<ParseObject> userquery = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        userquery.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        userquery.include(ParseConstants.KEY_USER);
        userquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> currentLocation, ParseException e) {
                if(e == null) {
                    mCurrentUserLocation = currentLocation;
                    for (ParseObject location : mCurrentUserLocation) {
                        //String name = location.getParseUser(ParseConstants.KEY_USER).getUsername();
                        currentUserlong = location.getParseGeoPoint(ParseConstants.KEY_COORDINATES).getLongitude();
                        currentUserlat = location.getParseGeoPoint(ParseConstants.KEY_COORDINATES).getLatitude();
                    }
                }
            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        query.whereNotEqualTo(ParseConstants.KEY_USER,ParseUser.getCurrentUser());
        query.include(ParseConstants.KEY_USER);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locations, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                mLocations = locations;
                if(locations != null && e == null) {
                    int i = 0;
                    ParseGeoPoint[] coordinats = new ParseGeoPoint[mLocations.size()];
                    usernames = new String[mLocations.size()];
                    userIds = new String[mLocations.size()];

                    for (ParseObject location : mLocations) {
                        coordinats[i] = location.getParseGeoPoint(ParseConstants.KEY_COORDINATES);
                        guestLong = coordinats[i].getLongitude();
                        guestLat = coordinats[i].getLatitude();
                        float dist = calculateDistance(currentUserlat, currentUserlong, guestLat, guestLong);
                        if (dist < 1) {
                            usernames[i] = location.getParseUser(ParseConstants.KEY_USER).getUsername();
                            userIds[i] = location.getParseUser(ParseConstants.KEY_USER).getObjectId();
                            Log.d(TAG, "Distance between current user and " + usernames[i] + " == " + String.valueOf(dist) + "Km");
                        }
                        i++;

                    }
                    if(mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mLocations);
                        mGridView.setAdapter(adapter);
                    }else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mLocations);
                    }
                }
            }
        });

    }

    public static float calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c)/1000;

        return dist;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
         //   ImageView userImageView = (ImageView)view.findViewById(R.id.userImageView);
            Intent chatIntent = new Intent(getActivity(),ChatBoxActivity.class);
            chatIntent.putExtra(ParseConstants.KEY_RECIPIENTS_ID, userIds[position]);
            chatIntent.putExtra(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
            startActivity(chatIntent);
        }
    };


}
