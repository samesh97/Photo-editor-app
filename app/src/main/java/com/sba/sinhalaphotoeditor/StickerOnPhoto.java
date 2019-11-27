package com.sba.sinhalaphotoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import render.animations.Bounce;
import render.animations.Render;
import render.animations.Zoom;

public class StickerOnPhoto extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {


    public static String STICKER_IN_URI = "imageInURI";
    public static String STICKER_SIZE = "textFontSize";
    public static String STICKER_OUT_URI = "imageOutURI";
    public static String STICKER_OUT_ERROR = "imageOutError";

    //public static Uri STICKER_ON_IMAGE_URI;

    public static int STICKER_ON_IMAGE_RESULT_OK_CODE = 210;
    public static int STICKER_ON_IMAGE_RESULT_FAILED_CODE = -200;
    public static int STICKER_ON_IMAGE_REQUEST_CODE = 200;



    private static String TAG = StickerOnPhoto.class.getSimpleName();
    private Uri imageInUri,imageOutUri;
    private String saveDir="/tmp/";
    ImageView addNewImage;
    private String errorAny = "";
    private ImageView sourceImageView;
    private RelativeLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    private float scaleFactor;

    DatabaseHelper helper = new DatabaseHelper(StickerOnPhoto.this);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(StickerOnPhoto.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(StickerOnPhoto.this);
        progressDialog = new ProgressDialog(StickerOnPhoto.this);
        extractBundle();
        uiSetup();
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        //set the rotation of the text view
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
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void extractBundle()
    {   //extract the data from previous activity
        Bundle bundle = getIntent().getExtras();
        imageInUri = Uri.parse(bundle.getString(STICKER_IN_URI));
        //STICKER_ON_IMAGE_URI = Uri.parse((bundle.getString("STICKER_ON_IMAGE_URI")));

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
            actionBar.setTitle("Add Sticker");
        }

        //get the image bitmap
        Bitmap bitmapForImageView = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(), true);


        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);


        int width = bitmapForImageView.getWidth();
        int height = bitmapForImageView.getHeight();

        //bitmapForImageView = scale(bitmapForImageView,metrics.widthPixels,metrics.heightPixels);

/*

        if(bitmapForImageView.getHeight() > bitmapForImageView.getWidth())
        {
           // height = scaleImageKeepAspectRatio(bitmapForImageView,width);
            Toast.makeText(this, "height is larger", Toast.LENGTH_SHORT).show();
            height = bitmapForImageView.getHeight();
            width = bitmapForImageView.getWidth();
        }
        //resize the views as per the image size

        /*
        if(bitmapForImageView.getWidth()>bitmapForImageView.getHeight())
        {
            width = 1280;
            height = 720;
        }
*//*
        else if(bitmapForImageView.getHeight() < bitmapForImageView.getWidth())
        {
            Toast.makeText(this, "width is larger", Toast.LENGTH_SHORT).show();
            //width =  scaleImageKeepAspectRatio2(bitmapForImageView,height);
            width = metrics.widthPixels;
            height = scaleImageKeepAspectRatio(bitmapForImageView,width);
        }
        else
        {
            Toast.makeText(this, "width and height same", Toast.LENGTH_SHORT).show();
            width = metrics.widthPixels;
            height =  metrics.widthPixels;
        }/*
        /*else
        {
            width = 600;
            height = 600;
        }*/

        //create the layouts
        //base layout
        baseLayout = new RelativeLayout(StickerOnPhoto.this);
        RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        //RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(width,height);
        baseLayout.setBackgroundColor(Color.parseColor("#000000"));

        //working layout
        workingLayout = new RelativeLayout(StickerOnPhoto.this);
        RelativeLayout.LayoutParams workingLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        workingLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        workingLayout.setLayoutParams(workingLayoutParams);

        //image view
        sourceImageView = new ImageView(StickerOnPhoto.this);
        RelativeLayout.LayoutParams sourceImageParams;
        if(bitmapForImageView.getHeight() > bitmapForImageView.getWidth())
        {
            int y = (metrics.widthPixels / width) * height;
            int dif = y - metrics.heightPixels;
            y = y + dif;
            int x = (((metrics.heightPixels) / (height)) * (width + dif));
            //x = x + (x - metrics.widthPixels);

            sourceImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else if(bitmapForImageView.getHeight() < bitmapForImageView.getWidth())
        {
            sourceImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else
        {
            sourceImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        sourceImageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        sourceImageView.setLayoutParams(sourceImageParams);
        //sourceImageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height));

        //textview
        addNewImage = new ImageView(StickerOnPhoto.this);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addNewImage.setLayoutParams(textViewParams);
        addNewImage.getLayoutParams().height = 80;
        addNewImage.getLayoutParams().width = 80;

        //add views to working layout
        workingLayout.addView(sourceImageView);
        workingLayout.addView(addNewImage);

        //add view to base layout
        baseLayout.addView(workingLayout);

        //set content view
        setContentView(baseLayout,baseLayoutParams);

        sourceImageView.setImageBitmap(bitmapForImageView);

        Render render = new Render(StickerOnPhoto.this);
        render.setAnimation(Bounce.InUp(sourceImageView));
        render.start();

        workingLayout.setDrawingCacheEnabled(true);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        addNewImage.setImageBitmap(EditorActivity.selectedSticker);
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
                        float finalY = view.getY() + dy + view.getHeight();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        toBeReturn = saveFile(Bitmap.createBitmap(workingLayout.getDrawingCache()),"temp.png");
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
    public int scaleImageKeepAspectRatio(Bitmap scaledGalleryBitmap,int width)
    {
        int imageWidth = scaledGalleryBitmap.getWidth();
        int imageHeight = scaledGalleryBitmap.getHeight();
        return  (imageHeight * width)/imageWidth;
        //scaledGalleryBitmap = Bitmap.createScaledBitmap(scaledGalleryBitmap, 500, newHeight, false);

    }
    public int scaleImageKeepAspectRatio2(Bitmap scaledGalleryBitmap,int height)
    {
        int imageWidth = scaledGalleryBitmap.getWidth();
        int imageHeight = scaledGalleryBitmap.getHeight();
        return  (imageHeight / height) * imageWidth;
        //scaledGalleryBitmap = Bitmap.createScaledBitmap(scaledGalleryBitmap, 500, newHeight, false);

    }
    public class RunInBackground extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime);
                MainActivity.deleteUndoRedoImages();
                //GlideBitmapPool.clearMemory();
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
                intent.putExtra(STICKER_OUT_URI,imageOutUri.toString());
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);

                    MainActivity.imagePosition++;
                    MainActivity.images.add(MainActivity.imagePosition,bitmap);
                    if(EditorActivity.isNeededToDelete)
                    {
                        try
                        {
                            MainActivity.images.remove(MainActivity.imagePosition + 1);
                        }
                        catch (Exception e)
                        {

                        }

                    }
                    MainActivity.CurrentWorkingFilePath = imageOutUri;


                    MainActivity.filePaths.add(imageOutUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setResult(STICKER_ON_IMAGE_RESULT_OK_CODE,intent);
                /*if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }*/
                finish();

            }
            else
            {
                Intent intent = new Intent();
                intent.putExtra(STICKER_OUT_ERROR,errorAny);
                setResult(STICKER_ON_IMAGE_RESULT_FAILED_CODE,intent);
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
    private Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }


}