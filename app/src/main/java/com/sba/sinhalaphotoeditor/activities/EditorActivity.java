package com.sba.sinhalaphotoeditor.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.custom.views.GridView;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.singleton.ImageList;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.NUMBER_OF_IMAGES_WAS_ADDED;
import static com.sba.sinhalaphotoeditor.activities.MyCustomGallery.IMAGE_PICK_RESULT_CODE;
import static com.sba.sinhalaphotoeditor.activities.MyCustomGallery.selectedBitmap;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int DRAW_ON_BITMAP_REQUEST_CODE = 500;
    public static final int NORMAL_CROP_IMAGE_REQUEST_CODE = 700;
    public static final int PICKED_IMAGE_CROP_IMAGE_REQUEST_CODE = 701;
    private ImageView userSelectedImage;
    private ImageView addText,addImage,addSticker,addCrop,addBlur;
    private static final int PICK_IMAGE_REQUEST = 234;
    private ImageView addEffect;

    private Methods methods;
    private InterstitialAd mInterstitialAd;

    //last double tap
    private long lastClickTime = 0;
    //gridView
    private boolean isGridActive = false;
    private GridView grid_view;
    private ImageView grid_icon;
    private boolean isGridViewWidthUpdating = false;
    private int maxSeconds = 3;
    private int tempMaxSeconds = 3;


    @Override
    public void onBackPressed()
    {
        if(ImageList.getInstance().getImageListSize() > 1)
        {
            showConfirmationDialogToExit();
        }
        else
        {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
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
        if(item.getItemId() == R.id.undoImage)
        {
            if(ImageList.getInstance().getCurrentPosition() >= 1)
            {
                try
                {

                    ImageList.getInstance().substractImagePosition(1);
                    methods.setImageViewScaleType(userSelectedImage);
                    Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.you_reach_end_text));
            }
        }
        if(item.getItemId() == R.id.saveImage)
        {
            if(mInterstitialAd.isLoaded())
            {
                mInterstitialAd.show();
            }
            else
            {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if(ImageList.getInstance().getImageListSize() > 0)
                {

                    startActivity(new Intent(getApplicationContext(), ImageSavingActivity.class));
                    overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);

                }
                else
                {
                    Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.something_went_wrong_text));
                }
            }

        }
        if(item.getItemId() == R.id.redoImage)
        {
            if(ImageList.getInstance().getCurrentPosition() < (ImageList.getInstance().getImageListSize() - 1))
            {
                try
                {

                    ImageList.getInstance().increaseImagePosition(1);
                    methods.setImageViewScaleType(userSelectedImage);
                    Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.you_reach_end_text_2));
            }
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TextOnImageActivity.TEXT_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == TextOnImageActivity.TEXT_ON_IMAGE_RESULT_OK_CODE)
            {
                methods.setImageViewScaleType(userSelectedImage);
                Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);
            }

        }
        if(requestCode == PhotoOnPhotoActivity.IMAGE_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == PhotoOnPhotoActivity.IMAGE_ON_IMAGE_RESULT_OK_CODE)
            {
                methods.setImageViewScaleType(userSelectedImage);
                Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);
            }

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == IMAGE_PICK_RESULT_CODE && selectedBitmap != null)
        {
                Intent intent = new Intent(getApplicationContext(),CropActivity.class);
                startActivityForResult(intent,PICKED_IMAGE_CROP_IMAGE_REQUEST_CODE);
        }
        if(requestCode == AddStickerOnImage.STICKER_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == AddStickerOnImage.STICKER_ON_IMAGE_RESULT_OK_CODE)
            {
                methods.setImageViewScaleType(userSelectedImage);
                Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);

            }
        }
        if(requestCode == NORMAL_CROP_IMAGE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            ImageList.getInstance().addBitmap(CropActivity.croppedBitmap,true);
            methods.setImageViewScaleType(userSelectedImage);
            Glide.with(getApplicationContext()).load(CropActivity.croppedBitmap).into(userSelectedImage);
            ImageList.getInstance().deleteUndoRedoImages();

        }
        if(requestCode == PICKED_IMAGE_CROP_IMAGE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            selectedBitmap = CropActivity.croppedBitmap;
            addImageOnImage();
        }
        if(requestCode == 10  && resultCode == 11)
        {
            methods.setImageViewScaleType(userSelectedImage);
            Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);
        }
        if(requestCode == 20  && resultCode == 21)
        {
            methods.setImageViewScaleType(userSelectedImage);
            Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);
        }
        if(requestCode == DRAW_ON_BITMAP_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            methods.setImageViewScaleType(userSelectedImage);
            Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3538783908730049/5147080745");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());



        methods = new Methods(getApplicationContext());


       MobileAds.initialize(this, new OnInitializationCompleteListener()
       {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        userSelectedImage = (ImageView) findViewById(R.id.userSelectedImage);
        addText = (ImageView) findViewById(R.id.addText);
        addImage = (ImageView) findViewById(R.id.addImage);
        addSticker =(ImageView) findViewById(R.id.addSticker);
        addCrop = (ImageView) findViewById(R.id.addCrop);
        addEffect = (ImageView) findViewById(R.id.addEffect);
        addBlur = (ImageView) findViewById(R.id.addBlur);
        ImageView drawImage = (ImageView) findViewById(R.id.drawImage);


        addBlur.setOnClickListener(this);
        addEffect.setOnClickListener(this);


        methods.setImageViewScaleType(userSelectedImage);
        Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSelectedImage);



        drawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), DrawOnBitmapActivity.class);
                startActivityForResult(intent,DRAW_ON_BITMAP_REQUEST_CODE);
                overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
            }
        });


        addText.setOnClickListener(this);
        addImage.setOnClickListener(this);
        addSticker.setOnClickListener(this);
        addCrop.setOnClickListener(this);




        //double tap zoom
        final int DOUBLE_TAP_EXPIRE_TIME = 300;
        userSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_TAP_EXPIRE_TIME)
                {
                    handleImageScale();
                }
                lastClickTime = clickTime;
            }
        });


        //turning on off gridView
        grid_view = findViewById(R.id.grid_view);
        grid_icon = findViewById(R.id.grid_icon);
        grid_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               handleGridView();
            }
        });


        //set gridCellWidth
        grid_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                if(!isGridActive)
                {
                    handleGridView();
                }

                tempMaxSeconds = maxSeconds;
                final View view1 = getLayoutInflater().inflate(R.layout.layout_seekbar,null);
                view1.setId(View.generateViewId());
                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);


                final SeekBar seek_bar = view1.findViewById(R.id.seek_bar);
                seek_bar.setProgress(grid_view.getGapWidth());

                seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
                    {

                        isGridViewWidthUpdating = true;
                        if(progress < 25)
                        {
                            seek_bar.setProgress(25);
                        }
                        grid_view.setGapWidth(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {
                        isGridViewWidthUpdating = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                        isGridViewWidthUpdating = false;
                        tempMaxSeconds = maxSeconds;
                    }
                });


                final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run()
                    {

                        if(!isGridViewWidthUpdating  && tempMaxSeconds <= 0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    view1.animate().scaleX(0).start();
                                            //view1.animate().alpha(0).setDuration(600).start();
                                }
                            });
                            timer.cancel();
                        }
                        tempMaxSeconds--;
                    }
                    }, 0, 1000);


                ConstraintLayout constraintLayout = findViewById(R.id.constraint_main);
                constraintLayout.addView(view1,lp);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(view1.getId(), ConstraintSet.RIGHT, grid_icon.getId(), ConstraintSet.LEFT, Methods.convertDpToPixel(40,getApplicationContext()));
                constraintSet.connect(view1.getId(),ConstraintSet.LEFT,constraintLayout.getId(),ConstraintSet.LEFT,Methods.convertDpToPixel(88,getApplicationContext()));
                constraintSet.connect(view1.getId(),ConstraintSet.TOP,grid_icon.getId(),ConstraintSet.TOP);
                constraintSet.connect(view1.getId(),ConstraintSet.BOTTOM,grid_icon.getId(),ConstraintSet.BOTTOM);
                constraintSet.applyTo(constraintLayout);


                return true;
            }
        });

    }

    private void handleGridView()
    {
        if(!isGridActive)
        {
            grid_view.setVisibility(View.VISIBLE);
            grid_icon.setImageDrawable(getDrawable(R.drawable.grid_show));
            isGridActive = true;
        }
        else
        {
            grid_view.setVisibility(View.GONE);
            grid_icon.setImageDrawable(getDrawable(R.drawable.grid_hide));
            isGridActive = false;
        }
    }

    private void addTextOnImage()
    {
        Intent intent = new Intent(EditorActivity.this, TextOnImageActivity.class);
        startActivityForResult(intent, TextOnImageActivity.TEXT_ON_IMAGE_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);

    }
    private void addImageOnImage()
    {
        Intent intent = new Intent(EditorActivity.this,PhotoOnPhotoActivity.class);
        startActivityForResult(intent, PhotoOnPhotoActivity.IMAGE_ON_IMAGE_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }
    private void showFileChooser()
    {

        Intent intent = new Intent(EditorActivity.this, MyCustomGallery.class);
        intent.putExtra("Activity","EditorActivity");
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }
    private void addStickerOnImage()
    {
        Intent intent = new Intent(EditorActivity.this,AddStickerOnImage.class);
        startActivityForResult(intent, AddStickerOnImage.STICKER_ON_IMAGE_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }
    @Override
    public void onClick(View v)
    {

        if(v == addBlur)
        {

                    if(ImageList.getInstance().getCurrentBitmap() != null && ImageList.getInstance().getImageListSize() > 0)
                    {
                        startActivityForResult(new Intent(getApplicationContext(), AdjustImage.class),10);
                        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
                    }
                    else
                    {
                        Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.no_image_selected_text));

                    }

        }
        else if(v == addEffect)
        {


                    if(ImageList.getInstance().getCurrentBitmap() != null && ImageList.getInstance().getImageListSize() > 0)
                    {
                        startActivityForResult(new Intent(getApplicationContext(), AddEffects.class),20);
                        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
                    }
                    else
                    {
                        Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.no_image_selected_text));
                    }



        }
        else if(v == addText)
        {

                    if(ImageList.getInstance().getCurrentBitmap() != null && ImageList.getInstance().getImageListSize() > 0)
                    {
                        addTextOnImage();
                    }
                    else
                    {
                        Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.no_image_selected_text));
                    }

        }
        else if(v == addImage)
        {

                    if(ImageList.getInstance().getCurrentBitmap() != null && ImageList.getInstance().getImageListSize() > 0)
                    {
                        showFileChooser();
                    }
                    else
                    {
                        Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.no_image_selected_text));
                    }

        }
        else if(v == addSticker)
        {
            addStickerOnImage();
        }
        else if(v == addCrop)
        {

            if(ImageList.getInstance().getCurrentBitmap() != null && ImageList.getInstance().getImageListSize() > 0)
            {
                selectedBitmap = ImageList.getInstance().getCurrentBitmap();
                Intent intent = new Intent(getApplicationContext(),CropActivity.class);
                startActivityForResult(intent,NORMAL_CROP_IMAGE_REQUEST_CODE);
                overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
            }
            else
            {
                    Methods.showCustomToast(EditorActivity.this,getResources().getString(R.string.no_image_selected_text));
            }
        }
    }
    private void showConfirmationDialogToExit()
    {
        final Dialog dialog = new Dialog(this);
        if(dialog.getWindow() != null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            float deviceWidth = Methods.getDeviceWidthInPX(getApplicationContext());
            int finalWidth = (int) (deviceWidth - (deviceWidth / 8));
            dialog.getWindow().setLayout(finalWidth,RelativeLayout.LayoutParams.WRAP_CONTENT);
        }


        View view = getLayoutInflater().inflate(R.layout.exit_dialog_layout,null);
        dialog.setContentView(view);


        ImageView imageView6 = view.findViewById(R.id.imageView6);
        imageView6.setImageDrawable(getDrawable(R.drawable.exit_editing_cartoon));
        imageView6.setScaleType(ImageView.ScaleType.CENTER);

        Typeface typeface = Methods.getDefaultTypeFace(getApplicationContext());


        TextView message = view.findViewById(R.id.textView8);
        message.setText(getResources().getString(R.string.are_you_sure_want_to_exit_text));
        message.setTypeface(typeface);

        TextView title = view.findViewById(R.id.textView7);
        title.setText(getResources().getString(R.string.warning_text));
        title.setTypeface(typeface);

        Button yes = view.findViewById(R.id.yesButton);
        Button no = view.findViewById(R.id.noButton);

        yes.setText(getResources().getString(R.string.yes_text));
        yes.setTypeface(typeface);

        no.setText(getResources().getString(R.string.no_text));
        no.setTypeface(typeface);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra(NUMBER_OF_IMAGES_WAS_ADDED,(ImageList.getInstance().getImageListSize() - 1));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void handleImageScale()
    {
        int maxScale = 2;
        if(userSelectedImage != null)
        {
            if(userSelectedImage.getScaleX() == 1 && userSelectedImage.getScaleY() == 1)
            {
                userSelectedImage.animate().scaleX(maxScale).scaleY(maxScale).start();

            }
            else
            {
                userSelectedImage.animate().scaleX(1).scaleY(1).start();
            }
        }
    }
}