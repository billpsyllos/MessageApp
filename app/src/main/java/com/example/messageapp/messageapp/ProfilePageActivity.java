package com.example.messageapp.messageapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ProfilePageActivity extends Activity {

    public static final String TAG = ProfilePageActivity.class.getSimpleName();
    protected Uri mMediaUri ;
    protected Uri mTakePicture;
    protected String mFileType = "image";
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static int result = 0;
    protected List<ParseObject> mProfile;
    protected List<ParseObject> mPictureProfiles;
    public static Drawable drawable;
    static boolean activeResults = false;
    protected ImageView mProfileView;


    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case 0: //Take Picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri ==null){
                        Toast.makeText(ProfilePageActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                    }
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    break;
                case 1://Choose an existing picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,CHOOSE_PHOTO_REQUEST);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mProfileView = (ImageView) findViewById(R.id.defaultProfilePicture);
        profileViewClickListener();
        drawable = this.getResources().getDrawable(R.drawable.default_profile_picture);

        String currentUsername = getIntent().getExtras().getString(ParseConstants.KEY_USERNAME);
        EditText usernameView = (EditText) findViewById(R.id.edit_profile_name);
        usernameView.setText(currentUsername);

        Button save = (Button) findViewById(R.id.profile_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activeResults == false) checkIfUserHasProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            activeResults = true;
            Log.i(TAG,"resultCode: " + resultCode);
            if (requestCode == CHOOSE_PHOTO_REQUEST  ) {
                if (data == null) {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }else {
                    //we are passing that Uri back using getData()
                    mMediaUri = data.getData();
                    mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

                }
                Log.i(TAG, "Media Uri: " + mMediaUri + " - File Type:  " + mFileType);
            }else if(requestCode == TAKE_PHOTO_REQUEST){
                mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
                Log.i(TAG, "Media Uri: " + mMediaUri + " - File Type:  " + mFileType);
            }
            ImageView profileView = (ImageView) findViewById(R.id.defaultProfilePicture);
            profileView.setImageURI(Uri.parse(String.valueOf(mMediaUri)));
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_page, menu);
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

    private Uri getOutPutMediaFileUri(int mediaType) {
        //To be safe, you should check that the sdcard is mounted
        //using Enviroment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()){

            String app_name = getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), app_name);

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_"+ timeStamp + ".jpg");
            } else {
                return null;
            }

            Log.d(TAG, "File: " +  Uri.fromFile(mediaFile) );
            return Uri.fromFile(mediaFile);

        }else{
            return null;
        }



    }
    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    private void checkIfUserHasProfile(){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
        query.include(ParseConstants.KEY_USER);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> profilePictures, ParseException e) {
                mPictureProfiles = profilePictures;
                String[] picturesProfilesId = new String[mPictureProfiles.size()];
                int i = 0;
                if (mPictureProfiles.size() == 0) {

                    mProfileView.setImageDrawable(drawable);
                    profileViewClickListener();
                    Log.d(TAG,"User has not profile result===== " + result );
                }else if(mPictureProfiles.size() == 1) {
                    for(final ParseObject profilePicture : mPictureProfiles){

                        ParseFile file = profilePicture.getParseFile(ParseConstants.KEY_PROFILE_PICTURE);
                        Uri fileUri = Uri.parse(file.getUrl());
                        Picasso.with(ProfilePageActivity.this).load(fileUri.toString()).into(mProfileView);
                        profileViewClickListener();


//                        picturesProfilesId[i] = profilePicture.getObjectId();
//                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
//                        query.getInBackground(picturesProfilesId[i], new GetCallback<ParseObject>() {
//                            @Override
//                            public void done(ParseObject object, ParseException e) {
//                                ParseFile fileObject = (ParseFile) object.get(ParseConstants.KEY_PROFILE_PICTURE);
//                                fileObject.getDataInBackground(new GetDataCallback() {
//                                    @Override
//                                    public void done(byte[] bytes, ParseException e) {
//                                        if(e == null){
//                                            Log.d(TAG,"We've got data in data");
//                                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                                            ImageView profileView = (ImageView) findViewById(R.id.defaultProfilePicture);
//                                            profileView.setImageBitmap(bmp);
//                                            profileView.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageActivity.this);
//                                                    builder.setItems(R.array.profile_camera_choices, mDialogListener);
//                                                    AlertDialog dialog = builder.create();
//                                                    dialog.show();
//                                                }
//                                            });
//                                        }else {
//                                            Log.d(TAG,"There was a problem downloading the data.");
//
//                                        }
//
//                                    }
//                                });
//
//                            }
//                        });
                    }
                    Log.d(TAG,"User has profile result===== " + result );
                }
            }
        });

    }

    private void profileViewClickListener() {
        mProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageActivity.this);
                builder.setItems(R.array.profile_camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void updateUserProfile(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_USER_PROFILE);
        query.include(ParseConstants.KEY_USER);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> profiles, ParseException e) {
                mProfile = profiles ;
                String[] objectId = new String[mProfile.size()];
                int i = 0;
                if (profiles.size() == 0) {
                    ParseObject profileObject = new ParseObject(ParseConstants.CLASS_USER_PROFILE);
                    profileObject.put(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
                    profileObject.put(ParseConstants.KEY_PROFILE_PICTURE, uploadFile(mMediaUri));
                    try {
                        profileObject.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    Toast.makeText(ProfilePageActivity.this, getString(R.string.profile_page_update_success),Toast.LENGTH_LONG).show();
                    
                    Log.i(TAG, "Create New user profile");
                }else if (profiles.size() == 1) {
                    for (ParseObject profile : mProfile) {
                        objectId[i] = profile.getObjectId();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_PROFILE);
                        query.getInBackground(objectId[i], new GetCallback<ParseObject>() {
                            public void done(ParseObject update, ParseException e) {
                                if (e == null) {
                                    update.put(ParseConstants.KEY_PROFILE_PICTURE, uploadFile(mMediaUri));
                                    update.saveInBackground();
                                    Log.i(TAG, "UserProfile updated successfully");
                                    Toast.makeText(ProfilePageActivity.this, getString(R.string.profile_page_update_success),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                }
            }
        });


    }

    private ParseFile uploadFile(Uri mediaUri){
        byte[] fileBytes = FileHelper.getByteArrayFromFileProfile(this, mediaUri);
        if (fileBytes == null){
            return null;
        }else{
           // if(mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            //}
            String fileName = FileHelper.getFileNameProfile(this, mediaUri, "image");
            ParseFile file = new ParseFile(fileName,fileBytes);
            return file;
        }
    }



}
