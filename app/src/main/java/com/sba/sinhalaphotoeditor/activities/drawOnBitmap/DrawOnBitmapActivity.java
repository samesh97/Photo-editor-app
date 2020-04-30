package com.sba.sinhalaphotoeditor.activities.drawOnBitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chuross.library.ExpandableLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.CallBacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.TextOnImageActivity;
import com.sba.sinhalaphotoeditor.adapters.ShadowColorAdapter;
import com.sba.sinhalaphotoeditor.adapters.TextColorAdapter;
import com.sba.sinhalaphotoeditor.adapters.TextFontAdapter;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.model.TextViewPlus;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import static com.sba.sinhalaphotoeditor.activities.EditorActivity.DRAW_ON_BITMAP_REQUEST_CODE;

public class DrawOnBitmapActivity extends AppCompatActivity implements OnTextAttributesChangedListner, OnAsyncTaskState {

    private PaintView paintView;
    private ExpandableLayout explandableLayout;
    TextViewPlus textViewPlus;
    private int preProgress = 12;
    private boolean isBrushSelected = true;
    private ImageView brush;
    private ImageView eraser;
    private ImageView img_done;
    private ProgressBar loading;

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
        paintView.setUserBitmap(ImageList.getInstance().getCurrentBitmap());

        explandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                animateExpandableLayout();
                showBottomSheet();
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_done.setEnabled(false);
                loading.setVisibility(View.VISIBLE);

                Bitmap bitmap = paintView.getBitmap();
                AddImageToArrayListAsyncTask asyncTask = new AddImageToArrayListAsyncTask(bitmap,DrawOnBitmapActivity.this);
                asyncTask.execute();

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
            dialog.getWindow().setDimAmount(0.0f);
        }

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_draw_bitmap_bottom_sheet,null,false);

        RecyclerView color_recycler_view = view.findViewById(R.id.color_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(DrawOnBitmapActivity.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        color_recycler_view.setLayoutManager(manager);
        TextColorAdapter adapter = new TextColorAdapter(getApplicationContext(),textViewPlus,this);
        color_recycler_view.setAdapter(adapter);

        SeekBar brush_size_seekbar = view.findViewById(R.id.brush_size_seekbar);
        brush_size_seekbar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        brush_size_seekbar.setProgress(preProgress);

        brush_size_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                preProgress = progress;
                paintView.setSizeBrush(progress);
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



        dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400));
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
}
