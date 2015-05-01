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
            //holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.userLabelId);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        String user = mUsers.get(position).getUsername();
        holder.nameLabel.setText(user);




        return convertView;
    }

    private static class ViewHolder{
        TextView nameLabel;
        ParseUser user;

    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
