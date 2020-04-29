package com.sba.sinhalaphotoeditor.activities.drawOnBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

public class DrawOnBitmapActivity extends AppCompatActivity {

    private PaintView paintView;
    private Button eraser;
    private Button pensil,save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_on_bitmap);


        paintView = findViewById(R.id.paintView);
        eraser = findViewById(R.id.eraser);
        pensil = findViewById(R.id.pensil);
        save = findViewById(R.id.save);


        paintView.setUserBitmap(ImageList.getInstance().getCurrentBitmap());

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.enableEraser();
            }
        });

        pensil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.desableEraser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmp = paintView.getBitmap();

                ImageList.getInstance().addBitmap(bitmp,true);
                setResult(Activity.RESULT_OK);
                finish();
               // SaveImage(bitmp);
            }
        });

    }
}
