package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.github.chuross.library.ExpandableLayout;
import com.sba.sinhalaphotoeditor.callbacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.config.RotationGestureDetector;
import com.sba.sinhalaphotoeditor.database.DatabaseHelper;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import static com.sba.sinhalaphotoeditor.activities.MyCustomGallery.selectedBitmap;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class PhotoOnPhotoActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener, OnAsyncTaskState
{


    public static String IMAGE_OUT_URI = "imageOutURI";


    public static int IMAGE_ON_IMAGE_RESULT_OK_CODE = 110;
    public static int IMAGE_ON_IMAGE_RESULT_FAILED_CODE = -100;
    public static int IMAGE_ON_IMAGE_REQUEST_CODE = 100;



    private Uri imageOutUri;
    private ImageView addNewImage;
    private ConstraintLayout workingLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private RotationGestureDetector mRotationGestureDetector;
    private float scaleFactor;

    private DatabaseHelper helper = new DatabaseHelper(PhotoOnPhotoActivity.this);

    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;
    private float opacityLevel = 1f;

    private Dialog dialog;


    private Methods methods;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
    public void onBackPressed()
    {
        if(expandableLayout.isExpanded())
        {
            expandableLayout.collapse();
            expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
            expandableLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        else
        {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_on_photo);




        methods = new Methods(getApplicationContext());


        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(PhotoOnPhotoActivity.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(PhotoOnPhotoActivity.this);
        uiSetup();

        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);
        SeekBar opacitySeekBar = (SeekBar) findViewById(R.id.opacitySeekBar);

        opacitySeekBar.setProgress((int)opacityLevel * 100);

        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                opacityLevel = (progress / 100.0f);
                addNewImage.setAlpha(opacityLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expandableLayout.isExpanded())
                {
                    expandableLayout.collapse();
                    expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
                    expandableLayout.setBackgroundColor(Color.TRANSPARENT);
                }
                else
                {
                    expandIcon.setBackgroundColor(Color.TRANSPARENT);
                    expandableLayout.setBackground(getResources().getDrawable(R.drawable.white_opacity_background));
                    expandableLayout.expand();
                }



            }
        });


    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector)
    {
        float angle = rotationDetector.getAngle();
        addNewImage.setRotation(angle);
    }

    @Override
    public void startActivityForResult()
    {
        hideProgressDialog();
        AsyncTask.execute(new Runnable()
        {
            @Override
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

        Intent intent = new Intent();
        intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
        setResult(IMAGE_ON_IMAGE_RESULT_OK_CODE,intent);
        finish();
    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch


            scaleFactor *= detector.getScaleFactor();
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
            scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //
            addNewImage.setScaleX(scaleFactor);
            addNewImage.setScaleY(scaleFactor);

            return true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        scaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    private void uiSetup()
    {
        //show progress dialog

       showProgressDialog();

        //setup action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Image");
        }

        //get the image bitmap
        Bitmap bitmapForImageView = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(), true);

        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = methods.scaleImageKeepAspectRatio(bitmapForImageView,width);


        //create the layouts
        //base layout
        ConstraintLayout baseLayout = findViewById(R.id.baseLayout);
        //working layout
        workingLayout = findViewById(R.id.workingLayout);
        //image view
        ImageView sourceImageView = findViewById(R.id.sourceImageView);
        //textview
        addNewImage = findViewById(R.id.addImageView);







        sourceImageView.setImageBitmap(bitmapForImageView);
        //Glide.with(getApplicationContext()).load(bitmapForImageView).into(sourceImageView);



        workingLayout.setDrawingCacheEnabled(true);
        hideProgressDialog();

        addNewImage.setImageBitmap(selectedBitmap);
        //addTextView.setTextSize(textFontSize);







        addNewImage.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0, lastY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        lastX = motionEvent.getX();
                        lastY = motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = motionEvent.getX() - lastX;
                        float dy = motionEvent.getY() - lastY;
                        float finalX = view.getX() + dx;
//                        float finalY = view.getY() + dy + view.getHeight();
                        float finalY = view.getY() + dy;
                        view.setX(finalX);
                        view.setY(finalY);
                        break;
                }

                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_on_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.setImage)
        {
            showProgressDialog();
            boolean doneSetting = setTextFinal();
            if(doneSetting)
            {
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);
                    bitmap = methods.CropBitmapTransparency(bitmap);
                    AddImageToArrayListAsyncTask task = new AddImageToArrayListAsyncTask(bitmap,this);
                    task.execute();
                }
                catch (Exception e)
                {
                    hideProgressDialog();
                    e.printStackTrace();
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
                    setResult(IMAGE_ON_IMAGE_RESULT_OK_CODE,intent);
                    finish();

                }

            }
            else
            {
                hideProgressDialog();
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
                setResult(IMAGE_ON_IMAGE_RESULT_OK_CODE,intent);
                finish();
            }
            return  true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }



    }

    private boolean setTextFinal()
    {
        addNewImage.setOnTouchListener(null);
        boolean toBeReturn = false;
        workingLayout.buildDrawingCache();
        toBeReturn = saveFile(Bitmap.createBitmap(workingLayout.getDrawingCache()),"temp.jpg");
        return toBeReturn;
    }

    private boolean saveFile(Bitmap sourceImageBitmap,String fileName)
    {
        boolean result = false;
        String saveDir = "/tmp/";
        String path = getApplicationInfo().dataDir + saveDir;
        File pathFile = new File(path);
        pathFile.mkdirs();
        File imageFile = new File(path,fileName);
        if(imageFile.exists())
        {
            imageFile.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);

            sourceImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            result = true;
        } catch (Exception e) {
            String errorAny = e.getMessage();
            result =false;
            e.printStackTrace();
        }
        imageOutUri = Uri.fromFile(imageFile);
        return result;
    }
    public void showProgressDialog()
    {
        if(dialog == null)
        {
            dialog = new Dialog(PhotoOnPhotoActivity.this,R.style.CustomBottomSheetDialogTheme);
        }
        View view = getLayoutInflater().inflate(R.layout.progress_dialog,null);
        ProgressBar bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);

        dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.show();
    }
    public void hideProgressDialog()
    {
        if(dialog != null)
        {
            View view = getLayoutInflater().inflate(R.layout.progress_dialog,null);
            ProgressBar bar = view.findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);
            dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.hide();
        }
    }

}
