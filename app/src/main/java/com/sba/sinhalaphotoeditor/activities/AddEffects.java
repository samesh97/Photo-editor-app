package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.sba.sinhalaphotoeditor.adapter.FilterAdapter;
import com.sba.sinhalaphotoeditor.singleton.ImageList;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            super.onBackPressed();
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
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if(filterAdapter != null)
                {
                    if(filterAdapter.returnPrevoiusPositin() != -99)
                    {
                        AddImageToArrayListAsyncTask asyncTask = new AddImageToArrayListAsyncTask(currentEditingBitmap, progress_bar, this);
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




        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));
        }


        GlideBitmapPool.clearMemory();


        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();

        render = new Render(AddEffects.this);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



    }
    private void init()
    {
        userSelectedImage =  findViewById(R.id.userSelectedImage);
        progress_bar = findViewById(R.id.progress_bar);
    }

    @Override
    public void bitmapChanged(Bitmap bitmap)
    {
        currentEditingBitmap = bitmap;
        Glide.with(getApplicationContext()).load(currentEditingBitmap).into(userSelectedImage);
    }

    @Override
    public void startActivityForResult()
    {
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
            helper.AddImage(helper.getBytes(ImageList.getInstance().getCurrentBitmap()),currentDateandTime);

            ImageList.getInstance().deleteUndoRedoImages();


        }
        });
    }
}
