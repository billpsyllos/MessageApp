package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class FriendMessagesActivity extends ListActivity {

    protected List<ParseObject> mMessages;

    public static final String TAG = FriendMessagesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_inbox);

        //TextView descriptionView = (TextView) findViewById(R.id.senderDescriptionView);
    }

    @Override
    public void onResume(){
        super.onResume();
        //getActivity().setProgressBarIndeterminateVisibility(true);



        String objectId = getIntent().getExtras().getString(ParseConstants.KEY_OBJECT_ID);
        Log.i(TAG, objectId);


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, objectId);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
          //      getActivity().setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    //success
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    String[] descriptions = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        descriptions[i] = message.getString(ParseConstants.KEY_DESCRIPTION);
                        i++;
                        //Log.i(TAG,"usernames == " + descriptions[i]);
                    }
                    if(getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);

                        setListAdapter(adapter);
                    }else{
                        //refill
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }

                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message =  mMessages.get(position);
        String descriptionLabel = message.getString(ParseConstants.KEY_DESCRIPTION);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            //view image
            Intent intent = new Intent(FriendMessagesActivity.this, ViewImageActivity.class);
            intent.setData(fileUri);
            //intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_DESCRIPTION, descriptionLabel);
            startActivity(intent);
        }else{
            //View video
            Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        //Delete it!
        /*
        List <String> ids = message.getList(ParseConstants.KEY_RECIPIENTS_ID);

        if(ids.size() == 1){
            //last recipient - delete
            message.deleteInBackground();
        }else{
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToremove = new ArrayList<String>();
            idsToremove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(ParseConstants.KEY_RECIPIENTS_ID,idsToremove);
            message.saveInBackground();

        }
        */




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_messages, menu);
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
