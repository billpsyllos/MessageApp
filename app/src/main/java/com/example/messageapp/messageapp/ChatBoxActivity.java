package com.example.messageapp.messageapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseInstallation;
import com.parse.ParseQuery;


public class ChatBoxActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        EditText pushdText = (EditText) findViewById(R.id.pushText);
        Button sendPush = (Button) findViewById(R.id.sendButton);
        sendPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPushNotifications();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_box, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void sendPushNotifications(){

//        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
//        //query.whereEqualTo(ParseConstants.KEY_USER_ID,"")
//
//        //query
    }
}
