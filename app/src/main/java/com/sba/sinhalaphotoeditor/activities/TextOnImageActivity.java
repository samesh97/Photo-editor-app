package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.chuross.library.ExpandableLayout;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.CallBacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.Config.RotationGestureDetector;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.adapters.ShadowColorAdapter;
import com.sba.sinhalaphotoeditor.adapters.TextColorAdapter;
import com.sba.sinhalaphotoeditor.adapters.TextFontAdapter;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.model.TextViewPlus;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import render.animations.Bounce;
import render.animations.Render;

import static com.sba.sinhalaphotoeditor.activities.EditorActivity.screenHeight;

public class TextOnImageActivity extends AppCompatActivity
        implements RotationGestureDetector.OnRotationGestureListener,
     View.OnTouchListener,View.OnLongClickListener, OnAsyncTaskState, OnTextAttributesChangedListner
{


    public static String TEXT_TO_WRITE = "sourceText";
    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static int TEXT_ON_IMAGE_RESULT_OK_CODE = 1;
    public static int TEXT_ON_IMAGE_RESULT_FAILED_CODE = -1;
    public static int TEXT_ON_IMAGE_REQUEST_CODE = 4;


    private Uri imageOutUri;
    private String saveDir="/tmp/";
    private String textToWrite = "";
    private ImageView sourceImageView;
    private ConstraintLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    private Dialog dia;

    ArrayList<Typeface> font = new ArrayList<>();


    private boolean isBorderCheckTrue = false;
    private boolean isGlowCheckTrue = false;


    DatabaseHelper helper = new DatabaseHelper(TextOnImageActivity.this);


//    private SeekBar opacitySeekBar;
//    private  SeekBar textShadowDirectionSeekBar;
//    private SeekBar textShadowUpDownDirectionSeekBar;
    private float opacityLevel = 1f;



    private int previousShadowLevel = 0;
    private int previousUpDownShadowLevel = 0;

    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;


    private int clickedId = 1;
    private int addedTextViewCount = 0;
    private ArrayList<TextViewPlus> addedTextViews = new ArrayList<>();
    private float lastX = 0, lastY = 0;

    private Button addNewTextViewButton;
    private Typeface selectedTypeFace = null;



    @Override
    public void onBackPressed()
    {
        if(expandableLayout.isExpanded())
        {
            expandableLayout.collapse();
            expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
            expandableLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_on_image);


        addNewTextViewButton = findViewById(R.id.addNewTextViewButton);
        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);




        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openBottomSheet();

//                if(expandableLayout.isExpanded())
//                {
//                    expandableLayout.collapse();
//                    //expandIcon.setImageResource(R.drawable.slide_up_image);
//                    expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
//                    expandableLayout.setBackgroundColor(Color.TRANSPARENT);
//                }
//                else
//                {
//                    expandIcon.setBackgroundColor(Color.TRANSPARENT);
//                    expandableLayout.setBackground(getResources().getDrawable(R.drawable.white_opacity_background));
//                    expandableLayout.expand();// expand with animation
//                    //expandIcon.setImageResource(R.drawable.slide_down_image);
//
//                }



            }
        });

        addNewTextViewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                showPopup();


            }
        });












        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();



        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));
        }


        setFonts();



        progressDialog = new ProgressDialog(TextOnImageActivity.this);
        dia = new Dialog(this,R.style.DialogSlideAnim);
        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(TextOnImageActivity.this,new TextOnImageActivity.simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(TextOnImageActivity.this);
        extractBundle();
        uiSetup();
        addNewTextView(textToWrite);




    }

    private void openBottomSheet()
    {
        BottomSheetDialog dialog = new BottomSheetDialog(TextOnImageActivity.this);

        if(dialog.getWindow() != null)
        {
            dialog.getWindow().setDimAmount(0.0f);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_text_bottom_sheet,null,false);

        RecyclerView colorRV = view.findViewById(R.id.color_recycler_view);
        TextColorAdapter textColorAdapter = new TextColorAdapter(getApplicationContext(),TextOnImageActivity.this);
        LinearLayoutManager textColorManger = new LinearLayoutManager(getApplicationContext());
        textColorManger.setOrientation(RecyclerView.HORIZONTAL);
        colorRV.setLayoutManager(textColorManger);
        colorRV.setAdapter(textColorAdapter);

        RecyclerView shadowColorRV = view.findViewById(R.id.shadow_color_recycler_view);
        ShadowColorAdapter shadowColorAdapter = new ShadowColorAdapter(getApplicationContext(),TextOnImageActivity.this);
        LinearLayoutManager shadowColorManager = new LinearLayoutManager(getApplicationContext());
        shadowColorManager.setOrientation(RecyclerView.HORIZONTAL);
        shadowColorRV.setLayoutManager(shadowColorManager);
        shadowColorRV.setAdapter(shadowColorAdapter);


        RecyclerView fontsRV = view.findViewById(R.id.font_recycler_view);
        TextFontAdapter textFontAdapter = new TextFontAdapter(getApplicationContext(),"Sample",TextOnImageActivity.this);
        LinearLayoutManager textFontManager = new LinearLayoutManager(getApplicationContext());
        textFontManager.setOrientation(RecyclerView.HORIZONTAL);
        fontsRV.setLayoutManager(textFontManager);
        fontsRV.setAdapter(textFontAdapter);

        EditText current_text = view.findViewById(R.id.current_text);
        for(TextViewPlus view2 : addedTextViews)
        {
            if(view2.getTextView().getId() == clickedId)
            {
                current_text.setText(view2.getText());
            }
        }
        current_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                for(TextViewPlus view : addedTextViews)
                {
                    if(view.getTextView().getId() == clickedId)
                    {
                        view.setText(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        SeekBar opacitySeekBar = view.findViewById(R.id.opacitySeekBar);
        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float opacityLevel = (progress / 100.0f);
                for(TextViewPlus view : addedTextViews)
                {
                    if(clickedId == view.getTextView().getId())
                    {
                        view.setTextOpacity(opacityLevel);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar textShadowDirectionSeekBar = view.findViewById(R.id.textShadowDirectionSeekBar);
        textShadowDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                for(TextViewPlus view : addedTextViews)
                {
                    if(clickedId == view.getTextView().getId())
                    {

                        previousShadowLevel = progress / 2 - 25;
                        view.setShadowXDirection(previousShadowLevel);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar textShadowUpDownDirectionSeekBar = view.findViewById(R.id.textShadowUpDownDirectionSeekBar);
        textShadowUpDownDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                for(TextViewPlus view : addedTextViews)
                {
                    if(clickedId == view.getTextView().getId())
                    {
                        previousUpDownShadowLevel = (progress / 2) - 25;
                        view.setShadowYDirection(previousUpDownShadowLevel);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400));

        dialog.show();
    }

    private void setFonts()
    {
        Typeface typeface;
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.iskpota);
        font.add(typeface);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.kaputaunicode);
        font.add(typeface);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.dinaminauniweb);
        font.add(typeface);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sarasaviunicode);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.fmmalithiuw46);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.hodipotha3);
        font.add(typeface);

        //typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.lklug);
        //font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.puskolapotha2010);
        font.add(typeface);



        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.nyh);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.warna);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.winnie);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.winnie1);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sulakna);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibrebold);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibrextrabold);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibresemibold);
        font.add(typeface);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.postnobillscolombobold);
        font.add(typeface);
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        //set the rotation of the text view
        float angle = rotationDetector.getAngle();
        for(TextViewPlus textView : addedTextViews)
        {
            if(clickedId == textView.getTextView().getId())
            {
                textView.getTextView().setRotation(angle);
            }
        }

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent)
    {
        clickedId = v.getId();
        setBackgroundAsSelected();
        for(TextViewPlus view : addedTextViews)
        {
            if(clickedId == view.getTextView().getId())
            {
                switch (motionEvent.getAction())
                {
                    case (MotionEvent.ACTION_DOWN):
                                lastX = motionEvent.getX();
                                lastY = motionEvent.getY();

                                break;
                            case MotionEvent.ACTION_MOVE:
                                float dx = motionEvent.getX() - lastX;
                                float dy = motionEvent.getY() - lastY;
                                float finalX = view.getTextView().getX() + dx;
                                float finalY = view.getTextView().getY() + dy;
                                view.getTextView().setX(finalX);
                                view.getTextView().setY(finalY);
                                break;
                        }

                        return true;

            }
        }
        return false;
    }

    @Override
    public void startActivityForResult()
    {
        Intent intent = new Intent();
        setResult(TEXT_ON_IMAGE_RESULT_OK_CODE,intent);
        finish();

        if(addedTextViews.size() > 0)
        {
            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                helper.AddImage(helper.getBytes((ImageList.getInstance().getCurrentBitmap())),currentDateandTime);
                ImageList.getInstance().deleteUndoRedoImages();
                GlideBitmapPool.clearMemory();
            }
            });
        }

    }

    @Override
    public void onTextColorChanged(int color)
    {
        for(TextViewPlus view : addedTextViews)
        {
            if(view.getTextView().getId() == clickedId)
            {
                view.setTextColor(color);
            }
        }
    }

    @Override
    public void onTextShadowColorChanged(int color)
    {
        for(TextViewPlus view : addedTextViews)
        {
            if(view.getTextView().getId() == clickedId)
            {
                view.setShadowColor(color);
            }
        }
    }

    @Override
    public void onTextFontChanged(Typeface typeFace)
    {
        for(TextViewPlus view : addedTextViews)
        {
            if(view.getTextView().getId() == clickedId)
            {
                view.setTypeface(typeFace);
            }
        }
    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch

            for(TextViewPlus view : addedTextViews)
            {
                if(clickedId == view.getTextView().getId())
                {
                    float size = view.getTextView().getTextSize();
                    float factor = detector.getScaleFactor();
                    float product = size*factor;
                    view.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, product);
                    size = view.getTextSize();
                }
            }



            return true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void extractBundle()
    {   //extract the data from previous activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            textToWrite = bundle.getString(TEXT_TO_WRITE);

        }

    }

    private void uiSetup()
    {
        //show progress dialog

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //setup action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Text");
        }




//        opacitySeekBar = (SeekBar) findViewById(R.id.opacitySeekBar);
//        textShadowDirectionSeekBar = (SeekBar) findViewById(R.id.textShadowDirectionSeekBar);
//        textShadowUpDownDirectionSeekBar = findViewById(R.id.textShadowUpDownDirectionSeekBar);
//
//        opacitySeekBar.setProgress((int)opacityLevel * 100);
//
//
//        textShadowDirectionSeekBar.setProgress(50);
//
//        textShadowDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//            {
//                if(previousShadowLevel < progress)
//                {
//                    for(TextViewPlus view : addedTextViews)
//                    {
//                        if(clickedId == view.getTextView().getId())
//                        {
//                            view.getTextView().setShadowLayer(2.5f, progress / 2 - 25, previousUpDownShadowLevel, view.getShadowColor());
//                            previousShadowLevel = progress / 2 - 25;
//                        }
//                    }
//
//                }
//                else
//                {
//                    for(TextViewPlus view : addedTextViews)
//                    {
//                        if (clickedId == view.getTextView().getId())
//                        {
//                            view.getTextView().setShadowLayer(2.5f, (progress / 2) - 25,previousUpDownShadowLevel , view.getShadowColor());
//                            previousShadowLevel = progress / 2 - 25;
//                        }
//                    }
//
//                }
//                if(progress <= 53 && progress >= 47)
//                {
//                    if(previousUpDownShadowLevel == 0)
//                    {
//                        for(TextViewPlus view : addedTextViews)
//                        {
//                            if(clickedId == view.getTextView().getId())
//                            {
//                                view.getTextView().setShadowLayer(0f,0,0,view.getShadowColor());
//                                textShadowDirectionSeekBar.setProgress(50);
//                                textShadowUpDownDirectionSeekBar.setProgress(50);
//                            }
//                        }
//
//                    }
//
//                    previousShadowLevel = 0;
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        textShadowUpDownDirectionSeekBar.setProgress(50);
//        textShadowUpDownDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//            {
//                if(previousUpDownShadowLevel < progress)
//                {
//                    for(TextViewPlus view : addedTextViews)
//                    {
//                        if(clickedId == view.getTextView().getId())
//                        {
//                            view.getTextView().setShadowLayer(2.5f, previousShadowLevel, (progress / 2) - 25, view.getShadowColor());
//                            previousUpDownShadowLevel = (progress / 2) - 25;
//                        }
//                    }
//
//                }
//                else
//                {
//                    for(TextViewPlus view : addedTextViews)
//                    {
//                        if(clickedId == view.getTextView().getId())
//                        {
//                            view.getTextView().setShadowLayer(2.5f, previousShadowLevel,(progress / 2) - 25 , view.getShadowColor());
//                            previousUpDownShadowLevel = (progress / 2) - 25;
//                        }
//
//                    }
//
//                }
//                if(progress <= 53 && progress >= 47)
//                {
//                    if(previousShadowLevel == 0)
//                    {
//                        for(TextViewPlus view : addedTextViews)
//                        {
//                            if(clickedId == view.getTextView().getId())
//                            {
//                                view.getTextView().setShadowLayer(0f,0,0,view.getShadowColor());
//                                textShadowDirectionSeekBar.setProgress(50);
//                                textShadowUpDownDirectionSeekBar.setProgress(50);
//                            }
//                        }
//
//                    }
//                    previousUpDownShadowLevel = 0;
//                }
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar)
//            {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar)
//            {
//
//            }
//        });





        //get the image bitmap
        //Bitmap bitmapForImageView = MediaStore.Images.Media.getBitmap(this.getContentResolver(),MainActivity.CurrentWorkingFilePath);
        Bitmap bitmapForImageView = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(), true);


        //create the layouts
        //base layout
        baseLayout = (ConstraintLayout) findViewById(R.id.baseLayout);
        //working layout
        workingLayout = (ConstraintLayout) findViewById(R.id.workingLayout);
        sourceImageView = (ImageView) findViewById(R.id.sourceImageView);


        //textview




        //sourceImageView.setImageBitmap(bitmapForImageView);

        Glide.with(getApplicationContext()).load(bitmapForImageView).into(sourceImageView);
        Methods methods = new Methods(getApplicationContext());
        Bitmap background = methods.getBlurBitmap(bitmapForImageView,getApplicationContext());
        sourceImageView.setBackground(new BitmapDrawable(getResources(), background));


        Render render = new Render(TextOnImageActivity.this);
        render.setAnimation(Bounce.InUp(sourceImageView));
        render.start();

        workingLayout.setDrawingCacheEnabled(true);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.setTextButton)
        {
            if(setTextFinal())
            {
                try
                {

                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);
                    bitmap = CropBitmapTransparency(bitmap);

                    AddImageToArrayListAsyncTask task;
                    if(addedTextViews.size() > 0)
                    {
                        task = new AddImageToArrayListAsyncTask(bitmap,this);
                    }
                    else
                    {
                        task = new AddImageToArrayListAsyncTask(null,this);
                    }
                    task.execute();

                    return true;


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }



            return false;
        }
        else if(item.getItemId() == R.id.setColor)
        {
            ColorPickerDialogBuilder.with(TextOnImageActivity.this)
                    .setTitle("Choose Color")
                    .initialColor(Color.WHITE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(20)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int i)
                        {
                            for(TextViewPlus view : addedTextViews)
                            {
                                if(clickedId == view.getTextView().getId())
                                {
                                    view.setTextColor(i);
                                }
                            }

                        }
                    })
                    .setPositiveButton("Ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, Integer[] integers)
                        {
                            for(TextViewPlus view : addedTextViews)
                            {
                                if(clickedId == view.getTextView().getId())
                                {
                                    view.setTextColor(i);
                                }
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).build().show();
            return true;
        }
        else if(item.getItemId() == R.id.setFont)
        {
            showAlert();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

        return true;

    }

    private boolean setTextFinal()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sourceImageView.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        for(final TextViewPlus view : addedTextViews)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    view.getTextView().setBackground(null);
                }
            });
            view.getTextView().setOnTouchListener(null);
        }

        boolean toBeReturn = false;
        workingLayout.buildDrawingCache();
        toBeReturn = saveFile(Bitmap.createBitmap(workingLayout.getDrawingCache()),"temp.jpg");
        return toBeReturn;
    }

    private boolean saveFile(Bitmap sourceImageBitmap,String fileName)
    {

        boolean result = false;
        String path = getApplicationInfo().dataDir + saveDir;
        File pathFile = new File(path);
        pathFile.mkdirs();
        File imageFile = new File(path,fileName);
        if(imageFile.exists())
        {
            imageFile.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);

            sourceImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            result = true;
        } catch (Exception e) {
            result =false;
            e.printStackTrace();
        }
        imageOutUri = Uri.fromFile(imageFile);
        return result;
    }
    public void showAlert()
    {

        dia.setContentView(R.layout.activity_font_popup_screen);
        dia.setCancelable(true);

        dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dia.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT,screenHeight / 2);

        Window window = dia.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();


        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);



        dia.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dia.dismiss();
            }
        });


        TextOnImageActivity.ListViewAdapter adapter = new TextOnImageActivity.ListViewAdapter(font);
        ListView list_alert = (ListView) dia.findViewById(R.id.fontList);

        list_alert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Toast.makeText(TextOnImage.this, "Font was selected", Toast.LENGTH_SHORT).show();
            }
        });

        final CheckBox isBorderOn = dia.findViewById(R.id.isBorderOn);
        final CheckBox isGlowOn = dia.findViewById(R.id.isGlowOn);
        isBorderOn.setChecked(isBorderCheckTrue);
        isGlowOn.setChecked(isGlowCheckTrue);

        final ImageView shadowAndGlowColor = dia.findViewById(R.id.shadowAndGlowColor);
        shadowAndGlowColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ColorPickerDialogBuilder.with(TextOnImageActivity.this)
                        .setTitle("Choose Color")
                        .initialColor(Color.WHITE)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(20)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int i)
                            {
                                for(TextViewPlus view : addedTextViews)
                                {
                                    if(clickedId == view.getTextView().getId())
                                    {
                                        view.setShadowColor(i);
                                    }
                                }
                            }
                        })
                        .setPositiveButton("Ok", new ColorPickerClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers)
                            {
                                for(TextViewPlus view : addedTextViews)
                                {
                                    if(clickedId == view.getTextView().getId())
                                    {
                                        view.setShadowColor(i);
                                    }
                                }
                                if(isGlowCheckTrue)
                                {
                                    for(TextViewPlus view : addedTextViews)
                                    {
                                        if(clickedId == view.getTextView().getId())
                                        {
                                            if(previousShadowLevel != 0 || previousUpDownShadowLevel != 0)
                                            {
                                                view.getTextView().setShadowLayer(2.5f, previousShadowLevel, previousUpDownShadowLevel, view.getShadowColor());
                                            }
                                            else
                                            {
                                                view.getTextView().setShadowLayer(12, 0, 0, view.getShadowColor());
                                            }
                                        }
                                    }


                                }
                                else
                                {
                                    for(TextViewPlus view : addedTextViews)
                                    {
                                        if(clickedId == view.getTextView().getId())
                                        {
                                            if(previousShadowLevel != 0 || previousUpDownShadowLevel != 0)
                                            {
                                                view.getTextView().setShadowLayer(2.5f, previousShadowLevel, previousUpDownShadowLevel,view.getShadowColor());
                                            }
                                            else
                                            {
                                                view.getTextView().setShadowLayer(2.5f, -1, 1, view.getShadowColor());
                                            }
                                        }
                                    }


                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).build().show();
            }
        });

        isBorderOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    for(TextViewPlus view : addedTextViews)
                    {
                        if(clickedId == view.getTextView().getId())
                        {
                            if(isGlowCheckTrue)
                            {
                                isGlowOn.setChecked(false);
                            }
                            view.getTextView().setShadowLayer(2.5f, -1, 1, view.getShadowColor());
                            isBorderCheckTrue = true;
                        }
                    }

                }
                else
                {
                    for(TextViewPlus view : addedTextViews)
                    {
                        if(clickedId == view.getTextView().getId())
                        {
                            if(!isGlowCheckTrue)
                            {
                                view.getTextView().setShadowLayer(0, 0, 0, Color.BLACK);
                            }
                            else
                            {
                                view.getTextView().setShadowLayer(12, 0, 0, view.getShadowColor());
                            }

                            isBorderCheckTrue = false;
                        }
                    }

                }
            }
        });

        isGlowOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    for(TextViewPlus view : addedTextViews)
                    {
                        if(clickedId == view.getTextView().getId())
                        {
                            if(isBorderCheckTrue)
                            {
                                isBorderOn.setChecked(false);
                            }

                            view.getTextView().setShadowLayer(12, 0, 0, view.getShadowColor());



                            isGlowCheckTrue = true;
                        }
                    }

                }
                else
                {
                    for(TextViewPlus view : addedTextViews)
                    {
                        if(clickedId == view.getTextView().getId())
                        {
                            if(!isBorderCheckTrue)
                            {
                                view.getTextView().setShadowLayer(0, 0, 0, Color.BLACK);
                            }
                            else
                            {
                                view.getTextView().setShadowLayer(2.5f, -1, 1, view.getShadowColor());
                            }
                        }
                    }


                    isGlowCheckTrue = false;
                }
            }
        });
        list_alert.setAdapter(adapter);
        dia.show();
    }
    public class ListViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
    {
        ArrayList<Typeface> fonts;


        ListViewAdapter(ArrayList<Typeface> fonts)
        {
            this.fonts = fonts;
        }
        @Override
        public int getCount()
        {
            return fonts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = getLayoutInflater().inflate(R.layout.fontrow,null);
            TextView fontText = (TextView) view.findViewById(R.id.fontText);

            fontText.setText(textToWrite);
            fontText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    for(TextViewPlus view : addedTextViews)
                    {
                        if(clickedId == view.getTextView().getId())
                        {
                            selectedTypeFace = fonts.get(position);
                            view.setTypeface(fonts.get(position));
                        }
                    }


                }
            });

            fontText.setTypeface(fonts.get(position));
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

        }
    }
    public int scaleImageKeepAspectRatio(Bitmap scaledGalleryBitmap,int width)
    {
        int imageWidth = scaledGalleryBitmap.getWidth();
        int imageHeight = scaledGalleryBitmap.getHeight();
        return  (imageHeight * width)/imageWidth;
        //scaledGalleryBitmap = Bitmap.createScaledBitmap(scaledGalleryBitmap, 500, newHeight, false);

    }
    Bitmap CropBitmapTransparency(Bitmap sourceBitmap)
    {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for(int y = 0; y < sourceBitmap.getHeight(); y++)
        {
            for(int x = 0; x < sourceBitmap.getWidth(); x++)
            {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if(alpha > 0)   // pixel is not 100% transparent
                {
                    if(x < minX)
                        minX = x;
                    if(x > maxX)
                        maxX = x;
                    if(y < minY)
                        minY = y;
                    if(y > maxY)
                        maxY = y;
                }
            }
        }
        if((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }
    public void addNewTextView(String text)
    {
        if(text != null && !text.trim().equals(""))
        {
            if(selectedTypeFace == null)
            {
                selectedTypeFace = ResourcesCompat.getFont(this, R.font.gemunulibresemibold);
            }
            addedTextViewCount++;
            TextViewPlus textView = new TextViewPlus(TextOnImageActivity.this,addedTextViewCount,text,selectedTypeFace);

            textView.getTextView().setMaxLines(1);

            textView.getTextView().setOnTouchListener(this);
            textView.getTextView().setOnLongClickListener(this);

            addedTextViews.add(textView);


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            textView.getTextView().setLayoutParams(layoutParams);


            WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            textView.getTextView().setX(width / 2 - 40);
            textView.getTextView().setY(height / 2 - 80);
            workingLayout.addView(textView.getTextView());

            clickedId = addedTextViewCount;

            setBackgroundAsSelected();

        }

    }
    public void setBackgroundAsSelected()
    {
        for(TextViewPlus view : addedTextViews)
        {
            if(clickedId == view.getTextView().getId())
            {
                view.getTextView().setBackgroundResource(R.drawable.selected_background);
            }
            else
            {
                view.getTextView().setBackground(null);
            }
        }
    }
    public void showPopup()
    {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        View view = getLayoutInflater().inflate(R.layout.activity_popup_screen,null);
        dialog.setContentView(view);

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);


        final EditText editText = (EditText) view.findViewById(R.id.updateText);
        Button button1 = (Button) view.findViewById(R.id.update);





        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(editText.getText().toString().equals("") || editText.getText().toString().trim().equals(""))
                {

                    Methods.showCustomToast(TextOnImageActivity.this,getResources().getString(R.string.enter_text));

                    //Toast.makeText(EditorActivity.this, "Enter a text", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                textToWrite = editText.getText().toString();
                addNewTextView(textToWrite);
            }
        });



        dialog.show();


    }
    public void removeStickerView(View v)
    {

        for(int i = 0; i < addedTextViews.size(); i++)
        {
            TextViewPlus view = addedTextViews.get(i);
            if(clickedId == view.getTextView().getId())
            {
                view.getTextView().setVisibility(View.GONE);
                addedTextViews.remove(i);
                clickedId = -99;
                setBackgroundAsSelected();
                break;
            }
        }

    }


}
