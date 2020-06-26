package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

import com.github.chuross.library.ExpandableLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sba.sinhalaphotoeditor.callbacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.callbacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.custom.views.PaintView;
import com.sba.sinhalaphotoeditor.adapters.TextColorAdapter;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.model.TextViewPlus;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;

public class DrawOnBitmapActivity extends AppCompatActivity implements OnTextAttributesChangedListner, OnAsyncTaskState {

    private PaintView paintView;
    private ExpandableLayout explandableLayout;
    TextViewPlus textViewPlus;
    private int preProgress = 12;
    private int opacityLevel = 100;
    private boolean isBrushSelected = true;
    private ImageView brush;
    private ImageView eraser;
    private ImageView img_done;
    private ProgressBar loading;

    private float scaleFactor,prevScale = 0f;
    private float focusX,focusY = 0f;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean userNeedToZoom = false;

    private boolean isZoomIn = true;
    private Timer timer;
    private ConstraintLayout img_done_container;


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(timer != null)
        {
            timer.cancel();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_on_bitmap);

        Methods.freeUpMemory();

        textViewPlus = new TextViewPlus(DrawOnBitmapActivity.this);
        textViewPlus.setTextColor(Color.RED);

        loading = findViewById(R.id.loading);
        img_done = findViewById(R.id.img_done);
        paintView = findViewById(R.id.paintView);
        explandableLayout = findViewById(R.id.explandableLayout);
        img_done_container = findViewById(R.id.img_done_container);
        paintView.setUserBitmap(DrawOnBitmapActivity.this,ImageList.getInstance().getCurrentBitmap());

        explandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                animateExpandableLayout();
                showBottomSheet();
            }
        });

//        img_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//
//                img_done_container.setEnabled(false);
//                img_done.setEnabled(false);
//                loading.setVisibility(View.VISIBLE);
//
//                Bitmap bitmap = paintView.getBitmap();
//                AddImageToArrayListAsyncTask asyncTask = new AddImageToArrayListAsyncTask(bitmap,DrawOnBitmapActivity.this);
//                asyncTask.execute();
//
//            }
//        });

        img_done_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                img_done_container.setEnabled(false);
//                img_done.setEnabled(false);
                loading.setVisibility(View.VISIBLE);

                Bitmap bitmap = paintView.getBitmap();
                AddImageToArrayListAsyncTask asyncTask = new AddImageToArrayListAsyncTask(bitmap,DrawOnBitmapActivity.this);
                asyncTask.execute();
            }
        });

        Switch isZooming = findViewById(R.id.isZooming);

        isZooming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                userNeedToZoom = isChecked;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(DrawOnBitmapActivity.this,new simpleOnScaleGestureListener());


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                if(userNeedToZoom && scaleFactor > 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            paintView.setScaleX(scaleFactor);
//                            paintView.setScaleY(scaleFactor);

                            paintView.animate().scaleY(scaleFactor).scaleX(scaleFactor).start();
                            //paintView.animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).setDuration(0).start();
                            if(focusX > 0 && focusY > 0)
                            {
                                paintView.animate().translationX(focusX).translationY(focusY).start();
                            }
                        }
                    });

                }

            }
        }, 0, 500);




    }
    private void showBottomSheet()
    {
        BottomSheetDialog dialog = new BottomSheetDialog(DrawOnBitmapActivity.this,R.style.CustomBottomSheetDialogTheme);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                undoAnimateExpandableLayout();
            }
        });
        if(dialog.getWindow() != null)
        {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setDimAmount(0.0f);
        }

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_draw_bitmap_bottom_sheet,null,false);

        RecyclerView color_recycler_view = view.findViewById(R.id.color_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(DrawOnBitmapActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        color_recycler_view.setLayoutManager(manager);
        TextColorAdapter adapter = new TextColorAdapter(getApplicationContext(),textViewPlus,this);
        color_recycler_view.setAdapter(adapter);

        final SeekBar brush_size_seekbar = view.findViewById(R.id.brush_size_seekbar);
        brush_size_seekbar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        brush_size_seekbar.setProgress(preProgress);

        final SeekBar opacity_seekbar = view.findViewById(R.id.opacity_seekbar);
        opacity_seekbar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        opacity_seekbar.setProgress(opacityLevel);

        brush_size_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(progress >= 5)
                {
                    preProgress = progress;
                    paintView.setSizeBrush(progress);
                }
                else
                {
                    brush_size_seekbar.setProgress(5);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        opacity_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress >= 10)
                {
                    opacityLevel = progress;
                    int opacity = ((int)(opacityLevel * 2.5f));
                    paintView.setOpacity(opacity);
                }
                else
                {
                    opacity_seekbar.setProgress(10);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brush = view.findViewById(R.id.brush);
        eraser = view.findViewById(R.id.eraser);

        if(isBrushSelected)
        {
            brush.setBackground(getResources().getDrawable(R.drawable.brush_type_selected_background));
        }
        else
        {
            eraser.setBackground(getResources().getDrawable(R.drawable.brush_type_selected_background));
        }



        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.desableEraser();
                brush.setBackground(getResources().getDrawable(R.drawable.brush_type_selected_background));
                eraser.setBackground(null);

                isBrushSelected = true;
            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.enableEraser();
                eraser.setBackground(getResources().getDrawable(R.drawable.brush_type_selected_background));
                brush.setBackground(null);

                isBrushSelected = false;
            }
        });



        int deviceHeight = (int) Methods.getDeviceHeightInPX(getApplicationContext());
        dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,deviceHeight / 2));
        dialog.show();
    }
    private void animateExpandableLayout()
    {
        if(explandableLayout != null)
        {
            explandableLayout.animate().setDuration(500).translationYBy(0).translationY(200).start();
        }
    }
    private void undoAnimateExpandableLayout()
    {
        if(explandableLayout != null)
        {
            explandableLayout.animate().setDuration(500).translationYBy(200).translationY(0).start();
        }
    }

    @Override
    public void onTextColorChanged(int color) {

        if(!isBrushSelected)
        {
            isBrushSelected = true;
            paintView.desableEraser();

            if(brush != null && eraser != null)
            {
                brush.setBackground(getResources().getDrawable(R.drawable.brush_type_selected_background));
                eraser.setBackground(null);
            }

        }
        paintView.setBrushColor(color);
        textViewPlus.setTextColor(color);
    }
    @Override
    public void onTextShadowColorChanged(int color) {

    }
    @Override
    public void onTextFontChanged(Typeface typeFace) {

    }

    @Override
    public void startActivityForResult() {
        img_done.setEnabled(true);
        setResult(Activity.RESULT_OK);
        finish();
        loading.setVisibility(View.GONE);
    }
    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch

            detector.setQuickScaleEnabled(true);

            scaleFactor *= (detector.getScaleFactor());
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
            scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //


            //focusX = detector.getFocusX();
            //focusY = detector.getFocusY();
            return true;
        }
    }
    public void setMotionEvent(final MotionEvent event)
    {
        isZoomIn = true;
        //focusX = event.getX();
        //focusY = event.getY();
        scaleGestureDetector.onTouchEvent(event);
    }
    public void setFocusXAndY(float x, float y)
    {
//        focusX = x;
//        focusY = y;
//
//        Log.d("TouchPos","X=" + x);
//        Log.d("TouchPos","Y=" + y);
//
//        int[] location = new int[2];
//        paintView.getLocationOnScreen(location);
//        int dx = location[0];
//        int dy = location[1];
//
//        Log.d("TouchPos","DX=" + dx);
//        Log.d("TouchPos","DY=" + dy);
    }
    public boolean getUserWantToZoomOrDrawState()
    {
        return userNeedToZoom;
    }
    public void zoomOver(boolean zoomState,MotionEvent event)
    {
        isZoomIn = zoomState;
    }
}
