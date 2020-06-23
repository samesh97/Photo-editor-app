package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.callbacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.config.BlurUtils;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.database.DatabaseHelper;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.singleton.ImageList;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import render.animations.*;

import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class AdjustImage extends AppCompatActivity implements OnAsyncTaskState {

    private ImageView adjustImage;
    private float blurValue = 0.0f;
    private float saturationValue = 0.0f;
    private float brightnessValue = 0.0f;

    Bitmap currentEditingImage = null;

    private ProgressDialog dialog;
    private DatabaseHelper helper = new DatabaseHelper(AdjustImage.this);
    private Methods methods;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }
        else
        {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_on_photo_menu, menu);
        return true;
    }
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.setImage)
        {
            if (mInterstitialAd.isLoaded())
            {
                mInterstitialAd.show();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }
            else
            {
                if(saturationValue > 0 || blurValue > 0 || brightnessValue > 0)
                {
                    dialog.setMessage("Loading..");
                    dialog.setCancelable(false);
                    dialog.show();
                    AddImageToArrayListAsyncTask task = new AddImageToArrayListAsyncTask(currentEditingImage,this);
                    task.execute();
                }
                else
                {
                    startActivityForResult();
                }

            }

        }
        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_image);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3538783908730049/5147080745");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());




        Methods.freeUpMemory();

        methods = new Methods(getApplicationContext());
        Render render = new Render(AdjustImage.this);
        dialog = new ProgressDialog(AdjustImage.this);

        adjustImage = findViewById(R.id.adjustImage);

        TickSeekBar listener1 =  findViewById(R.id.listener);
        TickSeekBar listener2 = findViewById(R.id.listener2);
        TickSeekBar listener3 = findViewById(R.id.listener3);



        createImage();
        render.setAnimation(Flip.InX(adjustImage));
        render.start();



        listener1.setOnSeekChangeListener(new OnSeekChangeListener()
        {
            @Override
            public void onSeeking(SeekParams seekParams)
            { }
            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar)
            {
                blurValue = seekBar.getProgress();
                createImage();
            }

        });

        listener2.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams)
            { }
            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

                saturationValue = seekBar.getProgress();
                createImage();
            }
        });

        listener3.setOnSeekChangeListener(new OnSeekChangeListener()
        {
            @Override
            public void onSeeking(SeekParams seekParams)
            { }
            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar)
            {
                brightnessValue = seekBar.getProgress();
                createImage();
            }

        });


    }

    private void createImage()
    {

        Log.d("SaturationValue"," " + saturationValue);
        currentEditingImage = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(),true);
        if(blurValue > 0)
        {
            currentEditingImage = new BlurUtils().blur(AdjustImage.this,currentEditingImage, blurValue);
        }
        if(saturationValue > 0)
        {
            currentEditingImage = methods.addSaturation(currentEditingImage,saturationValue);
        }
        if(brightnessValue > 0)
        {
            currentEditingImage = methods.changeBitmapContrastBrightness(currentEditingImage,1f,brightnessValue);
        }


        Glide.with(getApplicationContext()).load(currentEditingImage).into(adjustImage);
    }

    @Override
    public void startActivityForResult()
    {
        dialog.hide();
        dialog.cancel();

        if(saturationValue > 0 || blurValue > 0 || brightnessValue > 0)
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    //get Date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    String path = Methods.saveToInternalStorage(getApplicationContext(),ImageList.getInstance().getCurrentBitmap(),currentDateandTime);

                    if(helper.AddImage(null,path))
                        ImageList.getInstance().deleteUndoRedoImages();


                }
            });
        }

        Intent intent = new Intent();
        setResult(11,intent);
        finish();
    }
}
