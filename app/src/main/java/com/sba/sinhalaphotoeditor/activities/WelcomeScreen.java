package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;

import java.util.Locale;

import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_ENGLISH;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_POSITION_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_TAMIL;
import static com.sba.sinhalaphotoeditor.Config.Constants.SHARED_PREF_NAME;

public class WelcomeScreen extends AppCompatActivity {


    private  Button sinhala,english;
    private Drawable drawable,drawable2;
    private static boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadLocale();
        setContentView(R.layout.activity_welcome_screen);


        sinhala = findViewById(R.id.sinhala);
        english = findViewById(R.id.english);
        drawable = getDrawable(R.drawable.left_side_corner_round_blue_background);
        drawable2 = getDrawable(R.drawable.right_side_corner_round_blue_background);


        sinhala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isUpdate = true;
                onLanguageChanged(LANGUAGE_SINHALA);
                setLocale(LANGUAGE_SINHALA);

            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isUpdate = true;
                onLanguageChanged(LANGUAGE_ENGLISH);
                setLocale(LANGUAGE_ENGLISH);
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String lan = pref.getString(LANGUAGE_KEY,LANGUAGE_SINHALA);
        if(lan.equals(LANGUAGE_ENGLISH))
        {
            onLanguageChanged(LANGUAGE_ENGLISH);
        }
        else
        {
            onLanguageChanged(LANGUAGE_SINHALA);
        }


        Button proceed = findViewById(R.id.proceed);


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                    startActivity(new Intent(WelcomeScreen.this, WalkThroughActivity.class));
                    overridePendingTransition(R.anim.activity_start_animation,R.anim.activity_exit_animation);
            }
        });

        changeTypeFace();
    }
    public void setLocale(String lang)
    {

        //set locale
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);



        //save locale
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LANGUAGE_KEY,lang);
        editor.apply();

        if(isUpdate)
        {
            isUpdate = false;
            refreshActivity();
        }



    }
    public void loadLocale()
    {
        //get locale
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String lan = pref.getString(LANGUAGE_KEY,null);
        if(lan != null)
        {
            isUpdate = false;
            setLocale(lan);
        }
        else
        {
            isUpdate = true;
            setLocale(LANGUAGE_SINHALA);
        }

    }
    public void onLanguageChanged(String language)
    {

        if(language.equals(LANGUAGE_SINHALA))
        {
            sinhala.setBackground(drawable);
            english.setBackground(null);
        }
        else if(language.equals(LANGUAGE_ENGLISH))
        {
            english.setBackground(drawable2);
            sinhala.setBackground(null);
        }

    }
    public void refreshActivity()
    {
        //refresh activity with  no animation
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
    private void changeTypeFace()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String language = pref.getString(LANGUAGE_KEY,LANGUAGE_SINHALA);
        Typeface typeface = null;

        if(language.equals(LANGUAGE_ENGLISH))
        {
            //english
            typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.englishfont);
        }
        else
        {
            //sinhala
            typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.bindumathi);
        }

        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        Button proceed = findViewById(R.id.proceed);

        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        proceed.setTypeface(typeface);


    }

}
