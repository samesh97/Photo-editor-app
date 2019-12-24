package com.sba.sinhalaphotoeditor;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import render.animations.*;
import render.animations.Zoom;

public class AdjustImage extends AppCompatActivity {

    ImageView adjustImage;

    TickSeekBar listner1,listener2;
    float blurValue = 0.0f;
    float contrastValue = 0.0f;

    float preblurValue = 0.0f;
    float precontrastValue = 0.0f;

    Bitmap filterAddingBitmap = null;
    Bitmap currentEditingImage = null;
    Bitmap blurAddedImage = null;
    Bitmap contrastAddedImage = null;

    ProgressDialog dialog;

    DatabaseHelper helper = new DatabaseHelper(AdjustImage.this);

    Render render;

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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
            new RunInBackground().execute();
        }
        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_image);


        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));


        render = new Render(AdjustImage.this);

        dialog = new ProgressDialog(AdjustImage.this);

        adjustImage = (ImageView) findViewById(R.id.adjustImage);
        listner1 = (TickSeekBar) findViewById(R.id.listener);
        listener2 = (TickSeekBar) findViewById(R.id.listener2);
        filterAddingBitmap = MainActivity.images.get(MainActivity.imagePosition);
        adjustImage.setImageBitmap(filterAddingBitmap);

        render.setAnimation(Flip.InX(adjustImage));
        render.start();



        listner1.setOnSeekChangeListener(new OnSeekChangeListener()
        {
            @Override
            public void onSeeking(SeekParams seekParams)
            {
                preblurValue = blurValue;
                blurValue = seekParams.progressFloat;

                if(contrastValue > 0)
                {
                    if(precontrastValue > contrastValue)
                    {
                        contrastAddedImage = filterAddingBitmap;
                    }
                    blurAddedImage = new BlurUtils().blur(AdjustImage.this,contrastAddedImage, seekParams.progressFloat);
                }
                else
                {
                    blurAddedImage = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, seekParams.progressFloat);
                }

                currentEditingImage = blurAddedImage;
                adjustImage.setImageBitmap(currentEditingImage);
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
            }

        });

        listener2.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams)
            {

                precontrastValue = contrastValue;
                contrastValue = seekParams.progressFloat;
                if(blurValue > 0)
                {
                    if(preblurValue > blurValue)
                    {
                        blurAddedImage = filterAddingBitmap;
                    }
                    contrastAddedImage = addSaturation(blurAddedImage,contrastValue);
                }
                else
                {
                    contrastAddedImage = addSaturation(filterAddingBitmap,contrastValue);
                }

                currentEditingImage = contrastAddedImage;
                adjustImage.setImageBitmap(currentEditingImage);
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

            }
        });










    }
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }
    public class RunInBackground extends AsyncTask<Void,Void,Void>
    {
        Bitmap bitmap;


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Loading..");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //dialog.dismiss();

            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    //get Date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    if(helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime))
                    {
                        bitmap = null;
                    }
                    MainActivity.deleteUndoRedoImages();

                    GlideBitmapPool.clearMemory();



                }
            });



        }

        @Override
        protected Void doInBackground(Void... voids)
        {


            if(blurValue > 0 && contrastValue > 0)
            {
                bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                bitmap = addSaturation(bitmap,contrastValue);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e) {

                    }
                }

                MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
                MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }
            else if(blurValue > 0)
            {
                bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                //bitmap = changeBitmapContrastBrightness(bitmap,contrastValue,0);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e) {

                    }
                }

                MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
                MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }
            else if(contrastValue > 0)
            {
                //Bitmap bitmap = new BlurUtils().blur(AdjustImage.this,filterAddingBitmap, blurValue);
                bitmap = addSaturation(filterAddingBitmap,contrastValue);
                MainActivity.imagePosition++;
                MainActivity.images.add(MainActivity.imagePosition, bitmap);
                if (EditorActivity.isNeededToDelete) {
                    try {
                        MainActivity.images.remove(MainActivity.imagePosition + 1);
                    } catch (Exception e)
                    {
                        Log.d("Error",e.getMessage());
                    }
                }

                MainActivity.CurrentWorkingFilePath = getImageUri(AdjustImage.this, filterAddingBitmap);
                MainActivity.filePaths.add(getImageUri(AdjustImage.this, filterAddingBitmap));
            }


            Intent intent = new Intent();
            setResult(11,intent);
            finish();


            return null;
        }
    }
    private Bitmap addSaturation(Bitmap src, float settingSat) {

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult =
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        return bitmapResult;
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
