package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PostActivity extends Activity {

    // UI references.
    private EditText postEditText;
    private TextView characterCountTextView;
    private Button postButton;
    private int maxCharacterCount;
    private ParseGeoPoint geoPoint;

    public static final String TAG = PostActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postEditText = (EditText) findViewById(R.id.post_edit_text);

        characterCountTextView = (TextView) findViewById(R.id.character_count_textview);
        postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseObject posts = new ParseObject(ParseConstants.CLASS_POSTS);
                posts.put(ParseConstants.KEY_POSTS_TEXT,postEditText.getText().toString());
                posts.put(ParseConstants.KEY_POSTS_CREATED_BY,ParseUser.getCurrentUser());
                try {
                    posts.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post, menu);
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
