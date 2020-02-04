package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sba.sinhalaphotoeditor.R;


public class CreateABackgroundActivity extends AppCompatActivity {

    ImageView userCreatedImage;
    Button create;
    EditText width,height;
    int color = -99;
    Bitmap createdBitmap = null;
    ImageView pickColor;
    Button useImage;
    Boolean isImageCreating = false;

    private InterstitialAd mInterstitialAd;


    @Override
    public void onBackPressed()
    {
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_abackground);




        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3538783908730049/5147080745");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();


        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int widtha = metrics.widthPixels;



        create = (Button) findViewById(R.id.create);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        userCreatedImage = (ImageView) findViewById(R.id.userCreatedImage);
        pickColor = (ImageView) findViewById(R.id.pickColor);
        useImage = (Button) findViewById(R.id.useImage);


        ImageView topGreenPannel;
        topGreenPannel = findViewById(R.id.topGreenPannel);
        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);


        userCreatedImage.setMaxWidth(widtha);

        useImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                useImage.setEnabled(false);
                if(createdBitmap != null)
                {
                    if(mInterstitialAd.isLoaded())
                    {
                        mInterstitialAd.show();
                        useImage.setEnabled(true);
                    }
                    else
                    {
                        EditorActivity.imageWidth = Integer.parseInt(width.getText().toString());
                        EditorActivity.imageHeight = Integer.parseInt(height.getText().toString());

                        MainActivity.images.clear();
                        //MainActivity.filePaths.clear();
                        MainActivity.imagePosition = 0;
                        MainActivity.images.add(createdBitmap);
                        //MainActivity.filePaths.add(getImageUri(getApplicationContext(),createdBitmap));
                        // MainActivity.CurrentWorkingFilePath = getImageUri(getApplicationContext(),createdBitmap);

                        startActivity(new Intent(getApplicationContext(), EditorActivity.class));
                        finish();
                    }


                }
            }
        });

        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseColor();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isImageCreating)
                {
                    return;
                }
                if(width.getText().toString().equals(""))
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);

                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.enter_valid_width));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "Enter a valid width", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(height.getText().toString().equals(""))
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);

                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.enter_valid_height));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "Enter a valid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(color == -99)
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.pick_color_text));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "Pick a color", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(width.getText().toString()) > 1000)
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.width_exceed_text));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "You can only create of maximum width of 1000", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(height.getText().toString()) > 1000)
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.height_exceed_text));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "You can only create of maximum height of 1000", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(width.getText().toString()) < 400)
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.width_is_low_text));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "You can only create of minimum width of 400", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(height.getText().toString()) < 400)
                {
                    View view = getLayoutInflater().inflate(R.layout.toast_layout,null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText(getResources().getString(R.string.height_is_low_text2));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    //Toast.makeText(CreateABackgroundActivity.this, "You can only create of minimum height of 400", Toast.LENGTH_SHORT).show();
                    return;
                }


                String hex = "#" + Integer.toHexString(color);
                createdBitmap = createImage(Integer.parseInt(width.getText().toString()),Integer.parseInt(height.getText().toString()),hex);
                userCreatedImage.setImageBitmap(createdBitmap);


            }
        });


        width.setText("1000");
        height.setText("1000");
        color = Color.parseColor("#2dcb70");
        createdBitmap = createImage(1000,1000,"#2dcb70");
        userCreatedImage.setImageBitmap(createdBitmap);


    }
    public  Bitmap createImage(int width, int height, String color)
    {
        isImageCreating = true;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        isImageCreating = false;
        return bitmap;
    }
    public void chooseColor()
    {
        ColorPickerDialogBuilder.with(CreateABackgroundActivity.this)
                .setTitle("Choose Color")
                .initialColor(Color.parseColor("#2dcb70"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(20)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i)
                    {
                        color = i;
                    }
                })
                .setPositiveButton("Ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers)
                    {
                        color = i;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                }).build().show();
    }

}
