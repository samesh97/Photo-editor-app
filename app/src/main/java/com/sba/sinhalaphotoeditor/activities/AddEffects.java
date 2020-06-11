package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.CallBacks.OnBitmapChanged;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.adapters.FilterAdapter;
import com.sba.sinhalaphotoeditor.singleton.ImageList;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubfilter;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import render.animations.*;


public class AddEffects extends AppCompatActivity implements OnBitmapChanged, OnAsyncTaskState {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private Bitmap currentEditingBitmap;
    private ImageView userSelectedImage;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private DatabaseHelper helper = new DatabaseHelper(AddEffects.this);
    private List<Filter> filters;
    private Render render;
    private FilterAdapter filterAdapter;
    private RecyclerView filterRecyclerView;
    private Methods methods;
    private ProgressBar progress_bar;

    private ConstraintLayout subFiltersLayout;
    private ImageView expandIcon;

    private Filter currentlyUsedFilter = null;
    private TickSeekBar listener,listener2,listener3;
    private Bitmap subFilterBitmap = null;

    private boolean isAnEffectAdded = false;


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        GlideBitmapPool.clearMemory();
    }

    @Override
    public void onBackPressed()
    {
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else
        {

            if(subFiltersLayout.getVisibility() == View.VISIBLE)
            {
                subFiltersLayout.setVisibility(View.GONE);
            }
            else
            {
                super.onBackPressed();
                overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_on_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.setImage)
        {
            if(mInterstitialAd.isLoaded())
            {
                mInterstitialAd.show();
            }
            else
            {
                progress_bar.setVisibility(View.VISIBLE);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if(filterAdapter != null)
                {
                    if(isAnEffectAdded)
                    {
                        AddImageToArrayListAsyncTask asyncTask;

                        if(subFilterBitmap != null)
                        {
                            asyncTask = new AddImageToArrayListAsyncTask(subFilterBitmap, this);
                        }
                        else
                        {
                            asyncTask = new AddImageToArrayListAsyncTask(currentEditingBitmap, this);
                        }

                        asyncTask.execute();
                    }
                    else
                    {
                        startActivityForResult();
                    }
                }


            }

        }
        return true;

    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_effects);


        init();
        setData();

    }
    private void setData()
    {
        currentEditingBitmap = ImageList.getInstance().getCurrentBitmap();
        methods = new Methods(getApplicationContext());
        methods.setImageViewScaleType(userSelectedImage);
        Glide.with(getApplicationContext()).load(currentEditingBitmap).into(userSelectedImage);

        filters = FilterPack.getFilterPack(AddEffects.this);




        filterRecyclerView = findViewById(R.id.filterRecyclerView);
        filterRecyclerView.setItemViewCacheSize(filters.size());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        filterAdapter = new FilterAdapter(AddEffects.this,filters,this);
        filterRecyclerView.setLayoutManager(manager);
        filterRecyclerView.setAdapter(filterAdapter);




        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3538783908730049/5147080745");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());





        Methods.freeUpMemory();

        render = new Render(AddEffects.this);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(subFiltersLayout.getVisibility() == View.GONE)
                {
                    subFiltersLayout.setVisibility(View.VISIBLE);
                }
                else if(subFiltersLayout.getVisibility() == View.VISIBLE)
                {
                    subFiltersLayout.setVisibility(View.GONE);
                }
            }
        });



    }
    private void init()
    {
        userSelectedImage =  findViewById(R.id.userSelectedImage);
        progress_bar = findViewById(R.id.progress_bar);
        subFiltersLayout = findViewById(R.id.subFiltersLayout);
        expandIcon = findViewById(R.id.expandIcon);
        listener = findViewById(R.id.listener);
        listener2 = findViewById(R.id.listener2);
        listener3 = findViewById(R.id.listener3);

        listener.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar)
            {
                addSubFilter();
            }
        });
        listener2.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

                addSubFilter();
            }
        });
        listener3.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

                addSubFilter();
            }
        });
    }

    private void addSubFilter()
    {


        float brightnessValue = listener.getProgress();
        float vignettetValue = listener2.getProgress();
        float saturationValue = listener3.getProgress();

        //saturation 1 is for default image colors
        if(saturationValue < 1)
        {
            saturationValue = 1;
        }


        subFilterBitmap = currentEditingBitmap.copy(currentEditingBitmap.getConfig(),true);

        Filter filter = new Filter();

        filter.addSubFilter(new BrightnessSubFilter((int) brightnessValue));
        filter.addSubFilter(new VignetteSubfilter(getApplicationContext(), (int) vignettetValue));
        filter.addSubFilter(new SaturationSubfilter(saturationValue));
        subFilterBitmap = filter.processFilter(subFilterBitmap);



        Glide.with(getApplicationContext()).load(subFilterBitmap).into(userSelectedImage);
        isAnEffectAdded = true;

    }

    @Override
    public void bitmapChanged(Bitmap bitmap,Filter filter)
    {

        listener.setProgress(0);
        listener2.setProgress(0);
        listener3.setProgress(0);
        subFilterBitmap = null;
        isAnEffectAdded = true;


        currentEditingBitmap = bitmap;
        Glide.with(getApplicationContext()).load(currentEditingBitmap).into(userSelectedImage);
        currentlyUsedFilter = filter;
    }

    @Override
    public void startActivityForResult()
    {
        progress_bar.setVisibility(View.GONE);
        Intent intent = new Intent();
        setResult(21,intent);
        finish();

        AsyncTask.execute(new Runnable()
        {@Override
        public void run()
        {
            //get Date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            String path = Methods.saveToInternalStorage(getApplicationContext(),ImageList.getInstance().getCurrentBitmap(),currentDateandTime);

            helper.AddImage(null,path);

            ImageList.getInstance().deleteUndoRedoImages();


        }
        });
    }
}
