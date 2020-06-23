package com.sba.sinhalaphotoeditor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.callbacks.OnAFileAddedListner;
import com.sba.sinhalaphotoeditor.callbacks.OnItemClickListner;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.GridViewAdapter;
import com.sba.sinhalaphotoeditor.aynctask.GalleryImageHandler;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;


public class MyCustomGallery extends AppCompatActivity {


    public static Bitmap selectedBitmap = null;
    public static final int IMAGE_PICK_RESULT_CODE = 235;


    private GridViewAdapter adapter;
    private LinearLayoutManager manager;
    private GalleryImageHandler galleryImageHandler;
    private ArrayList<GalleryImage> showingImages;
    public static final int imageLimit = 18;
    private int maxImagesPerScroll = 18;
    private static int lastScrolledPosition = 0;
    private static int lastShowingListCount = 0;
    private boolean isStillShowing = false;
    private static int prevOffset = 0;
    private  Timer timer;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        lastScrolledPosition =   manager.findFirstVisibleItemPosition();
        lastShowingListCount = showingImages.size();
        View firstItemView = manager.findViewByPosition(lastScrolledPosition);
        if(firstItemView != null)
        prevOffset = firstItemView.getTop();

        if(timer != null)
        {
            timer.cancel();
        }

        galleryImageHandler.cancel(true);

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
        galleryImageHandler.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_custom_gallery);


        final SwipeRefreshLayout swipe_layout = findViewById(R.id.swipe_layout);
        final RecyclerView gridView = findViewById(R.id.gridView);
        manager = new LinearLayoutManager(getApplicationContext());
        showingImages = new ArrayList<>();

        swipe_layout.setEnabled(false);


        adapter = new GridViewAdapter(getApplicationContext(), showingImages, new OnItemClickListner() {
            @Override
            public void onClicked(File file)
            {
                showSelectView(file);
            }
        });
        gridView.setLayoutManager(manager);
        gridView.setAdapter(adapter);

        galleryImageHandler = new GalleryImageHandler(getContentResolver(), new OnAFileAddedListner() {
            @Override
            public void fileAdded(GalleryImage image)
            {

            }

            @Override
            public void notifyAdapter()
            {
                try
                {
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        galleryImageHandler.execute();

        if(lastShowingListCount == 0)
        {
            swipe_layout.setEnabled(true);
            swipe_layout.setRefreshing(true);
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run()
                {
                    if(galleryImageHandler.getListSize() >= imageLimit)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                galleryImageHandler.getList(showingImages,imageLimit);

                                swipe_layout.setRefreshing(false);
                                swipe_layout.setEnabled(false);
                            }
                        });

                        timer.cancel();
                    }

                }
            }, 0, 500);

        }
        else
        {
            galleryImageHandler.getList(showingImages,lastShowingListCount);
        }



        gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                   galleryImageHandler.getList(showingImages,maxImagesPerScroll);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });




        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                if(prevOffset > 0 || lastScrolledPosition > 0)
                {
                    if(showingImages.size() >= lastShowingListCount)
                    {
                        final Button button = findViewById(R.id.scrollToPos);

                        TranslateAnimation animation = new TranslateAnimation(0,0,2000, 0);
                        animation.setDuration(500);
                        animation.setFillAfter(true);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                button.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation)
                            {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        button.startAnimation(animation);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                manager.scrollToPositionWithOffset(lastScrolledPosition,prevOffset);
                            }
                        });


                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TranslateAnimation animation = new TranslateAnimation(0,0,0, 2000);
                                animation.setDuration(500);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation)
                                    {
                                        button.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                button.startAnimation(animation);
                            }
                        }, 4000 );
                        timer.cancel();

                    }
                }
                else
                {
                    timer.cancel();
                }

            }
        }, 0, 100);


        changeTypeFace();

    }
    public Bitmap getBitmap(String path)
    {
        try
        {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, options);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public void showSelectView(File file)
    {
        if(!isStillShowing)
        {
            isStillShowing = true;
            final ConstraintLayout select_image_layout = findViewById(R.id.select_image_layout);
            CircleImageView picture = findViewById(R.id.picture);
            Button select_btn = findViewById(R.id.select_btn);

            final Bitmap bitmap;
            try
            {
                bitmap = Methods.getResizedBitmapWithURI(getApplicationContext(),Uri.fromFile(file),10);
                if(bitmap != null)
                {
                    Glide.with(MyCustomGallery.this)
                            .load(file).into(picture);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            select_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(selectedBitmap != null)
                    {

                        galleryImageHandler.cancel(true);
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        setResult(IMAGE_PICK_RESULT_CODE,intent);
                        finish();
                        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);

                    }
                    else
                    {
                        Methods.showCustomToast(MyCustomGallery.this,getResources().getString(R.string.select_an_image_first_text));
                    }
                }
            });
            select_image_layout.setVisibility(View.VISIBLE);
            select_image_layout.animate().setDuration(250).translationYBy(200).translationY(0).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    isStillShowing = false;
                    select_image_layout.animate().setDuration(500).translationYBy(0).translationY(200).start();
                }
            },2000);

        }

    }
    private void changeTypeFace()
    {
       Typeface typeface = Methods.getDefaultTypeFace(getApplicationContext());

        TextView tool_bar_title = findViewById(R.id.tool_bar_title);
        Button scrollToPos = findViewById(R.id.scrollToPos);
        Button select_btn = findViewById(R.id.select_btn);
        tool_bar_title.setTypeface(typeface);
        scrollToPos.setTypeface(typeface);
        select_btn.setTypeface(typeface);

    }
}