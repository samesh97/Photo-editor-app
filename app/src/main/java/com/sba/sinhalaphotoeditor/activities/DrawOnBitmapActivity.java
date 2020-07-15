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
import android.os.AsyncTask;
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

import static android.view.View.LAYER_TYPE_HARDWARE;
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

    private float scaleFactor;

    private ConstraintLayout img_done_container;


    @Override
    protected void onDestroy() {
        super.onDestroy();
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


        img_done_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                img_done_container.setEnabled(false);
                loading.setVisibility(View.VISIBLE);

                if(paintView.getPathSize() > 0)
                {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run()
                        {
                            Bitmap bitmap = paintView.getBitmap();
                            AddImageToArrayListAsyncTask asyncTask = new AddImageToArrayListAsyncTask(bitmap,DrawOnBitmapActivity.this);
                            asyncTask.execute();
                        }
                    });

                }
                else
                {
                    startActivityForResult();
                }

            }
        });




        ConstraintLayout img_undo_container = findViewById(R.id.img_undo_container);
        img_undo_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.undo();
            }
        });

        ConstraintLayout img_redo_container = findViewById(R.id.img_redo_container);
        img_redo_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.redo();
            }
        });




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
                paintView.disableEraser();
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
            paintView.disableEraser();

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
    public void startActivityForResult()
    {
        img_done.setEnabled(true);
        setResult(Activity.RESULT_OK);
        finish();
        loading.setVisibility(View.GONE);
    }
    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch

            detector.setQuickScaleEnabled(true);
            scaleFactor *= (detector.getScaleFactor());
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
            scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //
            return true;
        }
    }
    public void setFocusXAndY(float x, float y)
    {
        if(paintView.getScaleX() == 2 && paintView.getScaleY() == 2)
        {
            paintView.animate().scaleX(1).scaleY(1).start();
        }
        else
        {
            paintView.animate().scaleX(2).scaleY(2).start();
        }

    }

}
