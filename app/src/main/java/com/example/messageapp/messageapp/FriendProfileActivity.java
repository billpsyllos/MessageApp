package com.example.messageapp.messageapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FriendProfileActivity extends Activity {

    protected List<ParseObject> mPictureProfiles;
    protected ImageView mProfileView;
    protected Button mChatBtn;
    protected Button mMapBtn;
    protected String username;
    protected String userId;
    protected ParseUser mfriend;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mCurrentUserRelation;
    protected ParseRelation<ParseUser> mFriendUserRelation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        mProfileView = (ImageView) findViewById(R.id.defaultFriendProfilePicture);
        mChatBtn = (Button) findViewById(R.id.chatButton);
        mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToChat();
            }
        });
        mChatBtn.setVisibility(View.INVISIBLE);

        mMapBtn = (Button) findViewById(R.id.mapButton);
        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMap();
            }
        });
        mMapBtn.setVisibility(View.INVISIBLE);

        userId = getIntent().getStringExtra(ParseConstants.KEY_OBJECT_ID);
        username = getIntent().getExtras().getString(ParseConstants.KEY_USERNAME);



        ParseUser.getQuery().whereEqualTo(ParseConstants.KEY_OBJECT_ID,userId)
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> parseUsers, ParseException e) {
                        if (parseUsers.size() == 0){

                        }else{
                            for (ParseUser user : parseUsers){
                                mfriend = user;
                                mFriendUserRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                                ParseQuery<ParseUser> query = mFriendUserRelation.getQuery();
                                query.whereEqualTo(ParseConstants.KEY_USER,mfriend);
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> parseUsers, ParseException e) {
                                        if(parseUsers.size() == 0){
                                            mChatBtn.setVisibility(View.VISIBLE);
                                            mMapBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                updateFriendProfile(mfriend);
                                checkFriendIfExistFriendRequest();
                            }
                        }
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_profile, menu);
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

    protected void navigateToChat(){
        Intent friendsIntent = new Intent(FriendProfileActivity.this,ChatBoxActivity.class);
        friendsIntent.putExtra(ParseConstants.KEY_SENDER_ID, username);
        startActivity(friendsIntent);
    }

    protected void navigateToMap(){
        Intent chatIntent = new Intent(FriendProfileActivity.this,MapActivity.class);
        chatIntent.putExtra(ParseConstants.KEY_OBJECT_ID, userId);
        startActivity(chatIntent);
    }

    protected void checkFriendIfExistFriendRequest(){

        ParseQuery<ParseObject> friendRequestQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_FRIEND_REQUESTS);
        friendRequestQuery.whereEqualTo(ParseConstants.KEY_FROM_USER, mfriend);
        friendRequestQuery.whereEqualTo(ParseConstants.KEY_TO_USER,ParseUser.getCurrentUser());
        //friendRequestQuery.whereEqualTo(ParseConstants.KEY_FRIEND_REQUEST_STATUS,"pending");

        friendRequestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> requests, ParseException e) {
                if(requests.size() == 0){
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_FRIEND_REQUESTS);
                    query.whereEqualTo(ParseConstants.KEY_FROM_USER, ParseUser.getCurrentUser());
                    query.whereEqualTo(ParseConstants.KEY_TO_USER,mfriend);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> obj, ParseException e) {
                            if(obj.size() == 0){
                                Log.d("TAG", "Not Exists Request");
                                ParseObject sendRequest = new ParseObject(ParseConstants.CLASS_FRIEND_REQUESTS);
                                sendRequest.put(ParseConstants.KEY_FROM_USER, ParseUser.getCurrentUser());
                                sendRequest.put(ParseConstants.KEY_TO_USER, mfriend);
                                sendRequest.put(ParseConstants.KEY_REQUEST_STATUS_FROM_USER, "pending");
                                sendRequest.put(ParseConstants.KEY_REQUEST_STATUS_TO_USER, "pending");
                                try {
                                    sendRequest.save();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }else{
                                Log.i("TAG", "Nothing");
                            }
                        }
                    });
                }else if(requests.size() == 1){
                    for (ParseObject request : requests) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_FRIEND_REQUESTS);
                        query.getInBackground(request.getObjectId(), new GetCallback<ParseObject>() {
                            public void done(ParseObject update, ParseException e) {
                                if (e == null) {
                                    String toUser = update.getParseUser(ParseConstants.KEY_TO_USER).getObjectId();

                                    if(toUser.equals(ParseUser.getCurrentUser().getObjectId())){
                                        update.put(ParseConstants.KEY_REQUEST_STATUS_TO_USER, "success");
                                        update.saveInBackground();

                                        mCurrentUserRelation.add(mfriend);
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e != null) {
                                                    Log.e("TAG", e.getMessage());
                                                }
                                            }
                                        });

                                        sendFriendSuccessNotification(mfriend.getObjectId());
                                        Log.i("TAG", "We have friendship");
                                    }else{
                                        Log.i("TAG", "Nothing");
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    protected void updateFriendProfile(ParseUser user){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
        query.include(ParseConstants.KEY_USER);
        query.whereEqualTo(ParseConstants.KEY_USER,user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> profiles, ParseException e) {
                mPictureProfiles = profiles;
                if (profiles.size()==0){
                    Log.d("TAG", "no profiles");
                }else{


                    for (ParseObject profilePicture : mPictureProfiles) {
                        ParseFile fileObject = (ParseFile) profilePicture.get(ParseConstants.KEY_PROFILE_PICTURE);
                        Uri fileUri = Uri.parse(fileObject.getUrl());
                        Picasso.with(FriendProfileActivity.this).load(fileUri.toString()).into(mProfileView);
                        mChatBtn.setText("Chat with " + profilePicture.getParseUser(ParseConstants.KEY_USER).getUsername());
                        mMapBtn.setText("See where is " + profilePicture.getParseUser(ParseConstants.KEY_USER).getUsername());
                    }
                }
            }
        });

    }

    protected void sendFriendSuccessNotification(String recipientId){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, recipientId);

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage("You have a friend Request");

        push.sendInBackground();
    }

}
