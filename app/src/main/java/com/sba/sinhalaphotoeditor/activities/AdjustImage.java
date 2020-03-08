package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.Config.BlurUtils;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import render.animations.*;

public class AdjustImage extends AppCompatActivity {

    ImageView adjustImage;

    TickSeekBar listner1,listener2;
    float blurValue = 0.0f;
    float contrastValue = 0.0f;

    float preblurValue = 0.0f;
    float precontrastValue = 0.0f;

    Bitmap filterAddingBitmap = null;
    Bitmap currentEditingImage = null;
    Bitmap blurAddedImage = null;
    Bitmap contrastAddedImage = null;

    ProgressDialog dialog;

    DatabaseHelper helper = new DatabaseHelper(AdjustImage.this);

    Render render;

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
                new RunInBackground().execute();
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




        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();

        methods = new Methods(getApplicationContext());


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));


        render = new Render(AdjustImage.this);

        dialog = new ProgressDialog(AdjustImage.this);

        adjustImage = (ImageView) findViewById(R.id.adjustImage);
        listner1 = (TickSeekBar) findViewById(R.id.listener);
        listener2 = (TickSeekBar) findViewById(R.id.listener2);


        ImageView topGreenPannel;
        topGreenPannel = findViewById(R.id.topGreenPannel);
        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);


        filterAddingBitmap = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(),true);



        adjustImage.setImageBitmap(filterAddingBitmap);

        render.setAnimation(Flip.InX(adjustImage));
        render.start();



        listner1.setOnSeekChangeListener(new OnSeekChangeListener()
        {
            @Override
            public void onSeeking(SeekParams seekParams)
            {
                preblurValue = blurValue;
                blurValue = seekParams.progressFloat;

                if(contrastValue > 0)
                {
                    if(precontrastValue > contrastValue)
                    {
                        contrastAddedImage = filterAddingBitmap;
                    }
                    blurAddedImage = new BlurUtils().blur(AdjustImage.this,contrastAddedImage, seekParams.progressFloat);
                }
                else
                {
                    blurAddedImage = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, seekParams.progressFloat);
                }

                currentEditingImage = blurAddedImage;
                adjustImage.setImageBitmap(currentEditingImage);
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
            }

        });

        listener2.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams)
            {

                precontrastValue = contrastValue;
                contrastValue = seekParams.progressFloat;
                if(blurValue > 0)
                {
                    if(preblurValue > blurValue)
                    {
                        blurAddedImage = filterAddingBitmap;
                    }
                    contrastAddedImage = methods.addSaturation(blurAddedImage,contrastValue);
                }
                else
                {
                    contrastAddedImage = methods.addSaturation(filterAddingBitmap,contrastValue);
                }

                currentEditingImage = contrastAddedImage;
                //adjustImage.setImageBitmap(currentEditingImage);
                Glide.with(getApplicationContext()).load(currentEditingImage).into(adjustImage);
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

            }
        });










    }
    public class RunInBackground extends AsyncTask<Void,Void,Void>
    {
        Bitmap bitmap;


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Loading..");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //dialog.dismiss();

            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    //get Date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    if(helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime))
                    {
                        bitmap = null;
                    }
                    MainActivity.deleteUndoRedoImages();

                    GlideBitmapPool.clearMemory();



                }
            });



        }

        @Override
        protected Void doInBackground(Void... voids)
        {


            if(blurValue > 0 && contrastValue > 0)
            {
                bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                bitmap = methods.addSaturation(bitmap,contrastValue);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e) {

                    }
                }

                //MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
               // MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }
            else if(blurValue > 0)
            {
                bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                //bitmap = changeBitmapContrastBrightness(bitmap,contrastValue,0);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e) {

                    }
                }

                //MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
                //MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }
            else if(contrastValue > 0)
            {
                //Bitmap bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                bitmap = methods.addSaturation(filterAddingBitmap,contrastValue);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e)
                    {
                        Log.d("Error",e.getMessage());
                    }
                }

               // MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
               // MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }


            Intent intent = new Intent();
            setResult(11,intent);
            finish();


            return null;
        }
    }
}
