package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.RegisterScreen;
import com.sba.sinhalaphotoeditor.adapters.WalkthroughPagerAdapter;

import java.util.ArrayList;

import static com.sba.sinhalaphotoeditor.Config.Constants.IS_WALKTHROUGH_NEEDED_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_ENGLISH;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_POSITION_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.Config.Constants.SHARED_PREF_NAME;

public class WalkThroughActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private WalkthroughPagerAdapter walkthroughPagerAdapter;

    private ArrayList<Drawable> image1 = new ArrayList<android.graphics.drawable.Drawable>();
    private ArrayList<Drawable> image2 = new ArrayList<Drawable>();
    private ArrayList<Drawable> image3 = new ArrayList<Drawable>();


    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();


    private Button next,previous;
    private boolean isAnimate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);


        Methods.freeUpMemory();


        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);






        //image1.add(getResources().getDrawable(R.drawable.addimage));
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);

       // image3.add(getResources().getDrawable(R.drawable.addfromimages));
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);


        //image2.add(getResources().getDrawable(R.drawable.createbitmap));
        image2.add(getResources().getDrawable(R.drawable.addtext));
        image2.add(getResources().getDrawable(R.drawable.addimage));
        image2.add(getResources().getDrawable(R.drawable.addsticker));
        image2.add(getResources().getDrawable(R.drawable.addeffect));
        image2.add(getResources().getDrawable(R.drawable.addblur));
        image2.add(getResources().getDrawable(R.drawable.addcrop));

        image1.add(getResources().getDrawable(R.drawable.undo));
        image2.add(getResources().getDrawable(R.drawable.saveimage));
        image3.add(getResources().getDrawable(R.drawable.redo));

//        titles.add(getResources().getString(R.string.title1));
//        description.add(getResources().getString(R.string.desc1));


        titles.add(getResources().getString(R.string.title2));
        description.add(getResources().getString(R.string.desc2));


        titles.add(getResources().getString(R.string.title3));
        description.add(getResources().getString(R.string.desc3));

        titles.add(getResources().getString(R.string.title4));
        description.add(getResources().getString(R.string.desc4));


        titles.add(getResources().getString(R.string.title5));
        description.add(getResources().getString(R.string.desc5));

        titles.add(getResources().getString(R.string.title6));
        description.add(getResources().getString(R.string.desc6));


        titles.add(getResources().getString(R.string.title7));
        description.add(getResources().getString(R.string.desc7));


        titles.add(getResources().getString(R.string.title8));
        description.add(getResources().getString(R.string.desc8));

        previous.setVisibility(View.GONE);

        viewPager = findViewById(R.id.viewPager);
        walkthroughPagerAdapter = new WalkthroughPagerAdapter(getSupportFragmentManager(),image1,image2,image3,titles,description);
        viewPager.setAdapter(walkthroughPagerAdapter);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(next.getText().equals(getResources().getString(R.string.finish_text)))
                {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(IS_WALKTHROUGH_NEEDED_KEY,true);
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), RegisterScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,true);

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1,true);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                previous.setVisibility(View.VISIBLE);

                if (position == (viewPager.getAdapter().getCount())-1)
                {
                    next.setText(getResources().getString(R.string.finish_text));
                }
                else
                {
                    next.setText(getResources().getString(R.string.next_text));
                }

                if (position == 0)
                {
                    TranslateAnimation animation = new TranslateAnimation(0,-Methods.getDeviceWidthInPX(getApplicationContext()),0,0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    previous.startAnimation(animation);

                    isAnimate = true;
                }
                else
                {
                    if(isAnimate)
                    {
                        TranslateAnimation animation = new TranslateAnimation(-Methods.getDeviceWidthInPX(getApplicationContext()),0,0,0);
                        animation.setDuration(500);
                        animation.setFillAfter(true);
                        previous.startAnimation(animation);

                        isAnimate = false;
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


        changeTypeFace();



    }
    private void changeTypeFace()
    {
        Typeface typeface = Methods.getDefaultTypeFace(getApplicationContext());

        next.setTypeface(typeface);
        previous.setTypeface(typeface);



    }
}
