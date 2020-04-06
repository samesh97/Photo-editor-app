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
import android.graphics.PorterDuff;
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
import android.widget.Toast;

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
        View.OnTouchListener,View.OnLongClickListener,
        OnAsyncTaskState, OnTextAttributesChangedListner {


    public static String TEXT_TO_WRITE = "sourceText";
    public static int TEXT_ON_IMAGE_RESULT_OK_CODE = 1;
    public static int TEXT_ON_IMAGE_REQUEST_CODE = 4;


    private Uri imageOutUri;
    private String saveDir="/tmp/";
    private String textToWrite = "";
    private ImageView sourceImageView;
    private ConstraintLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private RotationGestureDetector mRotationGestureDetector;



    private DatabaseHelper helper;



    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;


    private int clickedId = 1;
    private int addedTextViewCount = 0;
    private ArrayList<TextViewPlus> addedTextViews = new ArrayList<>();
    private float lastX = 0, lastY = 0;
    private Typeface selectedTypeFace = null;
    private TextViewPlus clickedTextView = null;



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_on_image);


        helper = new DatabaseHelper(TextOnImageActivity.this);
        Button addNewTextViewButton = findViewById(R.id.addNewTextViewButton);
        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);


        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                animateExpandableLayout();
                showBottomSheet();
            }
        });

        addNewTextViewButton.setOnClickListener(new View.OnClickListener() {
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


        scaleGestureDetector = new ScaleGestureDetector(TextOnImageActivity.this,new TextOnImageActivity.simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(TextOnImageActivity.this);
        extractBundle();
        uiSetup();
        addNewTextView(textToWrite);


    }
    private void animateExpandableLayout()
    {
        if(expandableLayout != null)
        {
            expandableLayout.animate().setDuration(500).translationYBy(0).translationY(200).start();
        }
    }
    private void undoAnimateExpandableLayout()
    {
        if(expandableLayout != null)
        {
            expandableLayout.animate().setDuration(500).translationYBy(200).translationY(0).start();
        }
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

        //setup action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Text");
        }


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








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        else
        {
            return super.onOptionsItemSelected(item);
        }

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
    private void showBottomSheet()
    {
        BottomSheetDialog dialog = new BottomSheetDialog(TextOnImageActivity.this,R.style.CustomBottomSheetDialogTheme);

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

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_text_bottom_sheet,null,false);
        clickedTextView = null;
        final EditText current_text = view.findViewById(R.id.current_text);
        for(TextViewPlus view2 : addedTextViews)
        {
            if(view2.getTextView().getId() == clickedId)
            {
                clickedTextView = view2;
                current_text.setText(view2.getText());
            }
        }



        RecyclerView colorRV = view.findViewById(R.id.color_recycler_view);
        TextColorAdapter textColorAdapter = new TextColorAdapter(getApplicationContext(),clickedTextView,TextOnImageActivity.this);
        LinearLayoutManager textColorManger = new LinearLayoutManager(getApplicationContext());
        textColorManger.setOrientation(RecyclerView.HORIZONTAL);
        colorRV.setItemViewCacheSize(100);
        colorRV.setLayoutManager(textColorManger);
        colorRV.setAdapter(textColorAdapter);

        RecyclerView shadowColorRV = view.findViewById(R.id.shadow_color_recycler_view);
        ShadowColorAdapter shadowColorAdapter = new ShadowColorAdapter(getApplicationContext(),clickedTextView,TextOnImageActivity.this);
        LinearLayoutManager shadowColorManager = new LinearLayoutManager(getApplicationContext());
        shadowColorManager.setOrientation(RecyclerView.HORIZONTAL);
        shadowColorRV.setItemViewCacheSize(100);
        shadowColorRV.setLayoutManager(shadowColorManager);
        shadowColorRV.setAdapter(shadowColorAdapter);


        final RecyclerView fontsRV = view.findViewById(R.id.font_recycler_view);
        TextFontAdapter textFontAdapter = new TextFontAdapter(getApplicationContext(),"මහරගම",clickedTextView,TextOnImageActivity.this);
        LinearLayoutManager textFontManager = new LinearLayoutManager(getApplicationContext());
        textFontManager.setOrientation(RecyclerView.HORIZONTAL);
        fontsRV.setItemViewCacheSize(100);
        fontsRV.setLayoutManager(textFontManager);
        fontsRV.setAdapter(textFontAdapter);




        current_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(clickedTextView != null)
                {
                    clickedTextView.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });


        SeekBar opacitySeekBar = view.findViewById(R.id.opacitySeekBar);
        opacitySeekBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        if(clickedTextView != null)
        {
            opacitySeekBar.setProgress((int)clickedTextView.getTextOpacity());
        }
        else
        {
            opacitySeekBar.setProgress(100);
        }

        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(clickedTextView != null)
                {
                    clickedTextView.setTextOpacity(progress);
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
        textShadowDirectionSeekBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        if(clickedTextView != null)
        {
            textShadowDirectionSeekBar.setProgress((int)clickedTextView.getShadowXDirection());
        }
        else
        {
            textShadowDirectionSeekBar.setProgress(50);
        }
        textShadowDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(clickedTextView != null)
                {
                    clickedTextView.setShadowXDirection(progress);
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
        textShadowUpDownDirectionSeekBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        if(clickedTextView != null)
        {
            textShadowUpDownDirectionSeekBar.setProgress((int)clickedTextView.getShadowYDirection());
        }
        else
        {
            textShadowUpDownDirectionSeekBar.setProgress(50);
        }
        textShadowUpDownDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(clickedTextView != null)
                {
                    clickedTextView.setShadowYDirection(progress);
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

}
