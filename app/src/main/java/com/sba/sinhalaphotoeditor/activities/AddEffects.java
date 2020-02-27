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
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import render.animations.*;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddEffects extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private CircleImageView filter1, filter2, filter3, filter4, filter5, filter6, filter7,filter8;
    private CircleImageView filter9, filter10, filter11, filter12, filter13, filter14, filter15,filter16;
    private Bitmap currentEditingBitmap;
    private ArrayList<CircleImageView> images = new ArrayList<>();
    private ImageView userSelectedImage;


    private Bitmap filter1Bitmap,filter2Bitmap,filter3Bitmap,filter4Bitmap,filter5Bitmap,filter6Bitmap,filter7Bitmap,filter8Bitmap = null;
    private Bitmap filter9Bitmap,filter10Bitmap,filter11Bitmap,filter12Bitmap,filter13Bitmap,filter14Bitmap,filter15Bitmap,filter16Bitmap = null;

    private ProgressDialog dialog;
    private ProgressDialog pdLoading;

    private Bitmap addedEffectBitmap = null;

    private ArrayList<Bitmap> allFilterBitmaps = new ArrayList<>();

    private InterstitialAd mInterstitialAd;


    private AdView mAdView;

    private DatabaseHelper helper = new DatabaseHelper(AddEffects.this);

    private List<Filter> filters;

    private String type = null;

    private Render render;


    private Methods methods;








    @Override
    protected void onDestroy()
    {
        new RunInBackground().cancel(true);
        new AsyncCaller().cancel(true);
        super.onDestroy();

        if(dialog != null)
        {
            dialog.dismiss();
        }
        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }

        GlideBitmapPool.clearMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
        }
        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog != null)
        {
            dialog.dismiss();
        }
        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }
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
                new RunInBackground().execute();
            }

        }
        return true;

    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_effects);




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



        methods = new Methods(getApplicationContext());

        GlideBitmapPool.clearMemory();


        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();

        render = new Render(AddEffects.this);
        dialog = new ProgressDialog(AddEffects.this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        pdLoading = new ProgressDialog(AddEffects.this);
        pdLoading.setMessage("Loading..");
        pdLoading.setCancelable(false);
        AsyncCaller caller = new AsyncCaller();





        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //  dialog = new ProgressDialog(AddEffects.this);

        // dialog.setMessage("Loading Filters..");
        // dialog.setCancelable(false);



        init();
        setData();
        onClickListners();








    }
    private void onClickListners()
    {
        filter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 0)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));

                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));

                    }
                }

                render.setAnimation(Bounce.In(filter1));
                render.start();
                type = "0";
                new AsyncCaller().execute();

            }
        });

        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 1)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));

                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter2));
                render.start();

                type = "1";
                //caller.execute();
                new AsyncCaller().execute();

            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 2)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter3));
                render.start();
                type = "2";
                //caller.execute();
                new AsyncCaller().execute();



            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 3)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter4));
                render.start();

                type = "3";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });
        filter5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 4)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter5));
                render.start();
                type = "4";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });
        filter6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 5)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter6));
                render.start();

                type = "5";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });
        filter7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 6)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter7));
                render.start();

                type = "6";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 7)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter8));
                render.start();
                type = "7";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 8)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter9));
                render.start();

                type = "8";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 9)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter10));
                render.start();

                type = "9";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 10)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter11));
                render.start();

                type = "10";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 11)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter12));
                render.start();
                type = "11";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 12)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter13));
                render.start();
                type = "12";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 13)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter14));
                render.start();
                type = "13";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 14)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }

                render.setAnimation(Bounce.In(filter15));
                render.start();
                type = "14";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });

        filter16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                for(int i = 0; i < images.size(); i++)
                {
                    if(i == 15)
                    {
                        images.get(i).setBorderWidth(4);
                        images.get(i).setBorderColor(Color.parseColor("#0ddcc6"));
                    }
                    else
                    {
                        images.get(i).setBorderWidth(3);
                        images.get(i).setBorderColor(Color.parseColor("#FFFFFF"));
                    }
                }
                render.setAnimation(Bounce.In(filter16));
                render.start();
                type = "15";
                //caller.execute();
                new AsyncCaller().execute();


            }
        });
    }

    private void setData()
    {

        images.add(filter1);
        images.add(filter2);
        images.add(filter3);
        images.add(filter4);
        images.add(filter5);
        images.add(filter6);
        images.add(filter7);
        images.add(filter8);
        images.add(filter9);
        images.add(filter10);
        images.add(filter11);
        images.add(filter12);
        images.add(filter13);
        images.add(filter14);
        images.add(filter15);
        images.add(filter16);

        allFilterBitmaps.add(filter1Bitmap);
        allFilterBitmaps.add(filter2Bitmap);
        allFilterBitmaps.add(filter3Bitmap);
        allFilterBitmaps.add(filter4Bitmap);
        allFilterBitmaps.add(filter5Bitmap);
        allFilterBitmaps.add(filter6Bitmap);
        allFilterBitmaps.add(filter7Bitmap);
        allFilterBitmaps.add(filter8Bitmap);
        allFilterBitmaps.add(filter9Bitmap);
        allFilterBitmaps.add(filter10Bitmap);
        allFilterBitmaps.add(filter11Bitmap);
        allFilterBitmaps.add(filter12Bitmap);
        allFilterBitmaps.add(filter13Bitmap);
        allFilterBitmaps.add(filter14Bitmap);
        allFilterBitmaps.add(filter15Bitmap);
        allFilterBitmaps.add(filter16Bitmap);



        currentEditingBitmap = MainActivity.images.get(MainActivity.imagePosition);


       // userSelectedImage.setImageBitmap(currentEditingBitmap);
        methods.setImageViewScaleType(userSelectedImage);
        Glide.with(getApplicationContext()).load(currentEditingBitmap).into(userSelectedImage);




        //render.setAnimation(Flip.InX(userSelectedImage));
        //render.start();

        Bitmap resizedImage = null;

        filters = FilterPack.getFilterPack(AddEffects.this);


        for (int i = 0; i < images.size(); i++)
        {
            resizedImage = methods.getResizedBitmap(currentEditingBitmap,150);
            images.get(i).setImageBitmap(filters.get(i).processFilter(resizedImage));

        }

    }
    private void init()
    {

        filter1 =  findViewById(R.id.filter1);
        filter2 =  findViewById(R.id.filter2);
        filter3 =  findViewById(R.id.filter3);
        filter4 = findViewById(R.id.filter4);
        filter5 = findViewById(R.id.filter5);
        filter6 =  findViewById(R.id.filter6);
        filter7 =  findViewById(R.id.filter7);
        filter8 =  findViewById(R.id.filter8);
        filter9 =  findViewById(R.id.filter9);
        filter10 =  findViewById(R.id.filter10);
        filter11 =  findViewById(R.id.filter11);
        filter12 = findViewById(R.id.filter12);
        filter13 =  findViewById(R.id.filter13);
        filter14 = findViewById(R.id.filter14);
        filter15 =  findViewById(R.id.filter15);
        filter16 =  findViewById(R.id.filter16);
        userSelectedImage =  findViewById(R.id.userSelectedImage);
    }


    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {

        Bitmap x = null;



        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(!isCancelled())
            {


                try
                {
                    x = methods.getResizedBitmap(currentEditingBitmap,1000);

                    switch (type) {
                        case "0":
                            allFilterBitmaps.set(0, filters.get(0).processFilter(x));
                            break;
                        case "1":
                            allFilterBitmaps.set(1, filters.get(1).processFilter(x));
                            break;
                        case "2":
                            allFilterBitmaps.set(2, filters.get(2).processFilter(x));
                            break;
                        case "3":
                            allFilterBitmaps.set(3, filters.get(3).processFilter(x));
                            break;
                        case "4":
                            allFilterBitmaps.set(4, filters.get(4).processFilter(x));
                            break;
                        case "5":
                            allFilterBitmaps.set(5, filters.get(5).processFilter(x));
                            break;
                        case "6":
                            allFilterBitmaps.set(6, filters.get(6).processFilter(x));
                            break;
                        case "7":
                            allFilterBitmaps.set(7, filters.get(7).processFilter(x));
                            break;
                        case "8":
                            allFilterBitmaps.set(8, filters.get(8).processFilter(x));
                            break;
                        case "9":
                            allFilterBitmaps.set(9, filters.get(9).processFilter(x));
                            break;
                        case "10":
                            allFilterBitmaps.set(10, filters.get(10).processFilter(x));
                            break;
                        case "11":
                            allFilterBitmaps.set(11, filters.get(11).processFilter(x));
                            break;
                        case "12":
                            allFilterBitmaps.set(12, filters.get(12).processFilter(x));
                            break;
                        case "13":
                            allFilterBitmaps.set(13, filters.get(13).processFilter(x));
                            break;
                        case "14":
                            allFilterBitmaps.set(14, filters.get(14).processFilter(x));
                            break;
                        case "15":
                            allFilterBitmaps.set(15, filters.get(15).processFilter(x));
                            break;
                    }
                }
                catch (OutOfMemoryError e)
                {
                    Log.d("Error",e.getMessage());
                }



            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);


            if(type.equals("0"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                //userSelectedImage.setImageBitmap(x);
                addedEffectBitmap = x;
            }
            if(type.equals("1"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("2"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("3"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("4"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("5"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("6"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("7"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("8"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("9"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("10"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("11"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("12"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("13"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("14"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }
            if(type.equals("15"))
            {
                Glide.with(getApplicationContext()).load(x).into(userSelectedImage);
                addedEffectBitmap = x;
            }

            //this method will be running on UI thread
            pdLoading.dismiss();



        }



    }

    private class RunInBackground extends AsyncTask<Void,Void,Void>
    {


        Bitmap lastBitmap;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            dialog.dismiss();

            try
            {

                Intent intent = new Intent();
                setResult(21,intent);
                finish();
                //lastBitmap.recycle();

            }
            catch (Exception e)
            {
                Log.d("Error",e.getMessage());
            }

            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime);

                MainActivity.deleteUndoRedoImages();


            }
            });


            this.cancel(true);

        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if(!isCancelled())
            {
                if(!isCancelled())
                {
                    if(addedEffectBitmap != null)
                    {

                        lastBitmap = getFinalImage();
                        //lastBitmap = addedEffectBitmap;

                        MainActivity.imagePosition++;
                        MainActivity.images.add(MainActivity.imagePosition, lastBitmap);




                        if (EditorActivity.isNeededToDelete) {
                            try {
                                MainActivity.images.remove(MainActivity.imagePosition + 1);
                            }
                            catch (Exception e)
                            {
                                Log.d("Error",e.getMessage());
                            }
                        }


                        //MainActivity.CurrentWorkingFilePath = getImageUri(AddEffects.this, lastBitmap);
                        //MainActivity.filePaths.add(getImageUri(AddEffects.this, lastBitmap));









                    }
                }

            }
            return null;
        }

    }
    public Bitmap getFinalImage()
    {



        currentEditingBitmap = MainActivity.images.get(MainActivity.imagePosition);
        Bitmap x = methods.getResizedBitmap(currentEditingBitmap,1200);

        switch (type) {
            case "0":

                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }

                x = filters.get(0).processFilter(x);
                break;
            case "1":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(1).processFilter(x);
                break;
            case "2":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }

                x = filters.get(2).processFilter(x);
                break;
            case "3":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(3).processFilter(x);
                break;
            case "4":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(4).processFilter(x);
                break;
            case "5":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }

                x = filters.get(5).processFilter(x);
                break;
            case "6":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(6).processFilter(x);
                break;
            case "7":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }

                x = filters.get(7).processFilter(x);
                break;
            case "8":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(8).processFilter(x);
                break;
            case "9":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(9).processFilter(x);
                break;
            case "10":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(10).processFilter(x);
                break;
            case "11":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(11).processFilter(x);
                break;
            case "12":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(12).processFilter(x);
                break;
            case "13":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(13).processFilter(x);
                break;
            case "14":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter16Bitmap != null) {
                    filter16Bitmap.recycle();
                }
                x = filters.get(14).processFilter(x);
                break;
            case "15":

                if (filter1Bitmap != null) {
                    filter1Bitmap.recycle();
                }
                if (filter2Bitmap != null) {
                    filter2Bitmap.recycle();
                }
                if (filter3Bitmap != null) {
                    filter3Bitmap.recycle();
                }
                if (filter4Bitmap != null) {
                    filter4Bitmap.recycle();
                }
                if (filter5Bitmap != null) {
                    filter5Bitmap.recycle();
                }
                if (filter6Bitmap != null) {
                    filter6Bitmap.recycle();
                }
                if (filter7Bitmap != null) {
                    filter7Bitmap.recycle();
                }
                if (filter8Bitmap != null) {
                    filter8Bitmap.recycle();
                }
                if (filter9Bitmap != null) {
                    filter9Bitmap.recycle();
                }
                if (filter10Bitmap != null) {
                    filter10Bitmap.recycle();
                }
                if (filter11Bitmap != null) {
                    filter11Bitmap.recycle();
                }
                if (filter12Bitmap != null) {
                    filter12Bitmap.recycle();
                }
                if (filter13Bitmap != null) {
                    filter13Bitmap.recycle();
                }
                if (filter14Bitmap != null) {
                    filter14Bitmap.recycle();
                }
                if (filter15Bitmap != null) {
                    filter15Bitmap.recycle();
                }
                x = filters.get(15).processFilter(x);
                break;
        }

        return x;
    }

}
