package com.sba.sinhalaphotoeditor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sba.sinhalaphotoeditor.CallBacks.OnAFileAddedListner;
import com.sba.sinhalaphotoeditor.CallBacks.OnItemClickListner;
import com.sba.sinhalaphotoeditor.Config.ExifUtil;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.GridViewAdapter;
import com.sba.sinhalaphotoeditor.aynctask.GalleryImageHandler;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sba.sinhalaphotoeditor.adapters.GridViewAdapter.numberOfImagesInARow;


public class MyCustomGallery extends AppCompatActivity {


    public static Bitmap selectedBitmap = null;
    public static final int IMAGE_PICK_RESULT_CODE = 235;



    private RecyclerView gridView;
    private GridViewAdapter adapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout swipe_layout;
    private GalleryImageHandler galleryImageHandler;
    private ArrayList<GalleryImage> showingImages;
    private int imageLimit = 18;
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_custom_gallery);


        swipe_layout = findViewById(R.id.swipe_layout);
        gridView = findViewById(R.id.gridView);
        manager = new LinearLayoutManager(getApplicationContext());
        showingImages = new ArrayList<>();


        final GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(), showingImages, new OnItemClickListner() {
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
            galleryImageHandler.getList(showingImages,imageLimit);
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
}