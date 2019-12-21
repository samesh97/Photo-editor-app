package com.sba.sinhalaphotoeditor;

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
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.glidebitmappool.GlideBitmapPool;
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
import render.animations.Zoom;

import static com.sba.sinhalaphotoeditor.EditorActivity.screenHeight;

public class TextOnImage extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {


    public static String IMAGE_IN_URI = "imageInURI";
    public static String TEXT_TO_WRITE = "sourceText";
    public static String TEXT_FONT_SIZE = "textFontSize";
    public static String TEXT_COLOR = "textColor";
    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static int TEXT_ON_IMAGE_RESULT_OK_CODE = 1;
    public static int TEXT_ON_IMAGE_RESULT_FAILED_CODE = -1;
    public static int TEXT_ON_IMAGE_REQUEST_CODE = 4;


    private static String TAG = TextOnImage.class.getSimpleName();
    private Uri imageInUri,imageOutUri;
    private String saveDir="/tmp/";
    private String textToWrite = "",textColor="#ffffff";
    private float textFontSize;
    private TextView  addTextView;
    private String errorAny = "";
    private ImageView sourceImageView;
    private RelativeLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    Dialog dia;

    ArrayList<Typeface> font = new ArrayList<>();


    private boolean isBorderCheckTrue = false;
    private boolean isGlowCheckTrue = false;
    String selectedColorForGlowAndBorder = null;


    DatabaseHelper helper = new DatabaseHelper(TextOnImage.this);


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



        progressDialog = new ProgressDialog(TextOnImage.this);
        dia = new Dialog(this,R.style.DialogSlideAnim);
        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(TextOnImage.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(TextOnImage.this);
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
        imageInUri = Uri.parse(bundle.getString(IMAGE_IN_URI));
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

        //get the image bitmap
        //Bitmap bitmapForImageView = MediaStore.Images.Media.getBitmap(this.getContentResolver(),MainActivity.CurrentWorkingFilePath);
        Bitmap bitmapForImageView = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(), true);

/*
        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = scaleImageKeepAspectRatio(bitmapForImageView,width);*/

        //bitmapForImageView = scale(bitmapForImageView,metrics.widthPixels,metrics.heightPixels);

/*

        if(bitmapForImageView.getHeight() > bitmapForImageView.getWidth())
        {
            height = scaleImageKeepAspectRatio(bitmapForImageView,width);
            width = scaleImageKeepAspectRatio2(bitmapForImageView,height);
        }
        //resize the views as per the image size

        /*
        if(bitmapForImageView.getWidth()>bitmapForImageView.getHeight())
        {
            width = 1280;
            height = 720;
        }

        if(bitmapForImageView.getWidth()<bitmapForImageView.getHeight())
        {
            width = bitmapForImageView.getWidth();
            height = height = scaleImageKeepAspectRatio(bitmapForImageView,width);
        }
        /*else
        {
            width = 600;
            height = 600;
        }*/

        //create the layouts
        //base layout
        baseLayout = new RelativeLayout(TextOnImage.this);
        RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        //RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(width,height);
        baseLayout.setBackgroundColor(Color.parseColor("#000000"));

        //working layout
        workingLayout = new RelativeLayout(TextOnImage.this);
        RelativeLayout.LayoutParams workingLayoutParams = new RelativeLayout.LayoutParams(bitmapForImageView.getWidth(),bitmapForImageView.getHeight());
        workingLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        workingLayout.setLayoutParams(workingLayoutParams);

        //image view
        sourceImageView = new ImageView(TextOnImage.this);
        RelativeLayout.LayoutParams sourceImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sourceImageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //sourceImageView.setScaleType(ImageView.ScaleType.MATRIX);
        sourceImageView.setLayoutParams(sourceImageParams);
        //sourceImageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height));


        //textview
        addTextView = new TextView(TextOnImage.this);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addTextView.setLayoutParams(textViewParams);

        //add views to working layout
        workingLayout.addView(sourceImageView);
        workingLayout.addView(addTextView);

        //add view to base layout
        baseLayout.addView(workingLayout);

        //set content view
        setContentView(baseLayout,baseLayoutParams);

        sourceImageView.setImageBitmap(bitmapForImageView);

        Render render = new Render(TextOnImage.this);
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



        Typeface typeface = ResourcesCompat.getFont(this, R.font.kaputaunicode);
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
                        float finalY = view.getY() + dy + view.getHeight();
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


            new RunInBackground().execute();
            return  true;
        }
        else if(item.getItemId() == R.id.setColor)
        {
            ColorPickerDialogBuilder.with(TextOnImage.this)
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

            sourceImageBitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
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


        ListViewAdapter adapter = new ListViewAdapter(font);
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
                ColorPickerDialogBuilder.with(TextOnImage.this)
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
                                    addTextView.setShadowLayer(12, 0, 0, Color.parseColor(selectedColorForGlowAndBorder));
                                }
                                else
                                {
                                    addTextView.setShadowLayer(2.5f, -1, 1, Color.parseColor(selectedColorForGlowAndBorder));
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


                    MainActivity.CurrentWorkingFilePath = imageOutUri;


                    MainActivity.filePaths.add(imageOutUri);
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



}