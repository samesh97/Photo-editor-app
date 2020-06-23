package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.database.DatabaseHelper;
import com.sba.sinhalaphotoeditor.aynctask.GalleryImageHandler;

import java.util.Locale;

import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_MESSAGE_TEXT;
import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_TITLE_TEXT;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class SplashScreen extends AppCompatActivity
{


    @Override
    protected void attachBaseContext(Context newBase)
    {
        SharedPreferences pref = newBase.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String localeString = pref.getString(LANGUAGE_KEY,LANGUAGE_SINHALA);
        Locale myLocale = new Locale(localeString);
        Locale.setDefault(myLocale);
        Configuration config = newBase.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(myLocale);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                Context newContext = newBase.createConfigurationContext(config);
                super.attachBaseContext(newContext);
                return;
            }
        } else {
            config.locale = myLocale;
        }
        super.attachBaseContext(newBase);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Methods.freeUpMemory();

        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        helper.deleteUnnessaryImages();


        final ImageView splash_logo = findViewById(R.id.splash_logo);
        animateViewUpFromBottom(splash_logo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                animateViewUpFromMiddle(splash_logo);
            }
        },1500);


        splash_logo.animate().alphaBy(0).alpha(1).translationYBy(-200).translationY(0).setDuration(1500);
    }
    private Intent configIntentForPushNotitfication()
    {
        Intent intent = getIntent();
        Intent newIntent;

        if(intent != null && intent.hasExtra(FIREBASE_PAYLOAD_TITLE_TEXT) && intent.hasExtra(FIREBASE_PAYLOAD_MESSAGE_TEXT))
        {
            newIntent = new Intent(SplashScreen.this,NotificationView.class);

            if(intent.getExtras() != null)
            {

                String message = intent.getStringExtra(FIREBASE_PAYLOAD_MESSAGE_TEXT);
                String title = intent.getStringExtra(FIREBASE_PAYLOAD_TITLE_TEXT);

                if(message != null && title != null && !message.equals("") && !title.equals("") && !message.trim().equals("") && !title.trim().equals(""))
                {
                    newIntent.putExtra(FIREBASE_PAYLOAD_TITLE_TEXT,title);
                    newIntent.putExtra(FIREBASE_PAYLOAD_MESSAGE_TEXT,message);
                }
            }

        }
        else
        {
             newIntent = new Intent(SplashScreen.this,MainActivity.class);
        }
        return newIntent;
    }
    public void animateViewUpFromBottom(View view)
    {
        TranslateAnimation animation = new TranslateAnimation(0,0,Methods.getDeviceHeightInDP(getApplicationContext()),0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.setAnimation(animation);
    }
    public void animateViewUpFromMiddle(View view)
    {
        TranslateAnimation animation = new TranslateAnimation(0,0,0,-Methods.getDeviceHeightInDP(getApplicationContext()));
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                Intent intent = configIntentForPushNotitfication();
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_start_animation,R.anim.activity_exit_animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
