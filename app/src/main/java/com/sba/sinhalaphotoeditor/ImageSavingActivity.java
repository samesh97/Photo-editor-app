package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import render.animations.Flip;
import render.animations.Render;

public class ImageSavingActivity extends AppCompatActivity {

    ImageView saveFinalImage;
    ImageView userSavingImage;
    ImageView shareFinalImage;


    ImageView shareImageView;
    TextView fromGalery;

    Render render;

    ImageView downloadImageView;

    TextView downloadImageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_saving);


        setTextViewFontAndSize();

        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();

        render = new Render(ImageSavingActivity.this);

        saveFinalImage = (ImageView) findViewById(R.id.saveFinalImage);
        userSavingImage = (ImageView) findViewById(R.id.userSavingImage);

        fromGalery = findViewById(R.id.fromGalery);

        shareImageView = findViewById(R.id.shareImageView);

        downloadImageView = findViewById(R.id.downloadImageView);

        downloadImageText = findViewById(R.id.downloadImageText);


        ImageView topGreenPannel;
        topGreenPannel = findViewById(R.id.topGreenPannel);
        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);


        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition)));
            }
        });

        fromGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition)));
            }
        });

        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle(getResources().getString(R.string.rate_app_text))
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage(getResources().getString(R.string.rate_app_description))
                        .setPositiveButton(getResources().getString(R.string.okay_text), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton(getResources().getString(R.string.cannot_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                    }
                }).show();
            }
        });

        downloadImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle(getResources().getString(R.string.rate_app_text))
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage(getResources().getString(R.string.rate_app_description))
                        .setPositiveButton(getResources().getString(R.string.okay_text), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton(getResources().getString(R.string.cannot_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                    }
                }).show();
            }
        });




        shareFinalImage = (ImageView) findViewById(R.id.shareFinalImage);

        shareFinalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition)));
            }
        });

        //userSavingImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
        Glide.with(getApplicationContext()).load(MainActivity.images.get(MainActivity.imagePosition)).into(userSavingImage);

        saveFinalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle(getResources().getString(R.string.rate_app_text))
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage(getResources().getString(R.string.rate_app_description))
                        .setPositiveButton(getResources().getString(R.string.okay_text), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton(getResources().getString(R.string.cannot_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                    }
                }).show();

            }
        });

        render.setAnimation(Flip.InX(userSavingImage));
        render.start();





    }
    private void shareImageUri(Uri uri)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
    private void SaveImage(Bitmap finalBitmap)
    {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/SinhalaPhotoEditor");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".PNG";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(ImageSavingActivity.this, "Saved to gallery", Toast.LENGTH_SHORT).show();
// Tell the media scanner about the new file so that it is
// immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);


                    }
                });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
/*

        String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
        Log.d("image",realPath);

        File file = new File(realPath);
        if(file.exists())
        {
            file.delete();
            Log.d("image","deleted");
        }*/

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
    public void setTextViewFontAndSize()
    {

        TextView downloadImageText = findViewById(R.id.downloadImageText);
        TextView fromGalery = findViewById(R.id.fromGalery);


        Typeface typeface;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        int pos = pref.getInt("LanguagePosition",-99);
        if(pos != 99) {
            switch (pos) {
                case 1:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibresemibold);
                    downloadImageText.setTypeface(typeface);
                    fromGalery.setTypeface(typeface);

                    break;
                case 2:

                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.englishfont);
                    downloadImageText.setTypeface(typeface);


                    fromGalery.setTypeface(typeface);





                    break;
                case 3:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.tamilfont);


                    downloadImageText.setTypeface(typeface);
                    downloadImageText.setTextSize(20);

                    fromGalery.setTypeface(typeface);
                    fromGalery.setTextSize(14);

                    break;
            }
        }
    }
}
