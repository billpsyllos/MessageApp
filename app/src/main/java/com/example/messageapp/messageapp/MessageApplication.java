package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.util.List;


/**
 * Created by billaros on 25/9/2014.
 */
public class MessageApplication extends Application {


    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";
    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;
    private static SharedPreferences preferences;


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "wdfoNc9o8Me7cPD0xtHEfkZH41mbHIXflPxeaY0h", "mM3kS9oOqplrGM7WwX0tWyfpp71hRcHoq2Am0S8d");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
        ParseTwitterUtils.initialize("WEG8zu3hpG0qLn0JkYDJ4nlBZ", "lh6vM4Rq6UR6BTi4LED6X0kSDIlyaWI9EjhT4ukDbepsI00xfQ");

        

    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }


    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
        installation.saveInBackground();
    }




}
