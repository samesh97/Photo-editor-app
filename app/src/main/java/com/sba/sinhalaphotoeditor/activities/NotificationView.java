package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.util.Locale;

import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_MESSAGE_TEXT;
import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_TITLE_TEXT;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class NotificationView extends AppCompatActivity
{

    private TextView titleTextView,descriptionTextView;
    private Intent intent;

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(NotificationView.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
        setContentView(R.layout.activity_notification_view);

        Log.d("intentResult","NotificationView_was_executed");

        initViews();
        intent = getIntent();
        if(intent != null)
        {
            setData();
        }


    }

    private void setData()
    {
        String title = intent.getStringExtra(FIREBASE_PAYLOAD_TITLE_TEXT);
        String message = intent.getStringExtra(FIREBASE_PAYLOAD_MESSAGE_TEXT);


        if(title == null || message == null || (title.equals("") || title.trim().equals("") || message.equals("") || message.trim().equals("")))
        {
            Intent intent = new Intent(NotificationView.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            titleTextView.setText("" + title);
            descriptionTextView.setText("" + message);
        }
    }

    private void initViews()
    {
        titleTextView = findViewById(R.id.title);
        descriptionTextView = findViewById(R.id.description);
    }
}
