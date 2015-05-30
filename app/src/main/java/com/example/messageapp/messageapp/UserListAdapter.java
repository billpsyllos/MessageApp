package com.example.messageapp.messageapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by billaros on 8/10/2014.
 */
public class UserListAdapter extends ArrayAdapter<ParseUser> {

    //public static RowSet user;
    protected Context mContext;
    protected List<ParseUser> mUsers;
    protected List<ParseObject> mPictureProfiles;

    public UserListAdapter(Context context, List<ParseUser> users){
        super(context,R.layout.chat_item, users);
        mContext = context ;
        mUsers = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.userLabelId);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser ob = mUsers.get(position);
        //ParseUser objectId = mUsers.getParseUser(ParseConstants.KEY_USER);
        String user = mUsers.get(position).getUsername();
        holder.nameLabel.setText(user);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
        query.whereEqualTo(ParseConstants.KEY_USER, ob);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> images, ParseException e) {
                mPictureProfiles = images;
                if ( images == null) {
                    holder.userImageView.setImageResource(R.drawable.default_profile_picture);
                }else{
                    for (ParseObject profilePicture : mPictureProfiles) {
                        ParseFile fileObject = (ParseFile) profilePicture.get(ParseConstants.KEY_PROFILE_PICTURE);
                        Uri fileUri = Uri.parse(fileObject.getUrl());
                        Picasso.with(mContext).load(fileUri.toString()).into(holder.userImageView);
                    }
                }
            }
        });

        try {
            holder.userImageView.setImageResource(R.drawable.default_profile_picture);

        }catch (Exception e){
            Log.e("Profile","RunTime Exception",e);
        }
        //holder.nameLabel.setText(user.getParseUser(ParseConstants.KEY_USER).getUsername());




        return convertView;
    }

    private static class ViewHolder{
        TextView nameLabel;
        ParseUser user;
        ImageView userImageView;

    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
