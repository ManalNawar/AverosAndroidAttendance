package com.averos.als.positioningdemo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {



    //constants
    String userEmail;
    String userName;
    List<User> users;
    PublicClientApplication sampleApp = LoginActivity.sampleApp;

    //views
    TextView useremail;
    TextView username;
    Button logout;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    SwitchCompat switchc;

    boolean isDarkMode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statuscolor));
        }

        //toolbar
        toolbar = findViewById(R.id.main_toolbar);
        //toolbar back button color change
        Drawable backbt = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        backbt.setColorFilter(getResources().getColor(R.color.textcolor), PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setHomeAsUpIndicator(backbt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // get user name and email from SharedPrefManager
        userEmail = SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
        userName = SharedPrefManager.getInstance(getApplicationContext()).getUser().getName();

        //setting up user name and email in text view
        username = findViewById(R.id.username);
        username.setText("Name: "+userName);
        useremail = findViewById(R.id.useremail);
        useremail.setText("Email: "+userEmail);

        switchc = findViewById(R.id.darkmode);


        // check the theme mode saved in shared preference.
        isDarkMode = SharedPrefManager.getInstance(getApplicationContext()).isDarkMode();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchc.setChecked(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchc.setChecked(false);
        }

        switchc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {

                if (ischecked) {

                    // switch to dark theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    // save theme to shared preferences
                    SharedPrefManager.getInstance(getApplicationContext()).setDarkthemeMode(true);
                }
                else {
                    // switch to light theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // save theme to shared preferences
                    SharedPrefManager.getInstance(getApplicationContext()).setDarkthemeMode(false);
                }
            }
        });

        builder = new AlertDialog.Builder(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog();
            }
        });
    }
    public void Dialog(){

        //set title and message of dialog
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        // dialog actions on YES and NO
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        logout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"same page",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void logout(){
        System.out.print("performing logout???????");
        try {
            users = sampleApp.getUsers();

            if (users == null) {
                /* We have no users */

            } else if (users.size() == 1) {
                /* We have 1 user */
                /* Remove from token cache */
                sampleApp.remove(users.get(0));
                // remove save user from shared perfrence
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                // updateSignedOutUI();
                startActivity(new Intent(this,LoginActivity.class));

            }
            else {
                /* We have multiple users */
                for (int i = 0; i < users.size(); i++) {
                    sampleApp.remove(users.get(i));
                }
            }

//            Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
//                    .show();

        } catch (MsalClientException e) {
            System.out.print( "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            System.out.print("User at this position does not exist: " + e.toString());
        }
    }

    public void DarkModeOn(){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
    public void DarkModeOff(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
