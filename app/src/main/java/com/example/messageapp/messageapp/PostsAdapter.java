package com.example.messageapp.messageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by billaros on 8/10/2014.
 */
public class PostsAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mPostos;


    public PostsAdapter(Context context, List<ParseObject> posts){
        super(context,R.layout.posts_item, posts);
        mContext = context ;
        mPostos = posts;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.posts_item, null);
            holder = new ViewHolder();
            holder.postsText = (TextView) convertView.findViewById(R.id.postText);
            holder.authorLabel = (TextView) convertView.findViewById(R.id.authorLabel);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = mPostos.get(position);


        holder.postsText.setText(message.getString(ParseConstants.KEY_POSTS_TEXT));
        holder.authorLabel.setText(message.getParseUser(ParseConstants.KEY_POSTS_CREATED_BY).getUsername().toString());

        return convertView;
    }

    private static class ViewHolder{
        TextView postsText;
        TextView authorLabel;

    }

    public void refill(List<ParseObject> messages){
        mPostos.clear();
        mPostos.addAll(messages);
        notifyDataSetChanged();
    }

}
