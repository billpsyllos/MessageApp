package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by billaros on 30/9/2014.
 */
public class PostsFragment extends ListFragment implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
       {

    public static final String TAG = PostsFragment.class.getSimpleName();

    protected   List<ParseObject> mPosts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts,
                container, false);

        return rootView;


    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);

        //ParseObject obj = ParseObject.createWithoutData("Posts","User");
        ParseQuery<ParseObject> gameQuery = ParseQuery.getQuery(ParseConstants.CLASS_POSTS);
        gameQuery.include(ParseConstants.KEY_POSTS_CREATED_BY);
        gameQuery.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        gameQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if ( e == null) {
                    mPosts = posts;
                    String usernames[] = new String[mPosts.size()];
                    String text[] = new String[mPosts.size()];
                    int i = 0;
                    for (ParseObject post : mPosts) {
                        text[i] = post.getString(ParseConstants.KEY_POSTS_TEXT);
                        usernames[i] = post.getParseUser(ParseConstants.KEY_POSTS_CREATED_BY).getUsername().toString();
                        i++;
                        PostsAdapter postsAdapter = new PostsAdapter(getListView().getContext(), mPosts);
                        setListAdapter(postsAdapter);
                    }
                }
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        //Toast.makeText(InboxFragment.this, getString(R.string.success_message_create), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
