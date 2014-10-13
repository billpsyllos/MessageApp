package com.example.messageapp.messageapp;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;


public class ViewImageActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        TextView descriptionView = (TextView) findViewById(R.id.senderDescriptionView);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);

        Uri imageUri = getIntent().getData();
        Log.i(TAG,"Image Uri ==== " + imageUri);
        Picasso.with(this).load(imageUri.toString()).into(imageView);

        String descriptionText = getIntent().getExtras().getString(ParseConstants.KEY_DESCRIPTION);
        Log.i(TAG, descriptionText);
        descriptionView.setText(descriptionText);


        /*
        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 10*1000);
        */
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
