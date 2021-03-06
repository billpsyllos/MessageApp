package com.example.messageapp.messageapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected Button mSignUpButton;
    protected Button mFacebookButton;
    protected Button mTwitterButton;
    public static final String TAG = LoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        isMobileDataEnabled();
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mTwitterButton = (Button)findViewById(R.id.twitterButton);
        mTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithTwitter();

            }
        });

        final List<String> permissions = Arrays.asList("public_profile", "user_friends", "user_about_me",
                "user_relationships", "user_birthday", "user_location");
        mFacebookButton = (Button)findViewById(R.id.facebookbutton);
        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithFacebook(permissions);
            }
        });


        mSignUpButton = (Button) findViewById(R.id.signupButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUp();
            }
        });

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
    }

    private void logInWithTwitter() {
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    navigateToMain();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                } else {
                    navigateToMain();
                    Log.d("MyApp", "User logged in through Twitter!");

                }
            }
        });
    }

    private void logInWithFacebook(List<String> permissions) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    navigateToMain();
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    navigateToMain();
                }
            }
        });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void logIn() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        username = username.trim();
        password = password.trim();

        if(username.isEmpty() || password.isEmpty() ){
            Utils.showDialog(this, R.string.err_fields_empty);
            return;
        }else{
            //Login
            final ProgressDialog dia = ProgressDialog.show(this, null,
                    getString(R.string.alert_wait));
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (e == null) {
                        //Success!
                        MessageApplication.updateParseInstallation(user);
                        dia.dismiss();
                        navigateToMain();
                    } else {
                        dia.dismiss();
                        Utils.showDialog(
                                LoginActivity.this,
                                getString(R.string.err_login) + " "
                                        + e.getMessage());
                        e.printStackTrace();

                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    protected void navigateToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void isMobileDataEnabled(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(TAG, "Wifi connected: " + isWifiConn);
        Log.d(TAG, "Mobile connected: " + isMobileConn);
        if(isMobileConn == false && isWifiConn == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(R.string.network_not_found_title);
            builder.setMessage(R.string.network_not_found_context);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            //builder.setNegativeButton(R.string.no, null);
            builder.create().show();
            return;
        }
    }


}
