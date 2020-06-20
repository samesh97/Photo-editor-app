package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.github.chuross.library.ExpandableLayout;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.Config.RotationGestureDetector;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import render.animations.Bounce;
import render.animations.Render;



public class AddStickerOnImage extends AppCompatActivity
        implements RotationGestureDetector.OnRotationGestureListener,
        View.OnTouchListener,View.OnLongClickListener, OnAsyncTaskState {



    //public static Uri STICKER_ON_IMAGE_URI;
    public static int STICKER_ON_IMAGE_RESULT_OK_CODE = 210;
    public static int STICKER_ON_IMAGE_REQUEST_CODE = 200;

    private Uri imageOutUri;
    private String saveDir="/tmp/";

    private ImageView sourceImageView;
    private ConstraintLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;
    private float scaleFactor;

    DatabaseHelper helper = new DatabaseHelper(AddStickerOnImage.this);

    private ExpandableLayout expandableLayout;
    private ImageView expandIcon;
    private SeekBar opacitySeekBar;
    private float opacityLevel = 1f;

    private Dialog dia;
    private ArrayList<Bitmap> stickerList1 = new ArrayList<>();
    private ArrayList<Bitmap> stickerList2 = new ArrayList<>();
    private ArrayList<Bitmap> stickerList3 = new ArrayList<>();
    private ArrayList<Bitmap> stickerList4 = new ArrayList<>();
    private ArrayList<Bitmap> stickerList5 = new ArrayList<>();



    private Methods methods;




    private ArrayList<ImageView> addedStickersList = new ArrayList<>();
    private int clickedId = 1;
    private int addedStickerCount = 0;
    float lastX = 0, lastY = 0;

    private Button addNewStickerButton;
    private ProgressBar progress_bar;

    @Override
    protected void onPause() {
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
            overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sticker_on_image);


        dia = new Dialog(this);
        setBackgroundAsSelected();

        progress_bar = findViewById(R.id.progress_bar);
        addNewStickerButton = findViewById(R.id.addNewStickerButton);
        addNewStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showStickerPopup();
            }
        });

        methods = new Methods(getApplicationContext());

        Methods.freeUpMemory();



        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(AddStickerOnImage.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(AddStickerOnImage.this);
        progressDialog = new ProgressDialog(AddStickerOnImage.this);
        uiSetup();

        expandableLayout = (ExpandableLayout)findViewById(R.id.explandableLayout);
        expandIcon = findViewById(R.id.expandIcon);
        opacitySeekBar = (SeekBar) findViewById(R.id.opacitySeekBar);

        opacitySeekBar.setProgress((int)opacityLevel * 100);

        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                opacityLevel = (progress / 100.0f);
                for(ImageView imageView : addedStickersList)
                {
                    if(clickedId == imageView.getId())
                    {
                        imageView.setAlpha(opacityLevel);
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


    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector)
    {
        for(ImageView view : addedStickersList)
        {
            if(view.getId() == clickedId)
            {
                view.setRotation(rotationDetector.getAngle());
            }
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent)
    {

        clickedId = v.getId();
        setBackgroundAsSelected();
        for(ImageView view : addedStickersList)
        {
            if(view.getId() == v.getId())
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
                        float finalX = view.getX() + dx;
//                        float finalY = view.getY() + dy + view.getHeight();
                        float finalY = view.getY() + dy;
                        view.setX(finalX);
                        view.setY(finalY);

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
        progress_bar.setVisibility(View.GONE);
        Intent intent = new Intent();
        setResult(STICKER_ON_IMAGE_RESULT_OK_CODE,intent);
        finish();



        if(addedStickersList.size() > 0)
        {
            AsyncTask.execute(new Runnable()
            {@Override
            public void run()
            {
                //get Date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());


                String path = Methods.saveToInternalStorage(getApplicationContext(),ImageList.getInstance().getCurrentBitmap(),currentDateandTime);
                helper.AddImage(null,path);


                ImageList.getInstance().deleteUndoRedoImages();

            }
            });
        }

    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch


            scaleFactor *= detector.getScaleFactor();
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
            scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //


            for(ImageView view : addedStickersList)
            {
                if(view.getId() == clickedId)
                {
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                }
            }



            return true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        scaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
            actionBar.setTitle("Add Image");
        }

        //get the image bitmap
        Bitmap bitmapForImageView = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(), true);

        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels;



        //create the layouts
        //base layout
        baseLayout = findViewById(R.id.baseLayout);
        //working layout
        workingLayout = findViewById(R.id.workingLayout);
        //image view
        sourceImageView = findViewById(R.id.sourceImageView);
        //textview
       // addNewImage = findViewById(R.id.addImageView);


        Glide.with(getApplicationContext()).load(bitmapForImageView).into(sourceImageView);
        Methods methods = new Methods(getApplicationContext());
        Bitmap background = methods.getBlurBitmap(bitmapForImageView,getApplicationContext());
        sourceImageView.setBackground(new BitmapDrawable(getResources(), background));


        Render render = new Render(AddStickerOnImage.this);
        render.setAnimation(Bounce.InUp(sourceImageView));
        render.start();

        workingLayout.setDrawingCacheEnabled(true);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }


        addNewStickerView(EditorActivity.selectedSticker);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_on_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.setImage)
        {
            if(setTextFinal())
            {
                progress_bar.setVisibility(View.VISIBLE);

                try
                {
                    Bitmap  bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageOutUri);
                    bitmap = methods.CropBitmapTransparency(bitmap);

                    AddImageToArrayListAsyncTask task;
                    if(addedStickersList.size() > 0)
                    {
                        task = new AddImageToArrayListAsyncTask(bitmap,this);
                    }
                    else
                    {
                        task = new AddImageToArrayListAsyncTask(null,this);
                    }

                    task.execute();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }


            //new RunInBackground().execute();
            return  true;
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
        for(final ImageView imageView : addedStickersList)
        {
            imageView.setOnTouchListener(null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    imageView.setBackground(null);
                }
            });

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
    private void showStickerPopup()
    {


        dia.setContentView(R.layout.activity_sticker_popup_screen);
        dia.setCancelable(true);
        if(dia.getWindow() != null)
        {
            dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dia.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT, (int) (Methods.getDeviceHeightInPX(getApplicationContext()) / 2));
            Window window = dia.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            window.setAttributes(wlp);
        }


        ListView list_alert = (ListView) dia.findViewById(R.id.stickeList);
        ListViewAdapter adapter = new ListViewAdapter(stickerList1,stickerList2,stickerList3,stickerList4,stickerList5);
        list_alert.setAdapter(adapter);
        setStickers(adapter);
        dia.show();

    }
    public class ListViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
    {
        ArrayList<Bitmap> stic1;
        ArrayList<Bitmap> stic2;
        ArrayList<Bitmap> stic3;
        ArrayList<Bitmap> stic4;
        ArrayList<Bitmap> stic5;


        ListViewAdapter(ArrayList<Bitmap> stic1,ArrayList<Bitmap> stic2,ArrayList<Bitmap> stic3,ArrayList<Bitmap> stic4,ArrayList<Bitmap> stic5)
        {
            this.stic1 = stic1;
            this.stic2 = stic2;
            this.stic3 = stic3;
            this.stic4 = stic4;
            this.stic5 = stic5;
        }
        @Override
        public int getCount()
        {
            return stic5.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = getLayoutInflater().inflate(R.layout.stickerrow,null);
            ImageView OneSticker = (ImageView) view.findViewById(R.id.OneSticker);
            ImageView TwoSticker = (ImageView) view.findViewById(R.id.TwoSticker);
            ImageView ThreeSticker = (ImageView) view.findViewById(R.id.ThreeSticker);
            ImageView FourSticker = (ImageView) view.findViewById(R.id.FourSticker);
            ImageView FiveSticker = (ImageView) view.findViewById(R.id.FiveSticker);


/*
            Glide.with(getApplicationContext()).load(getResizedBitmap(stic1.get(position),80)).into(OneSticker);
            Glide.with(getApplicationContext()).load(getResizedBitmap(stic2.get(position),80)).into(TwoSticker);
            Glide.with(getApplicationContext()).load(getResizedBitmap(stic3.get(position),80)).into(ThreeSticker);
            Glide.with(getApplicationContext()).load(getResizedBitmap(stic4.get(position),80)).into(FourSticker);
            Glide.with(getApplicationContext()).load(getResizedBitmap(stic5.get(position),80)).into(FiveSticker);
*/

            OneSticker.setImageBitmap(stic1.get(position));
            TwoSticker.setImageBitmap(stic2.get(position));
            ThreeSticker.setImageBitmap(stic3.get(position));
            FourSticker.setImageBitmap(stic4.get(position));
            FiveSticker.setImageBitmap(stic5.get(position));



            Animation pulse = AnimationUtils.loadAnimation(AddStickerOnImage.this, R.anim.pulse2);
            OneSticker.startAnimation(pulse);
            TwoSticker.startAnimation(pulse);
            ThreeSticker.startAnimation(pulse);
            FourSticker.startAnimation(pulse);
            FiveSticker.startAnimation(pulse);







            OneSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = getRealSticker((position * 5) + 1);
                    addNewStickerView(x);
                    dia.dismiss();
                }
            });
            TwoSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = getRealSticker((position * 5) + 2);
                    addNewStickerView(x);
                    dia.dismiss();
                }
            });
            ThreeSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = getRealSticker((position * 5) + 3);
                    addNewStickerView(x);
                    dia.dismiss();
                }
            });
            FourSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = getRealSticker((position * 5) + 4);
                    addNewStickerView(x);
                    dia.dismiss();
                }
            });
            FiveSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = getRealSticker((position * 5) + 5);
                    addNewStickerView(x);
                    dia.dismiss();
                }
            });

            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

        }
    }
    public Bitmap getRealSticker(int position)
    {

        switch (position)
        {
            case 2  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji2);
            case 3  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji3);
            case 4  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji4);
            case 5  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji5);
            case 6  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji6);
            case 7  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji7);
            case 8  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji8);
            case 9  : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji9);
            case 10 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji10);
            case 11 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji11);
            case 12 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji12);
            case 13 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji13);
            case 14 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji14);
            case 15 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji15);
            case 16 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji16);
            case 17 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji17);
            case 18 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji18);
            case 19 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji19);
            case 20 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji20);
            case 21 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji21);
            case 22 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji22);
            case 23 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji23);
            case 24 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji24);
            case 25 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji25);
            case 26 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji26);
            case 27 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji27);
            case 28 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji28);
            case 29 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji29);
            case 30 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji30);
            case 31 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji31);
            case 32 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji32);
            case 33 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji33);
            case 34 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji34);
            case 35 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji35);
            case 36 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji36);
            case 37 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji37);
            case 38 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji38);
            case 39 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji39);
            case 40 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji40);
            case 41 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji41);
            case 42 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji42);
            case 43 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji43);
            case 44 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji44);
            case 45 : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji45);
            default : return BitmapFactory.decodeResource(getResources(),R.drawable.emoji1);

        }

    }
    private void setStickers(ListViewAdapter adapter)
    {
        removeStickers();
        Bitmap b;
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji1);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji2);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji3);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji4);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji5);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji6);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji7);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji8);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji9);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji10);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji11);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji12);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji13);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji14);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji15);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji16);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji17);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji18);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji19);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji20);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji21);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji22);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji23);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji24);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji25);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji26);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji27);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji28);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji29);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji30);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji31);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji32);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji33);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji34);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji35);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji36);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji37);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji38);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji39);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji40);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();


        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji41);
        b = methods.getResizedBitmap(b,80);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji42);
        b = methods.getResizedBitmap(b,80);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji43);
        b = methods.getResizedBitmap(b,80);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji44);
        b = methods.getResizedBitmap(b,80);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji45);
        b = methods.getResizedBitmap(b,80);
        stickerList5.add(b);

        adapter.notifyDataSetChanged();
        b = null;

    }
    public void removeStickers()
    {
        stickerList1.clear();
        stickerList2.clear();
        stickerList3.clear();
        stickerList4.clear();
        stickerList5.clear();

    }
    public void addNewStickerView(Bitmap bitmap)
    {
        if(bitmap != null)
        {
            addedStickerCount++;
            ImageView imageView = new ImageView(AddStickerOnImage.this);
            imageView.setImageBitmap(bitmap);
            imageView.setId(addedStickerCount);

            imageView.setOnTouchListener(this);
            imageView.setOnLongClickListener(this);

            addedStickersList.add(imageView);


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(80,80);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(layoutParams);


            WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            imageView.setX(width / 2 - 40);
            imageView.setY(height / 2 - 80);
            workingLayout.addView(imageView);

            clickedId = addedStickerCount;

            setBackgroundAsSelected();

        }
    }
    public void removeStickerView(View v)
    {

        for(int i = 0; i < addedStickersList.size(); i++)
        {
            ImageView view = addedStickersList.get(i);
            if(clickedId == view.getId())
            {
                view.setVisibility(View.GONE);
                addedStickersList.remove(i);
                clickedId = -99;
                setBackgroundAsSelected();
                break;
            }
        }

    }
    public void setBackgroundAsSelected()
    {
        for(ImageView view : addedStickersList)
        {
            if(clickedId == view.getId())
            {
                view.setBackgroundResource(R.drawable.selected_background);
            }
            else
            {
                view.setBackground(null);
            }
        }
    }
    public void removeBackgroundAsSelected()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for(ImageView view : addedStickersList)
                {
                    view.setBackground(null);
                }
            }
        });

    }
}
