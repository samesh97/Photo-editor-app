package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

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

        render = new Render(ImageSavingActivity.this);

        saveFinalImage = (ImageView) findViewById(R.id.saveFinalImage);
        userSavingImage = (ImageView) findViewById(R.id.userSavingImage);

        fromGalery = findViewById(R.id.fromGalery);

        shareImageView = findViewById(R.id.shareImageView);

        downloadImageView = findViewById(R.id.downloadImageView);

        downloadImageText = findViewById(R.id.downloadImageText);

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
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle("ඇප් එක Rate කරන්න")
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage("මෙවැනි Apps තවත් නිර්මාණය කිරිමට අපට ඔබගේ වටිනා Rate එක ලබා දෙන්න!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle("ඇප් එක Rate කරන්න")
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage("මෙවැනි Apps තවත් නිර්මාණය කිරිමට අපට ඔබගේ වටිනා Rate එක ලබා දෙන්න!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        userSavingImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));

        saveFinalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(ImageSavingActivity.this).setIcon(R.drawable.ic_star_half).setTitle("ඇප් එක Rate කරන්න")
                        .setIcon(R.drawable.ic_star_half)
                        .setMessage("මෙවැනි Apps තවත් නිර්මාණය කිරිමට අපට ඔබගේ වටිනා Rate එක ලබා දෙන්න!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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
    private boolean saveFile(Bitmap sourceImageBitmap, String fileName)
    {


        ActivityCompat.requestPermissions(ImageSavingActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                211);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root+"/DCIM/SinhalaPhotoEditor/");
        myDir.mkdirs();
        String fname = "Image-" + fileName+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            sourceImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e)
        {

            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }
    private void shareImageUri(Uri uri)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
}
