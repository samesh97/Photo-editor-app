package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import static com.sba.sinhalaphotoeditor.config.Constants.APP_MARKET_LINK;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class ImageSavingActivity extends AppCompatActivity {


    private Methods methods;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_saving);

        methods = new Methods(ImageSavingActivity.this);



        ImageView saveFinalImage = (ImageView) findViewById(R.id.saveFinalImage);
        ImageView userSavingImage = (ImageView) findViewById(R.id.userSavingImage);

        TextView fromGalery = findViewById(R.id.fromGalery);

        ImageView shareImageView = findViewById(R.id.shareImageView);

        ImageView downloadImageView = findViewById(R.id.downloadImageView);

        TextView downloadImageText = findViewById(R.id.downloadImageText);



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


        ImageView shareFinalImage = (ImageView) findViewById(R.id.shareFinalImage);

        shareFinalImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareImageUri(getImageUri(getApplicationContext(),ImageList.getInstance().getCurrentBitmap()));
            }
        });


        Glide.with(getApplicationContext()).load(ImageList.getInstance().getCurrentBitmap()).into(userSavingImage);
        userSavingImage.setClipToOutline(true);




        saveFinalImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();

            }
        });


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


        Typeface typeface = Methods.getDefaultTypeFace(getApplicationContext());

        TextView message = view.findViewById(R.id.textView8);

        message.setText(getResources().getString(R.string.rate_app_description));
        message.setTypeface(typeface);

        TextView title = view.findViewById(R.id.textView7);


        ImageView imageView6 = view.findViewById(R.id.imageView6);
        imageView6.setImageDrawable(getDrawable(R.drawable.download_image_cartoon));

        title.setText(getResources().getString(R.string.rate_app_text));
        title.setTypeface(typeface);

        Button yes = view.findViewById(R.id.yesButton);
        Button no = view.findViewById(R.id.noButton);

        yes.setText(getResources().getString(R.string.okay_text));
        yes.setTypeface(typeface);
        no.setText(getResources().getString(R.string.cannot_text));
        no.setTypeface(typeface);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                methods.SaveImage(ImageList.getInstance().getCurrentBitmap(),getContentResolver());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_MARKET_LINK)));

            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                methods.SaveImage(ImageList.getInstance().getCurrentBitmap(),getContentResolver());

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

        return Uri.parse(path);
    }
}
