package com.example.messageapp.messageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by billaros on 30/9/2014.
 */
public class PostsFragment extends ListFragment {

    public static final String TAG = PostsFragment.class.getSimpleName();

    protected   List<ParseObject> mPosts;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts,
                container, false);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);

        //ParseObject obj = ParseObject.createWithoutData("Posts","User");
        ParseQuery<ParseObject> gameQuery = ParseQuery.getQuery(ParseConstants.CLASS_POSTS);
        gameQuery.include(ParseConstants.KEY_POSTS_CREATED_BY);
        gameQuery.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        gameQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                if ( e == null) {
                    mPosts = posts;
                    String usernames[] = new String[mPosts.size()];
                    String text[] = new String[mPosts.size()];
                    int i = 0;
                    for (ParseObject post : mPosts) {
                        text[i] = post.getString(ParseConstants.KEY_POSTS_TEXT);
                        usernames[i] = post.getParseUser(ParseConstants.KEY_POSTS_CREATED_BY).getUsername().toString();
                        i++;
                        PostsAdapter postsAdapter = new PostsAdapter(getListView().getContext(), mPosts);
                        setListAdapter(postsAdapter);
                       // Log.i(TAG, usernames[i].toString());
                    }
                }
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);



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
}
