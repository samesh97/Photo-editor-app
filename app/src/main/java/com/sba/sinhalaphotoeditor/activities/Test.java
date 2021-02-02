package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.StickerViewPagerAdapter;
import com.sba.sinhalaphotoeditor.callbacks.OnFirebaseStickerChangedListener;
import com.sba.sinhalaphotoeditor.custom.views.ImageViewPlus;
import com.sba.sinhalaphotoeditor.database.StickerDatabase;
import com.sba.sinhalaphotoeditor.model.FirebaseStickerCategory;

import java.util.ArrayList;

public class Test extends AppCompatActivity {

    private TabLayout lay_tab;
    private ViewPager sticker_view_pager;
    private StickerViewPagerAdapter pagerAdapter;
    private ArrayList<FirebaseStickerCategory> categoryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initViews();
        setData();

        StickerDatabase.sync(new OnFirebaseStickerChangedListener() {
            @Override
            public void onCategoryChanged(FirebaseStickerCategory category)
            {
                addNewTab(category);
            }
        });

    }

    private void initViews()
    {
        lay_tab = findViewById(R.id.lay_tab);
        sticker_view_pager = findViewById(R.id.sticker_view_pager);
    }
    private void setData()
    {
        lay_tab.setupWithViewPager(sticker_view_pager);
        categoryArrayList = new ArrayList<>();
        pagerAdapter = new StickerViewPagerAdapter(getSupportFragmentManager(),categoryArrayList);
        sticker_view_pager.setAdapter(pagerAdapter);
    }
    private void addNewTab(FirebaseStickerCategory category)
    {
        categoryArrayList.add(category);
        pagerAdapter.notifyDataSetChanged();

        lay_tab.removeAllTabs();
        for(FirebaseStickerCategory cat : categoryArrayList)
        {
            lay_tab.addTab(lay_tab.newTab().setText(cat.getName()));
        }
    }

}