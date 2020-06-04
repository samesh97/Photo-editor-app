package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.chuross.library.ExpandableLayout;
import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.Config.RotationGestureDetector;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import render.animations.Bounce;
import render.animations.Render;

public class PhotoOnPhotoActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener
{


    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static Uri IMAGE_ON_IMAGE_URI;

    public static int IMAGE_ON_IMAGE_RESULT_OK_CODE = 110;
    public static int IMAGE_ON_IMAGE_RESULT_FAILED_CODE = -100;
    public static int IMAGE_ON_IMAGE_REQUEST_CODE = 100;



    private Uri imageOutUri;
    private String saveDir="/tmp/";
    ImageView addNewImage;
    private String errorAny = "";
    private ImageView sourceImageView;
    private ConstraintLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    private float scaleFactor;

    DatabaseHelper helper = new DatabaseHelper(PhotoOnPhotoActivity.this);

    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;
    private SeekBar opacitySeekBar;
    private float opacityLevel = 1f;


    private Methods methods;

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
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
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_on_photo);




        methods = new Methods(getApplicationContext());

        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();

        if(getActionBar() != null && getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));
        }


        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(PhotoOnPhotoActivity.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(PhotoOnPhotoActivity.this);
        progressDialog = new ProgressDialog(PhotoOnPhotoActivity.this);
        extractBundle();
        uiSetup();

        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);
        opacitySeekBar = (SeekBar) findViewById(R.id.opacitySeekBar);

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
                    //expandIcon.setImageResource(R.drawable.slide_up_image);
                    expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
                    expandableLayout.setBackgroundColor(Color.TRANSPARENT);
                }
                else
                {
                    expandIcon.setBackgroundColor(Color.TRANSPARENT);
                    expandableLayout.setBackground(getResources().getDrawable(R.drawable.white_opacity_background));
                    expandableLayout.expand();// expand with animation
                    //expandIcon.setImageResource(R.drawable.slide_down_image);

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
    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch


            scaleFactor *= detector.getScaleFactor();
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
            scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //
            addNewImage.setScaleX(scaleFactor);
            addNewImage.setScaleY(scaleFactor);

            /*
            float size = addNewImage.getWidth();
            float factor = detector.getScaleFactor();
            float product = size*factor;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)product,(int)product);
            addNewImage.setLayoutParams(layoutParams);
            return true;*/

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

    private void extractBundle()
    {   //extract the data from previous activity
        Bundle bundle = getIntent().getExtras();
        // imageInUri = Uri.parse(bundle.getString(IMAGE_IN_URI));
        if(bundle != null)
        {
            IMAGE_ON_IMAGE_URI = Uri.parse((bundle.getString("IMAGE_ON_IMAGE_URI")));
        }


    }

    private void uiSetup()
    {
        //show progress dialog

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

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
        baseLayout = findViewById(R.id.baseLayout);
        //working layout
        workingLayout = findViewById(R.id.workingLayout);
        //image view
        sourceImageView = findViewById(R.id.sourceImageView);
        //textview
        addNewImage = findViewById(R.id.addImageView);







        sourceImageView.setImageBitmap(bitmapForImageView);
        //Glide.with(getApplicationContext()).load(bitmapForImageView).into(sourceImageView);

        Render render = new Render(PhotoOnPhotoActivity.this);
        render.setAnimation(Bounce.InUp(sourceImageView));
        render.start();

        workingLayout.setDrawingCacheEnabled(true);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        addNewImage.setImageURI(IMAGE_ON_IMAGE_URI);
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

            new RunInBackground().execute();
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
            errorAny = e.getMessage();
            result =false;
            e.printStackTrace();
        }
        imageOutUri = Uri.fromFile(imageFile);
        return result;
    }
    public class RunInBackground extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading..");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                String path = Methods.saveToInternalStorage(getApplicationContext(),ImageList.getInstance().getCurrentBitmap(),currentDateandTime);
                helper.AddImage(null,path);
                ImageList.getInstance().deleteUndoRedoImages();

                GlideBitmapPool.clearMemory();
            }
            });
        }

        @Override
        protected Void doInBackground(Void... voids)
        {


            //set the text
            boolean doneSetting = setTextFinal();
            if(doneSetting)
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);

                    bitmap = methods.CropBitmapTransparency(bitmap);
                    ImageList.getInstance().addBitmap(bitmap,true);
                    if(EditorActivity.isNeededToDelete)
                    {
                        try
                        {
                            ImageList.getInstance().removeBitmap(ImageList.getInstance().getCurrentPosition() + 1,false);
                        }
                        catch (Exception e)
                        {

                        }
                    }

                    // MainActivity.CurrentWorkingFilePath = imageOutUri;
                    // MainActivity.filePaths.add(imageOutUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setResult(IMAGE_ON_IMAGE_RESULT_OK_CODE,intent);
                /*
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }*/
                finish();

            }else
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_ERROR,errorAny);
                setResult(IMAGE_ON_IMAGE_RESULT_FAILED_CODE,intent);
                /*
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }*/
                finish();

            }
            return null;
        }
    }
}
