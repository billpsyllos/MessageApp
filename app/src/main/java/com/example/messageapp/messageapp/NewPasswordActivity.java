package com.example.messageapp.messageapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class NewPasswordActivity extends Activity {

    protected EditText mNewPassword;
    protected EditText mRepeatPassword;
    protected Button   mUpdateButton;
    public static final String TAG = NewPasswordActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        final String intentEmail = intent.getStringExtra(ParseConstants.KEY_EMAIL);
        final String intentObjectId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        mNewPassword = (EditText)findViewById(R.id.editTextNewPassword);
        mRepeatPassword = (EditText)findViewById(R.id.editTextNewPasswordRepeat);
        mUpdateButton = (Button)findViewById(R.id.updatePasswordbutton);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newPass = mNewPassword.getText().toString();
                String repeatPass = mRepeatPassword.getText().toString();
                if (!newPass.equals(repeatPass)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(NewPasswordActivity.this);
                    alert.setTitle(getString(R.string.invalid_password_error_title)); // GPS not found
                    alert.setMessage(getString(R.string.invalid_password_message_error)); // Want to enable?
                    alert.setNegativeButton(R.string.Ok, null);
                    alert.create().show();
                    return;
                } else {
                    //if exist make update password
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    //query.whereEqualTo(ParseConstants.KEY_EMAIL,intentEmail);
                    query.getInBackground(intentObjectId,new GetCallback<ParseUser>() {

                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if(e==null){
                                parseUser.setPassword(newPass.toString());
                                parseUser.increment(ParseConstants.KEY_PASSWORD);
                                parseUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if( e == null){
                                            Log.d(TAG,"finded it make update new password is ==== " + newPass);
                                            Intent loginIntent = new Intent(NewPasswordActivity.this,LoginActivity.class);
                                            startActivity(loginIntent);
                                        }else{
                                            Log.d(TAG,e.getMessage());
                                        }
                                    }
                                });

                            }else{
                                Log.d(TAG,"error e!=null ");
                            }
                        }
                    });


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_password, menu);
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
