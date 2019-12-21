package com.sba.sinhalaphotoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
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


import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import render.animations.Bounce;
import render.animations.Render;
import render.animations.Zoom;

public class PhotoOnPhoto extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {


    public static String IMAGE_IN_URI = "imageInURI";
    public static String IMAGE_SIZE = "textFontSize";
    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static Uri IMAGE_ON_IMAGE_URI;

    public static int IMAGE_ON_IMAGE_RESULT_OK_CODE = 110;
    public static int IMAGE_ON_IMAGE_RESULT_FAILED_CODE = -100;
    public static int IMAGE_ON_IMAGE_REQUEST_CODE = 100;



    private static String TAG = PhotoOnPhoto.class.getSimpleName();
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

    DatabaseHelper helper = new DatabaseHelper(PhotoOnPhoto.this);

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

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));

        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(PhotoOnPhoto.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(PhotoOnPhoto.this);
        progressDialog = new ProgressDialog(PhotoOnPhoto.this);
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
        imageInUri = Uri.parse(bundle.getString(IMAGE_IN_URI));
        IMAGE_ON_IMAGE_URI = Uri.parse((bundle.getString("IMAGE_ON_IMAGE_URI")));

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
        Bitmap bitmapForImageView = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(), true);

        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = scaleImageKeepAspectRatio(bitmapForImageView,width);

       // bitmapForImageView = scale(bitmapForImageView,metrics.widthPixels,metrics.heightPixels);



       /* if(bitmapForImageView.getHeight() > bitmapForImageView.getWidth())
        {
            height = scaleImageKeepAspectRatio(bitmapForImageView,width);
            width = scaleImageKeepAspectRatio2(bitmapForImageView,height);
        }
        //resize the views as per the image size

        /*
        if(bitmapForImageView.getWidth()>bitmapForImageView.getHeight())
        {
            width = 1280;
            height = 720;
        }
*/
       /* if(bitmapForImageView.getWidth()<bitmapForImageView.getHeight())
        {
            width = bitmapForImageView.getWidth();
            height = height = scaleImageKeepAspectRatio(bitmapForImageView,width);
        }
        /*else
        {
            width = 600;
            height = 600;
        }*/

        //create the layouts
        //base layout
        baseLayout = new RelativeLayout(PhotoOnPhoto.this);
        RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        //RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(width,height);
        baseLayout.setBackgroundColor(Color.parseColor("#000000"));

        //working layout
        workingLayout = new RelativeLayout(PhotoOnPhoto.this);
        RelativeLayout.LayoutParams workingLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        workingLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        workingLayout.setLayoutParams(workingLayoutParams);

        //image view
        sourceImageView = new ImageView(PhotoOnPhoto.this);
        RelativeLayout.LayoutParams sourceImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sourceImageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        sourceImageView.setLayoutParams(sourceImageParams);
        //sourceImageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height));

        //textview
        addNewImage = new ImageView(PhotoOnPhoto.this);
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

        Render render = new Render(PhotoOnPhoto.this);
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
                helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime);
                MainActivity.deleteUndoRedoImages();

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
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);


        String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
        Log.d("image",realPath);

        File file = new File(realPath);
        if(file.exists())
        {
            file.delete();
            Log.d("image","deleted");
        }

        return Uri.parse(path);
    }
    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            //Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }


}