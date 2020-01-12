package com.sba.sinhalaphotoeditor.welcomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.MainActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.walkthrough.WalkThroughActivity;

import java.util.Locale;

public class WelcomeScreen extends AppCompatActivity {

    private int selectedLanguagePos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);


        //setTextViewFontAndSize();

        Button proceed = findViewById(R.id.proceed);
        Spinner languagePicker = findViewById(R.id.languagePicker);
        ImageView topGreenPannel = findViewById(R.id.topGreenPannel);


        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);

        languagePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedLanguagePos = position;
                switch (position)
                {
                    case 1:
                        setLocale("si",position);
                        break;
                    case 2:
                        setLocale("en",position);
                        break;
                    case 3:
                        setLocale("ta",position);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(selectedLanguagePos == 0)
                {
                    Toast.makeText(WelcomeScreen.this, "Select a language first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(WelcomeScreen.this, WalkThroughActivity.class));
                }
            }
        });
    }
    public void setLocale(String lang,int position)
    {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("LanguagePosition",position);
        editor.putString("Language",lang);

        editor.apply();

    }
    public void setTextViewFontAndSize()
    {

        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        Button proceed = findViewById(R.id.proceed);


        Typeface typeface;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        int pos = pref.getInt("LanguagePosition",-99);
        if(pos != 99) {
            switch (pos) {
                case 1:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibresemibold);
                    textView2.setTypeface(typeface);
                    textView3.setTypeface(typeface);
                    proceed.setTypeface(typeface);

                    break;
                case 2:

                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.englishfont);
                    textView2.setTypeface(typeface);


                    textView3.setTypeface(typeface);
                    proceed.setTypeface(typeface);





                    break;
                case 3:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.tamilfont);


                    textView2.setTypeface(typeface);
                    textView2.setTextSize(20);

                    textView3.setTypeface(typeface);
                    textView3.setTextSize(14);

                    proceed.setTextSize(14);
                    proceed.setTypeface(typeface);

                    break;
            }
        }
    }
}
