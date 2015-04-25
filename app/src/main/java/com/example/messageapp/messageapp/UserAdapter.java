package com.example.messageapp.messageapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
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
public class UserAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mUsers;
    protected List<ParseObject> mPictureProfiles;

    public UserAdapter(Context context, List<ParseObject> users){
        super(context,R.layout.user_item, users);
        mContext = context ;
        mUsers = users;
        //this.mPicasso = Picasso.with(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject user = mUsers.get(position);
        ParseUser objectId = user.getParseUser(ParseConstants.KEY_USER);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
        query.whereEqualTo(ParseConstants.KEY_USER, objectId);
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
        holder.nameLabel.setText(user.getParseUser(ParseConstants.KEY_USER).getUsername());


        return convertView;
    }

    private static class ViewHolder{
        ImageView userImageView;
        TextView nameLabel;

    }

    public void refill(List<ParseObject> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
