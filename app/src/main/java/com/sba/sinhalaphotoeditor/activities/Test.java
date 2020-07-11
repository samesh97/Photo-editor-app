package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.custom.views.ImageViewPlus;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ImageViewPlus imageViewPlus = findViewById(R.id.imageViewPlus);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.download);
        imageViewPlus.setBitmap(bitmap);
    }
}