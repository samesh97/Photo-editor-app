package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.AlbumAdapter;
import com.sba.sinhalaphotoeditor.adapters.ImageAdapter;
import com.sba.sinhalaphotoeditor.callbacks.OnAlbumClick;
import com.sba.sinhalaphotoeditor.model.AlbumModel;
import com.sba.sinhalaphotoeditor.model.pictureFacer;
import com.sba.sinhalaphotoeditor.sdk.Methods;

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

public class MyGallery extends AppCompatActivity {


    private RecyclerView albums_rv;
    private RecyclerView images_rv;
    private ArrayList<pictureFacer> photoList;
    private ArrayList<AlbumModel> albumList;
    private ImageAdapter imageAdapter;
    private AlbumAdapter albumAdapter;

    private int lastPos = 0;
    private Timer timer;

    private boolean isStillShowing = false;
    public static Bitmap selectedBitmap = null;


    @Override
    public void onBackPressed()
    {
        if(timer != null)
            timer.cancel();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }

    @Override
    protected void onDestroy()
    {
        if(timer != null)
            timer.cancel();
        super.onDestroy();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        init();
        setData();
        changeTypeFace();

    }

    private void setData()
    {

        albumAdapter = new AlbumAdapter(getApplicationContext(), albumList, new OnAlbumClick() {
            @Override
            public void onAlbumChanged(String path,int position)
            {
                if(lastPos != position)
                {
                    setAdapter(path);
                    lastPos = position;
                }

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        albums_rv.setLayoutManager(manager);
        albums_rv.setAdapter(albumAdapter);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run()
            {
                getPicturePaths();
            }
        });



        imageAdapter = new ImageAdapter(getApplicationContext(), photoList, new OnAlbumClick() {
            @Override
            public void onAlbumChanged(final String path, int position)
            {
                showSelectView(new File(path));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run()
                    {
                        showSelectView(new File(path));
                        selectedBitmap = getBitmap(path);
                    }
                });
            }
        });


        if(albumList.size() > 0)
        {
            setAdapter(albumList.get(0).getPath());
        }
        else
        {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run()
                {
                    if(albumList.size() > 0)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                setAdapter(albumList.get(0).getPath());
                            }
                        });

                        timer.cancel();
                    }
                }
            }, 0, 50);
        }
    }

    private void setAdapter(final String path)
    {
        photoList.clear();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        images_rv.setLayoutManager(gridLayoutManager);
        images_rv.setAdapter(imageAdapter);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run()
            {
                getAllImagesByFolder(path);

            }
        });


    }

    private void init()
    {
        albums_rv = findViewById(R.id.albums_rv);
        images_rv = findViewById(R.id.images_rv);

        photoList = new ArrayList<>();
        albumList = new ArrayList<>();

    }
    private void getPicturePaths()
    {
        final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, orderBy);

        if(cursor == null || cursor.getCount() == 0)
        {
            hideAllViews();
        }


        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                AlbumModel folds = new AlbumModel();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths))
                {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();

                    albumList.add(folds);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            albumAdapter.notifyDataSetChanged();
                        }
                    });
                }
                else
                {
                    for(int i = 0;i<albumList.size();i++)
                    {
                        if(albumList.get(i).getPath().equals(folderpaths))
                        {
                            albumList.get(i).setFirstPic(datapath);
                            albumList.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideAllViews()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Toolbar toolbar = findViewById(R.id.toolbar);
                TextView textView14 = findViewById(R.id.textView14);
                TextView textView15 = findViewById(R.id.textView15);
                ConstraintLayout lay = findViewById(R.id.lay);
                ConstraintLayout bound1 = findViewById(R.id.bound1);
                ConstraintLayout bound2 = findViewById(R.id.bound2);

                //lay.setVisibility(View.GONE);
                bound1.setVisibility(View.GONE);
                bound2.setVisibility(View.GONE);
                textView14.setVisibility(View.INVISIBLE);
                textView15.setVisibility(View.INVISIBLE);


                TextView tool_bar_title = toolbar.findViewById(R.id.tool_bar_title);
                tool_bar_title.setText(R.string.no_images_found_text);

                ConstraintLayout constraint_main = findViewById(R.id.constraint_main);


                LottieAnimationView imageView = new LottieAnimationView(getApplicationContext());
                imageView.setAnimation(R.raw.question);
                imageView.setRepeatCount(5);

                //ImageView imageView = new ImageView(getApplicationContext());
                imageView.setId(View.generateViewId());
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);


                constraint_main.addView(imageView,params);
                //Glide.with(getApplicationContext()).load(R.drawable.empty_character).into(imageView);

                ConstraintSet set = new ConstraintSet();
                set.clone(constraint_main);
                set.connect(constraint_main.getId(),ConstraintSet.LEFT,imageView.getId(),ConstraintSet.LEFT,10);
                set.connect(constraint_main.getId(),ConstraintSet.TOP,imageView.getId(),ConstraintSet.TOP,10);
                set.connect(constraint_main.getId(),ConstraintSet.RIGHT,imageView.getId(),ConstraintSet.RIGHT,10);
                set.connect(constraint_main.getId(),ConstraintSet.BOTTOM,imageView.getId(),ConstraintSet.BOTTOM,10);

                set.applyTo(constraint_main);
                imageView.playAnimation();
            }
        });


    }

    public void getAllImagesByFolder(String path)
    {

        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, orderBy);
        try
        {
            if(cursor != null)
            {
                cursor.moveToFirst();
                do{
                    pictureFacer pic = new pictureFacer();

                    pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                    pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                    pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                    photoList.add(pic);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            //imageAdapter.notifyItemRangeChanged(0,photoList.size());
                            imageAdapter.notifyItemInserted(photoList.size());
                        }
                    });
                }while(cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(!isStillShowing && file != null)
        {
            isStillShowing = true;
            final ConstraintLayout select_image_layout = findViewById(R.id.select_image_layout);
            CircleImageView picture = findViewById(R.id.picture);
            Button select_btn = findViewById(R.id.select_btn);


            Glide.with(MyGallery.this)
                    .load(file).into(picture);


            select_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(selectedBitmap != null)
                    {

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);

                    }
                    else
                    {
                        Methods.showCustomToast(MyGallery.this,getResources().getString(R.string.select_an_image_first_text));
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
        Button select_btn = findViewById(R.id.select_btn);
        tool_bar_title.setTypeface(typeface);
        select_btn.setTypeface(typeface);

    }
}