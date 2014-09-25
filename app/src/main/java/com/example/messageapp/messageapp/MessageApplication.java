package com.example.messageapp.messageapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;


/**
 * Created by billaros on 25/9/2014.
 */
public class MessageApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "wdfoNc9o8Me7cPD0xtHEfkZH41mbHIXflPxeaY0h", "mM3kS9oOqplrGM7WwX0tWyfpp71hRcHoq2Am0S8d");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
