package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import static com.sba.sinhalaphotoeditor.Config.Constants.FIREBASE_PAYLOAD_MESSAGE_TEXT;
import static com.sba.sinhalaphotoeditor.Config.Constants.FIREBASE_PAYLOAD_TITLE_TEXT;

public class SplashScreen extends AppCompatActivity
{
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        helper = new DatabaseHelper(getApplicationContext());
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
