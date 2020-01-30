package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import render.animations.Flip;
import render.animations.Render;

import static com.sba.sinhalaphotoeditor.EditorActivity.screenHeight;

public class ImageSavingActivity extends AppCompatActivity {

    ImageView saveFinalImage;
    ImageView userSavingImage;
    ImageView shareFinalImage;


    ImageView shareImageView;
    TextView fromGalery;

    Render render;

    ImageView downloadImageView;

    TextView downloadImageText;

    private Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_saving);

        methods = new Methods(getApplicationContext());

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

        fromGalery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition)));
            }
        });

        downloadImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               showDialog();
            }
        });

        downloadImageText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });




        shareFinalImage = (ImageView) findViewById(R.id.shareFinalImage);

        shareFinalImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition)));
            }
        });

        //userSavingImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
        Glide.with(getApplicationContext()).load(MainActivity.images.get(MainActivity.imagePosition)).into(userSavingImage);

        saveFinalImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();

            }
        });

        render.setAnimation(Flip.InX(userSavingImage));
        render.start();





    }

    private void showDialog()
    {
        final Dialog dialog = new Dialog(ImageSavingActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        View view = getLayoutInflater().inflate(R.layout.exit_dialog_layout,null);

        dialog.setContentView(view);

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView message = view.findViewById(R.id.textView8);

        message.setText(getResources().getString(R.string.rate_app_description));

        TextView title = view.findViewById(R.id.textView7);


        ImageView icon = view.findViewById(R.id.imageView4);
        icon.setImageResource(R.drawable.ic_star_half);


        title.setText(getResources().getString(R.string.rate_app_text));

        Button yes = view.findViewById(R.id.yesButton);
        Button no = view.findViewById(R.id.noButton);

        yes.setText(getResources().getString(R.string.okay_text));
        no.setText(getResources().getString(R.string.cannot_text));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                methods.SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                methods.SaveImage(MainActivity.images.get(MainActivity.imagePosition));
                dialog.dismiss();
            }
        });
        dialog.show();
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
