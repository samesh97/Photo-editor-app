package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.RecyclerView.RecyclerViewAdapter;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.util.ArrayList;

public class UsePreviouslyEditedImageActivity extends AppCompatActivity {

    RecyclerView ImageList;
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    DatabaseHelper helper = new DatabaseHelper(UsePreviouslyEditedImageActivity.this);

    ProgressDialog pdLoading;

    Button deleteAll;

    RecyclerViewAdapter adapter;

    private Methods methods;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onStop() {
        super.onStop();
        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pdLoading != null)
        {
            pdLoading.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();


        if(pdLoading != null)
        {
            pdLoading.dismiss();
        }
        GlideBitmapPool.clearMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_previously_edited_image);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3538783908730049/5147080745");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());





        methods = new Methods(getApplicationContext());
        setTextViewFontAndSize();

        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();


        helper.deleteUnnessaryImages();

        pdLoading = new ProgressDialog(UsePreviouslyEditedImageActivity.this);

        GlideBitmapPool.clearMemory();

        //overridePendingTransition(R.anim.slidein, R.anim.slideout);


        ImageList = (RecyclerView) findViewById(R.id.ImageList);
        ImageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RecyclerViewAdapter(UsePreviouslyEditedImageActivity.this,images,ids,dates);
        ImageList.setAdapter(adapter);


        ImageView topGreenPannel;
        topGreenPannel = findViewById(R.id.topGreenPannel);
        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);


        deleteAll = (Button) findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(helper.getNumberOfRows() > 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UsePreviouslyEditedImageActivity.this);
                    alert.setTitle("Delete All");
                    alert.setIcon(R.drawable.delete);
                    alert.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            helper.deleteAll();
                            ids.clear();
                            images.clear();
                            dates.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                }
                else
                {
                    Methods.showCustomToast(UsePreviouslyEditedImageActivity.this,getResources().getString(R.string.no_images_available_text));

                }

            }
        });




        try {
            Cursor cursor = helper.GetAllImages();
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                ids.add(cursor.getInt(0));
                images.add(helper.getImage(cursor.getBlob(1)));
                dates.add(cursor.getString(2));

                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Methods.showCustomToast(UsePreviouslyEditedImageActivity.this,getResources().getString(R.string.we_will_fix_it_soon_text));

        }





    }
    public void setTextViewFontAndSize()
    {

        TextView textView = findViewById(R.id.textView);
        Button deleteAll = findViewById(R.id.deleteAll);


        Typeface typeface;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        int pos = pref.getInt("LanguagePosition",-99);
        if(pos != 99) {
            switch (pos) {
                case 1:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibresemibold);
                    textView.setTypeface(typeface);
                    deleteAll.setTypeface(typeface);

                    break;
                case 2:

                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.englishfont);
                    textView.setTypeface(typeface);


                    deleteAll.setTypeface(typeface);





                    break;
                case 3:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.tamilfont);


                    textView.setTypeface(typeface);
                    textView.setTextSize(20);

                    deleteAll.setTypeface(typeface);
                    deleteAll.setTextSize(14);

                    break;
            }
        }
    }
}


