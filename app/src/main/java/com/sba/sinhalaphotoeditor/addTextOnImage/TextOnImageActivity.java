package com.sba.sinhalaphotoeditor.addTextOnImage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.sba.sinhalaphotoeditor.EditorActivity;
import com.sba.sinhalaphotoeditor.MainActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.RotationGestureDetector;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import render.animations.Bounce;
import render.animations.Render;

import static com.sba.sinhalaphotoeditor.EditorActivity.screenHeight;

public class TextOnImageActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener
{


    public static String IMAGE_IN_URI = "imageInURI";
    public static String TEXT_TO_WRITE = "sourceText";
    public static String TEXT_FONT_SIZE = "textFontSize";
    public static String TEXT_COLOR = "textColor";
    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static int TEXT_ON_IMAGE_RESULT_OK_CODE = 1;
    public static int TEXT_ON_IMAGE_RESULT_FAILED_CODE = -1;
    public static int TEXT_ON_IMAGE_REQUEST_CODE = 4;


    private static String TAG = TextOnImageActivity.class.getSimpleName();
    private Uri imageInUri,imageOutUri;
    private String saveDir="/tmp/";
    private String textToWrite = "",textColor="#ffffff";
    private float textFontSize;
    private TextView addTextView;
    private String errorAny = "";
    private ImageView sourceImageView;
    private ConstraintLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    Dialog dia;

    ArrayList<Typeface> font = new ArrayList<>();


    private boolean isBorderCheckTrue = false;
    private boolean isGlowCheckTrue = false;
    String selectedColorForGlowAndBorder = "#000000";


    DatabaseHelper helper = new DatabaseHelper(TextOnImageActivity.this);


    private SeekBar opacitySeekBar;
    private  SeekBar textShadowDirectionSeekBar;
    private SeekBar textShadowUpDownDirectionSeekBar;
    private float opacityLevel = 1f;



    private int previousShadowLevel = 0;
    private int previousUpDownShadowLevel = 0;

    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;


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





        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expandableLayout.isExpanded())
                {
                    expandableLayout.collapse();
                    //expandIcon.setImageResource(R.drawable.slide_up_image);
                    expandIcon.setBackground(getResources().getDrawable(R.drawable.top_rounded_background));
                    expandableLayout.setBackgroundColor(Color.TRANSPARENT);
                }
                else
                {
                    expandIcon.setBackgroundColor(Color.TRANSPARENT);
                    expandableLayout.setBackground(getResources().getDrawable(R.drawable.white_opacity_background));
                    expandableLayout.expand();// expand with animation
                    //expandIcon.setImageResource(R.drawable.slide_down_image);

                }



            }
        });












        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();



        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));

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



        progressDialog = new ProgressDialog(TextOnImageActivity.this);
        dia = new Dialog(this,R.style.DialogSlideAnim);
        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(TextOnImageActivity.this,new TextOnImageActivity.simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(TextOnImageActivity.this);
        extractBundle();
        uiSetup();




    }
    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        //set the rotation of the text view
        float angle = rotationDetector.getAngle();
        addTextView.setRotation(angle);
    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch
            float size = addTextView.getTextSize();
            float factor = detector.getScaleFactor();
            float product = size*factor;
            addTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);
            size = addTextView.getTextSize();
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
//        imageInUri = Uri.parse(bundle.getString(IMAGE_IN_URI));
        textToWrite = bundle.getString(TEXT_TO_WRITE);
        textFontSize = bundle.getFloat(TEXT_FONT_SIZE);
        textColor = bundle.getString(TEXT_COLOR);
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




        opacitySeekBar = (SeekBar) findViewById(R.id.opacitySeekBar);
        textShadowDirectionSeekBar = (SeekBar) findViewById(R.id.textShadowDirectionSeekBar);
        textShadowUpDownDirectionSeekBar = findViewById(R.id.textShadowUpDownDirectionSeekBar);

        opacitySeekBar.setProgress((int)opacityLevel * 100);
        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                opacityLevel = (progress / 100.0f);
                addTextView.setAlpha(opacityLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textShadowDirectionSeekBar.setProgress(50);

        textShadowDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(previousShadowLevel < progress)
                {
                    addTextView.setShadowLayer(2.5f, progress / 2 - 25, previousUpDownShadowLevel, Color.parseColor(selectedColorForGlowAndBorder));
                    previousShadowLevel = progress / 2 - 25;
                }
                else
                {
                    addTextView.setShadowLayer(2.5f, (progress / 2) - 25,previousUpDownShadowLevel , Color.parseColor(selectedColorForGlowAndBorder));
                    previousShadowLevel = progress / 2 - 25;
                }
                if(progress <= 53 && progress >= 47)
                {
                    if(previousUpDownShadowLevel == 0)
                    {
                        addTextView.setShadowLayer(0f,0,0,Color.parseColor(selectedColorForGlowAndBorder));
                        textShadowDirectionSeekBar.setProgress(50);
                        textShadowUpDownDirectionSeekBar.setProgress(50);
                    }

                    previousShadowLevel = 0;
                }




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        textShadowUpDownDirectionSeekBar.setProgress(50);
        textShadowUpDownDirectionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(previousUpDownShadowLevel < progress)
                {
                    addTextView.setShadowLayer(2.5f, previousShadowLevel, (progress / 2) - 25, Color.parseColor(selectedColorForGlowAndBorder));
                    previousUpDownShadowLevel = (progress / 2) - 25;
                }
                else
                {
                    addTextView.setShadowLayer(2.5f, previousShadowLevel,(progress / 2) - 25 , Color.parseColor(selectedColorForGlowAndBorder));
                    previousUpDownShadowLevel = (progress / 2) - 25;
                }
                if(progress <= 53 && progress >= 47)
                {
                    if(previousShadowLevel == 0)
                    {
                        addTextView.setShadowLayer(0f,0,0,Color.parseColor(selectedColorForGlowAndBorder));
                        textShadowDirectionSeekBar.setProgress(50);
                        textShadowUpDownDirectionSeekBar.setProgress(50);
                    }
                    previousUpDownShadowLevel = 0;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });





        //get the image bitmap
        //Bitmap bitmapForImageView = MediaStore.Images.Media.getBitmap(this.getContentResolver(),MainActivity.CurrentWorkingFilePath);
        Bitmap bitmapForImageView = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(), true);


        //create the layouts
        //base layout
        baseLayout = (ConstraintLayout) findViewById(R.id.baseLayout);
        //working layout
        workingLayout = (ConstraintLayout) findViewById(R.id.workingLayout);
        sourceImageView = (ImageView) findViewById(R.id.sourceImageView);

        //textview
        addTextView = (TextView) findViewById(R.id.addTextView);

        addTextView.setAlpha(opacityLevel);


        //sourceImageView.setImageBitmap(bitmapForImageView);

        Glide.with(getApplicationContext()).load(bitmapForImageView).into(sourceImageView);

        Render render = new Render(TextOnImageActivity.this);
        render.setAnimation(Bounce.InUp(sourceImageView));
        render.start();

        workingLayout.setDrawingCacheEnabled(true);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        //setup the text view
        addTextView.setText(textToWrite);
        addTextView.setTextSize(textFontSize);



        Typeface typeface = ResourcesCompat.getFont(this, R.font.gemunulibresemibold);
        addTextView.setTypeface(typeface);



        addTextView.setTextColor(Color.parseColor(textColor));
        addTextView.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0, lastY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        lastX = motionEvent.getX();
                        lastY = motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = motionEvent.getX() - lastX;
                        float dy = motionEvent.getY() - lastY;
                        float finalX = view.getX() + dx;
//                        float finalY = view.getY() + dy + view.getHeight();
                        float finalY = view.getY() + dy;
                        view.setX(finalX);
                        view.setY(finalY);
                        break;
                }

                return true;
            }
        });

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


            new TextOnImageActivity.RunInBackground().execute();
            return  true;
        }
        else if(item.getItemId() == R.id.setColor)
        {
            ColorPickerDialogBuilder.with(TextOnImageActivity.this)
                    .setTitle("Choose Color")
                    .initialColor(Color.parseColor(textColor))
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(20)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int i) {
                            addTextView.setTextColor(i);
                        }
                    })
                    .setPositiveButton("Ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                            addTextView.setTextColor(i);
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

        addTextView.setOnTouchListener(null);
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
            errorAny = e.getMessage();
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


        //addTextView.setTypeface(typeface);

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
                        .initialColor(Color.parseColor(textColor))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(20)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int i)
                            {
                                selectedColorForGlowAndBorder = String.format("#%X", i);
                            }
                        })
                        .setPositiveButton("Ok", new ColorPickerClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers)
                            {
                                selectedColorForGlowAndBorder = String.format("#%X", i);
                                if(isGlowCheckTrue)
                                {
                                    if(previousShadowLevel != 0 || previousUpDownShadowLevel != 0)
                                    {
                                        addTextView.setShadowLayer(2.5f, previousShadowLevel, previousUpDownShadowLevel, Color.parseColor(selectedColorForGlowAndBorder));
                                    }
                                    else
                                    {
                                        addTextView.setShadowLayer(12, 0, 0, Color.parseColor(selectedColorForGlowAndBorder));
                                    }

                                }
                                else
                                {
                                    if(previousShadowLevel != 0 || previousUpDownShadowLevel != 0)
                                    {
                                        addTextView.setShadowLayer(2.5f, previousShadowLevel, previousUpDownShadowLevel, Color.parseColor(selectedColorForGlowAndBorder));
                                    }
                                    else
                                    {
                                        addTextView.setShadowLayer(2.5f, -1, 1, Color.parseColor(selectedColorForGlowAndBorder));
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
                    if(isGlowCheckTrue)
                    {
                        isGlowOn.setChecked(false);
                    }
                    if(selectedColorForGlowAndBorder != null)
                    {
                        addTextView.setShadowLayer(2.5f, -1, 1, Color.parseColor(selectedColorForGlowAndBorder));
                    }
                    else
                    {
                        addTextView.setShadowLayer(2.5f, -1, 1, Color.BLACK);
                    }

                    isBorderCheckTrue = true;
                }
                else
                {
                    if(!isGlowCheckTrue)
                    {
                        addTextView.setShadowLayer(0, 0, 0, Color.BLACK);
                    }
                    else
                    {
                        if(selectedColorForGlowAndBorder != null)
                        {
                            addTextView.setShadowLayer(12, 0, 0, Color.parseColor(selectedColorForGlowAndBorder));
                        }
                        else
                        {
                            addTextView.setShadowLayer(12, 0, 0, Color.BLACK);
                        }

                    }

                    isBorderCheckTrue = false;
                }
            }
        });

        isGlowOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    if(isBorderCheckTrue)
                    {
                        isBorderOn.setChecked(false);
                    }
                    if(selectedColorForGlowAndBorder != null)
                    {
                        addTextView.setShadowLayer(12, 0, 0, Color.parseColor(selectedColorForGlowAndBorder));
                    }
                    else
                    {
                        addTextView.setShadowLayer(12, 0, 0, Color.BLACK);
                    }

                    isGlowCheckTrue = true;
                }
                else
                {
                    if(!isBorderCheckTrue)
                    {
                        addTextView.setShadowLayer(0, 0, 0, Color.BLACK);
                    }
                    else
                    {
                        if(selectedColorForGlowAndBorder != null)
                        {
                            addTextView.setShadowLayer(2.5f, -1, 1, Color.parseColor(selectedColorForGlowAndBorder));
                        }
                        else
                        {
                            addTextView.setShadowLayer(2.5f, -1, 1, Color.BLACK);
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
                    addTextView.setTypeface(fonts.get(position));

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
    public class RunInBackground extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime);
                MainActivity.deleteUndoRedoImages();
                GlideBitmapPool.clearMemory();
            }
            });
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            //set the text
            boolean doneSetting = setTextFinal();
            if(doneSetting)
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
                try {
                    Bitmap  bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);
                    bitmap = CropBitmapTransparency(bitmap);


                    MainActivity.imagePosition++;
                    MainActivity.images.add(MainActivity.imagePosition,bitmap);
                    if(EditorActivity.isNeededToDelete)
                    {
                        try
                        {
                            MainActivity.images.remove(MainActivity.imagePosition + 1);
                        }
                        catch (Exception e)
                        {

                        }


                    }


                    //MainActivity.CurrentWorkingFilePath = imageOutUri;


                   // MainActivity.filePaths.add(imageOutUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setResult(TEXT_ON_IMAGE_RESULT_OK_CODE,intent);
                /*
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }*/
                finish();

            }else
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_ERROR,errorAny);
                setResult(TEXT_ON_IMAGE_RESULT_FAILED_CODE,intent);
                /*
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }*/
                finish();

            }
            return null;
        }
    }

    private Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private Bitmap createBitmapFromLayout(View tv) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv.measure(spec, spec);
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(tv.getMeasuredWidth(), tv.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate((-tv.getScrollX()), (-tv.getScrollY()));
        tv.draw(c);
        return b;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);



        String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
        Log.d("image",realPath);

        File file = new File(realPath);
        if(file.exists())
        {
            file.delete();
            Log.d("image","deleted");
        }

        return Uri.parse(path);
    }
    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            //Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
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




}
