package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;

import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.custom.views.CropView;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

public class CropActivity extends AppCompatActivity {

    private CropView crop_view;
    public static Bitmap croppedBitmap;
    private int rotateDegree = 0;


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        crop_view = findViewById(R.id.crop_view);
        crop_view.setBitmap(MyCustomGallery.selectedBitmap);

        ConstraintLayout img_done_container = findViewById(R.id.img_done_container);
        img_done_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                croppedBitmap = crop_view.getBitmap();
                setResult(RESULT_OK);
                finish();
            }
        });

        ConstraintLayout rotate_container = findViewById(R.id.rotate_container);
        rotate_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(rotateDegree == 0)
                {
                    rotateDegree = 90;
                }
                else if(rotateDegree == 90)
                {
                    rotateDegree = 180;
                }
                else if(rotateDegree == 180)
                {
                    rotateDegree = 270;
                }
                else if(rotateDegree == 270)
                {
                    //360 is also = 0
                    rotateDegree = 0;
                }


                Matrix matrix = new Matrix();
                matrix.preRotate(rotateDegree);
                Bitmap b = MyCustomGallery.selectedBitmap.copy(MyCustomGallery.selectedBitmap.getConfig(),true);


                int width = b.getWidth();
                int height = b.getHeight();


                b = Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);
                crop_view.setBitmap(b);
            }
        });

        ConstraintLayout flip_horizontal_container = findViewById(R.id.flip_horizontal_container);
        flip_horizontal_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crop_view.flipInHorizontal();
            }
        });

        ConstraintLayout flip_vertical_container = findViewById(R.id.flip_vertical_container);
        flip_vertical_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crop_view.flipInVertical();
            }
        });
    }
}