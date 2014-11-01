package com.example.messageapp.messageapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class ResetPasswordActivity extends Activity {

    public static final String TAG = ResetPasswordActivity.class.getSimpleName();

    protected EditText mEmail ;
    protected Button mRessetPasswordButton;
    private List<ParseUser> mEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getActionBar();
        actionBar.hide();



        mEmail = (EditText)findViewById(R.id.reset_email_text);
        mRessetPasswordButton = (Button)findViewById(R.id.resetbutton);
        mRessetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                if (email.isEmpty()) {
                    emailError();
                } else if (isEmailValid(email) == false) {
                    emailError();
                } else {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo(ParseConstants.KEY_EMAIL, email);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> emails, ParseException e) {
                            if (e == null) {
                                mEmails = emails;
                                if (mEmails.size() == 1) {
                                    Log.d(TAG, "User exists make reset");
                                    Intent newPasswordIntent = new Intent(ResetPasswordActivity.this,NewPasswordActivity.class);
                                    for(ParseUser user : mEmails){
                                        newPasswordIntent.putExtra(ParseConstants.KEY_OBJECT_ID,user.getObjectId().toString());
                                    }
                                    newPasswordIntent.putExtra(ParseConstants.KEY_EMAIL,mEmail.getText().toString());

                                    startActivity(newPasswordIntent);
                                    /*for(ParseObject email: mEmails) {
                                        String username = email.getParseUser(ParseConstants.KEY_USERNAME).getUsername();
                                        String password = email.getParseUser(ParseConstants.KEY_USER).get(ParseConstants.KEY_PASSWORD).toString();

                                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                                            @Override
                                            public void done(ParseUser user, ParseException e) {
                                                setProgressBarIndeterminateVisibility(false);
                                                if (e == null) {
                                                    //Success!
                                                    Intent newPasswordIntent = new Intent(ResetPasswordActivity.this,NewPasswordActivity.class);
                                                    startActivity(newPasswordIntent);
                                                } else {
                                                    Log.d(TAG,"errorors");
                                                }
                                            }
                                        });
                                    }*/
                                } else {
                                    Log.d(TAG, "User does not exist");
                                    AlertDialog.Builder locationBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                    locationBuilder.setTitle(getString(R.string.invalide_email_error_title));
                                    locationBuilder.setMessage(getString(R.string.error_not_email_exists_message));
                                    locationBuilder.setNegativeButton(R.string.Ok, null);
                                    locationBuilder.create().show();
                                }
                            } else {
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void emailError(){
        AlertDialog.Builder locationBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);
        locationBuilder.setTitle(getString(R.string.invalide_email_error_title));
        locationBuilder.setMessage(getString(R.string.error_invalid_email_message));
        locationBuilder.setNegativeButton(R.string.Ok, null);
        locationBuilder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.resset_password, menu);
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
