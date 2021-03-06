package com.example.messageapp.messageapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener, GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int CHOOSE_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB
    protected List<ParseObject> mLocations;
    protected double pLong;
    protected double pLat;

    protected Uri mMediaUri ;
    protected ParseUser mUser;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected static ParseRelation<ParseUser> mCurrentUserRelation;
    protected static ParseUser mCurrentUser;
    protected static ParseUser mToFriend;



    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case 0: //Take Picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri ==null){
                        Toast.makeText(MainActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                    }
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    break;
                case 1: //Take Video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri ==null){
                        Toast.makeText(MainActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                    }else{
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2://Choose an existing picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,CHOOSE_PHOTO_REQUEST);
                    break;
                case 3://Choose an existing video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this,getString(R.string.video_warning),Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
                    break;
            }

        }
    };

    private Uri getOutPutMediaFileUri(int mediaType) {
        //To be safe, you should check that the sdcard is mounted
        //using Enviroment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()){

            String app_name = getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), app_name);

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_"+ timeStamp + ".jpg");
            } else if(mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_"+ timeStamp + ".mp4");
            } else {
                return null;
            }

            Log.d(TAG, "File: " +  Uri.fromFile(mediaFile) );
            return Uri.fromFile(mediaFile);

        }else{
            return null;
        }



    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        //ParseAnalytics.trackAppOpened(getIntent());
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        }else if (ParseFacebookUtils.isLinked(currentUser)) {
            facebookMakeMeRequest();
            Log.i(TAG,"facebookUser is logged in");
        }else if(ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())){
            twitterMakeRequest();
            Log.i(TAG,"TwitterUser is logged in");
        }
        else {
            Log.i(TAG, currentUser.getUsername());
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        checkIfLocationsIsEnable();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        updateFriendRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // add it to the Gallery
            Log.i(TAG,"resultCode: " + resultCode);
            if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == CHOOSE_VIDEO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else {
                    //we are passing that Uri back using getData()
                    mMediaUri = data.getData();
                }
                Log.i(TAG,"Media Uri: " + mMediaUri);
                if(requestCode == CHOOSE_VIDEO_REQUEST){
                    //Make sure the file is less than 10 MB
                    int fileSize = 0;
                    InputStream inputStream = null ;
                    try {
                        //input stream is used to stream information from the file bbb(Byte by Byte)
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    }finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(fileSize >= FILE_SIZE_LIMIT){
                        Log.i(TAG,"fileSize: " + fileSize);
                        Toast.makeText(this, getString(R.string.error_file_size_too_large), Toast.LENGTH_LONG).show();
                        return;
                    }

                }
            }

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

            Intent recipientsIntent = new Intent(this,RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            String fileType;
            if(requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
                fileType = ParseConstants.TYPE_IMAGE;
            }else{
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);
            startActivity(recipientsIntent);

        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void facebookMakeMeRequest() {
        //String accessToken = FacebookSdk.getClientToken();
        GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (user != null) {
                    //profilePictureView.setProfileId(user.optString("id"));
                    final String firstName = user.optString("first_name");
                    ParseUser mUser = ParseUser.getCurrentUser();
                    mUser.put(ParseConstants.KEY_USERNAME, firstName);
                    mUser.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            // TODO Auto-generated method stub
                            if (e == null) {
                                Log.d(TAG,"Facebook Username updated successfully ==== " + firstName);
                            } else {
                                Log.d(TAG,"Error");
                            }
                        }
                    });
                } else if (response.getError() != null) {
                    // handle error
                    Log.d(TAG,"Erroradasdas");
                }
            }
        }).executeAsync();
    }

    private void twitterMakeRequest() {
        ParseUser mUser = ParseUser.getCurrentUser();
        mUser.put(ParseConstants.KEY_USERNAME, ParseTwitterUtils.getTwitter().getScreenName());
        mUser.saveInBackground(new SaveCallback() {
            public void done(com.parse.ParseException e) {
                // TODO Auto-generated method stub
                if (e == null) {
                    Log.d(TAG,"Twitter Username updated successfully");
                } else {
                    Log.d(TAG,"Error");
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.action_logout:
                mGoogleApiClient.disconnect();
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_edit_profil:
                Intent profilePageIntent = new Intent(this, ProfilePageActivity.class);
                profilePageIntent.putExtra(ParseConstants.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());
                startActivity(profilePageIntent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.action_add_friends:
                Intent editFrinedsIntent = new Intent(this,EditFriendsActivity.class);
                startActivity(editFrinedsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Location services connected.");
        //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services disconnected.");
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {

        final double pLong = location.getLongitude();
        final double pLat = location.getLatitude();
        final ParseGeoPoint point = new ParseGeoPoint(pLong, pLat);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_LOCATION);
        query.include(ParseConstants.KEY_USER);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        try {
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> locations, ParseException ee) {
                    mLocations = locations;
                    String[] usernames = new String[mLocations.size()];
                    String[] objectId = new String[mLocations.size()];
                    int i = 0;
                    if (locations.size() == 0) {
                        ParseObject locationObject = new ParseObject(ParseConstants.CLASS_LOCATION);
                        locationObject.put(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
                        locationObject.put(ParseConstants.KEY_COORDINATES, point);
                        try {
                            locationObject.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "Insert first time geolocations for current user");
                    } else if (locations.size() == 1) {
                        for (ParseObject location : mLocations) {
                            usernames[i] = location.getParseUser(ParseConstants.KEY_USER).getUsername();
                            objectId[i] = location.getObjectId();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_LOCATION);
                            query.getInBackground(objectId[i], new GetCallback<ParseObject>() {
                                public void done(ParseObject update, ParseException e) {
                                    if (e == null) {
                                        update.put(ParseConstants.KEY_COORDINATES, point);
                                        update.saveInBackground();
                                        Log.i(TAG, "Location coordinates updated successfully");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG,"RunTime Exception",e);
        }
    }

    public void checkIfLocationsIsEnable(){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.gps_not_found_title);  // GPS not found
            builder.setMessage(R.string.gps_not_found_message); // Want to enable?
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.create().show();
            return;
        }
    }

    public void updateFriendRequests(){
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_FRIEND_REQUESTS);
        query.whereEqualTo(ParseConstants.KEY_FROM_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(ParseConstants.KEY_REQUEST_STATUS_FROM_USER, "pending");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> friendRequests, ParseException e) {
                if(friendRequests.size() != 0){
                    for (ParseObject request : friendRequests) {
                        mToFriend = request.getParseUser(ParseConstants.KEY_TO_USER);
                        mCurrentUserRelation.add(mToFriend);
                        mCurrentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("TAG", e.getMessage());

                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("We have a friendship!!!!")
                                            .setPositiveButton("See user", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // FIRE ZE MISSILES!
                                                    Intent friendIntent = new Intent(MainActivity.this,FriendProfileActivity.class);
                                                    friendIntent.putExtra(ParseConstants.KEY_OBJECT_ID, mToFriend.getObjectId().toString());
                                                    //friendIntent.putExtra(ParseConstants.KEY_USERNAME, mToFriend.getEmail());
                                                    startActivity(friendIntent);
                                                }
                                            }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    }).show();
                                }
                            }
                        });

                        request.put(ParseConstants.KEY_REQUEST_STATUS_FROM_USER,"success");
                        request.saveEventually();

                    }
                }
            }
        });
    }
}
