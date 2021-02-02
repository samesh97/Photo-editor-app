package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.StickerViewPagerAdapter;
import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.util.Locale;

import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class PickStickerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager sticker_view_pager;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_sticker);

        init();
//
//
        addNewTab();
        addNewTab();
        addNewTab();
        addNewTab();
        addNewTab();
//        addNewTab();
//        addNewTab();
//        addNewTab();
//        addNewTab();
//        addNewTab();
//
        setData();



    }
    private void init()
    {
        tabLayout = findViewById(R.id.tabLayout);
        sticker_view_pager = findViewById(R.id.sticker_view_pager);
    }
    private void setData()
    {
       // StickerViewPagerAdapter stickerViewPagerAdapter = new StickerViewPagerAdapter(getSupportFragmentManager(),5);
       // sticker_view_pager.setAdapter(stickerViewPagerAdapter);
       // tabLayout.setupWithViewPager(sticker_view_pager);
    }
    private void addNewTab()
    {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setIcon(getDrawable(R.drawable.addblur));
        tabLayout.addTab(tab);
    }


}