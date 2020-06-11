package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.ByteArrayOutputStream;

import render.animations.Flip;
import render.animations.Render;

public class ImageSavingActivity extends AppCompatActivity {

    private ImageView saveFinalImage;
    private ImageView userSavingImage;
    private ImageView shareFinalImage;


    private ImageView shareImageView;
    private TextView fromGalery;

    private Render render;

    private ImageView downloadImageView;

    private TextView downloadImageText;

    private Methods methods;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_saving);

        methods = new Methods(ImageSavingActivity.this);

        setTextViewFontAndSize();





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
                shareImageUri(getImageUri(getApplicationContext(), ImageList.getInstance().getCurrentBitmap()));
            }
        });

        fromGalery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),ImageList.getInstance().getCurrentBitmap()));
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
                shareImageUri(getImageUri(getApplicationContext(),ImageList.getInstance().getCurrentBitmap()));
            }
        });

        //userSavingImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
        Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSavingImage);

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
        if(dialog.getWindow() != null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // dialog.getWindow().setDimAmount(0.4f);
            float deviceWidth = Methods.getDeviceWidthInPX(getApplicationContext());
            int finalWidth = (int) (deviceWidth - (deviceWidth / 8));
            dialog.getWindow().setLayout(finalWidth,RelativeLayout.LayoutParams.WRAP_CONTENT);
        }


        View view = getLayoutInflater().inflate(R.layout.exit_dialog_layout,null);

        dialog.setContentView(view);


        TextView message = view.findViewById(R.id.textView8);

        message.setText(getResources().getString(R.string.rate_app_description));

        TextView title = view.findViewById(R.id.textView7);


        ImageView imageView6 = view.findViewById(R.id.imageView6);
        imageView6.setImageDrawable(getDrawable(R.drawable.download_image_cartoon));



       // ImageView icon = view.findViewById(R.id.imageView4);
       // icon.setImageResource(R.drawable.ic_star_half);


        title.setText(getResources().getString(R.string.rate_app_text));

        Button yes = view.findViewById(R.id.yesButton);
        Button no = view.findViewById(R.id.noButton);

        yes.setText(getResources().getString(R.string.okay_text));
        no.setText(getResources().getString(R.string.cannot_text));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                methods.SaveImage(ImageList.getInstance().getCurrentBitmap());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.sba.sinhalaphotoeditor")));
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                methods.SaveImage(ImageList.getInstance().getCurrentBitmap());
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
