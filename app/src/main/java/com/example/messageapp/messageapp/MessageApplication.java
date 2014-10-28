package com.example.messageapp.messageapp;

import android.app.Application;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;


/**
 * Created by billaros on 25/9/2014.
 */
public class MessageApplication extends Application {

    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;
    private static SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "wdfoNc9o8Me7cPD0xtHEfkZH41mbHIXflPxeaY0h", "mM3kS9oOqplrGM7WwX0tWyfpp71hRcHoq2Am0S8d");


    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

}
