package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.internal.ImageDownloader;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class ViewImageActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pd;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        //TextView descriptionView = (TextView) findViewById(R.id.senderDescriptionView);
        imageView = (ImageView)findViewById(R.id.imageView1);

        pd = new ProgressDialog(this);
        pd.setMessage("Downloading Image");

        Uri imageUri = getIntent().getData();
        Log.i(TAG,"Image Uri ==== " + imageUri);
/*        pd.show();
        Picasso.with(this).load(imageUri.toString()).resize(50, 50)
                .into(imageView);
        pd.dismiss();
*/
        //new ImageDownloader().execute(imageUri.toString());
        // Download Image from URL
        InputStream input = null;
        try {
            input = new URL(imageUri.toString()
            ).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Decode Bitmap
        bitmap = BitmapFactory.decodeStream(input);
        imageView.setImageBitmap(bitmap);
        String descriptionText = getIntent().getExtras().getString(ParseConstants.KEY_DESCRIPTION);
        Log.i(TAG, descriptionText);
        //descriptionView.setText(descriptionText);


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


    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {
            // TODO Auto-generated method stub
            //return downloadBitmap(param[0]);
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }



        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
            pd.show();

        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            Log.i("Async-Example", "onPostExecute Called");
//            imageView.setImageBitmap(result);
            pd.dismiss();
            Log.d(TAG, String.valueOf(result));

        }


    }
}
