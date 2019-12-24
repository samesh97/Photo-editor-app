package com.sba.sinhalaphotoeditor.welcomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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


        Button proceed = findViewById(R.id.proceed);
        Spinner languagePicker = findViewById(R.id.languagePicker);

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
                else if(selectedLanguagePos == 3)
                {
                    Toast.makeText(WelcomeScreen.this, "Tamil language is not supporting yet", Toast.LENGTH_SHORT).show();
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
}
