package com.example.messageapp.messageapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by billaros on 30/9/2014.
 */
public class FriendsFragment extends ListFragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected String[] senderObjectId;
    protected String[] usernames;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends,
                container, false);

        //updateUserStatus(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserList();


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.i(TAG, senderObjectId[position]);
        Intent friendsIntent = new Intent(getActivity(),ChatBoxActivity.class);
        friendsIntent.putExtra(ParseConstants.KEY_SENDER_ID, usernames[position]);
        startActivity(friendsIntent);
    }

    private void loadUserList(){

        final ProgressDialog dia = ProgressDialog.show(this.getActivity(), null,
                getString(R.string.alert_loading));
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        getActivity().setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                dia.dismiss();
                if ( e == null) {
                    //if(friends.size() == 0) Toast.makeText(this,R.string.msg_no_user_found,Toast.LENGTH_SHORT).show();
                    mFriends = friends;
                    usernames = new String[mFriends.size()];
                    senderObjectId = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        senderObjectId[i] = user.getObjectId();
                        i++;
                    }

                    if (getListView().getAdapter() == null) {
                        UserListAdapter adapter = new UserListAdapter(getListView().getContext(), mFriends);
                        setListAdapter(adapter);
                    } else {
                        //refill
                        ((UserListAdapter) getListView().getAdapter()).refill(mFriends);
                    }
                }else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage() )
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

//    private void updateUserStatus(boolean online)
//    {
//        mCurrentUser.put("online", online);
//        mCurrentUser.saveEventually();
//    }




}
