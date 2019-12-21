package com.sba.sinhalaphotoeditor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;



import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import render.animations.Render;
import render.animations.*;

public class EditorActivity extends AppCompatActivity {

    ImageView userSelectedImage;
    ImageView addText,addImage,addSticker,addCrop,addBlur;
    private static final int PICK_IMAGE_REQUEST = 234;
    static boolean isNeededToDelete = false;

    static int imageWidth,imageHeight;

    ImageView addEffect;


    static int screenWidth,screenHeight;

    Dialog dia;

    String CropType = null;


    public static Bitmap selectedSticker = null;

    ArrayList<Bitmap> stickerList1 = new ArrayList<>();
    ArrayList<Bitmap> stickerList2 = new ArrayList<>();
    ArrayList<Bitmap> stickerList3 = new ArrayList<>();
    ArrayList<Bitmap> stickerList4 = new ArrayList<>();
    ArrayList<Bitmap> stickerList5 = new ArrayList<>();

//    private AdView mAdView;


    DatabaseHelper helper = new DatabaseHelper(EditorActivity.this);

    Render render;


    @Override
    public void onBackPressed()
    {
        if(MainActivity.images.size() > 1)
        {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("අවවාදයයි!")
                    .setIcon(R.drawable.ic_delete)
                    .setMessage("ඉවත් වීමකද ඔබ සකසාගෙන ඇති සියලුම පිංතූර මැකෙන අතර\nමෙය නැවත ලබාගත නොහැක.\nතවමත් ඔබට ඉවත් වීමට අවශ්\u200Dයද?")
                    .setPositiveButton("අවශයයි", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                        }
                    }).setNegativeButton("අවශ්\u200Dය නැත", null).show();
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.undoImage)
        {
            if(MainActivity.imagePosition >= 1)
            {
                try
                {


                    // MainActivity.images.remove(MainActivity.imagePosition);
                    MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(--MainActivity.imagePosition);
                    userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
                }
                catch (Exception e)
                {

                }
            }
            else
            {

                render.setAnimation(Attention.Shake(userSelectedImage));
                render.start();
                Toast.makeText(this, "You reached to the Original Photo", Toast.LENGTH_LONG).show();
            }
        }
        if(item.getItemId() == R.id.saveImage)
        {
            if(MainActivity.images.size() > 0)
            {

                startActivity(new Intent(getApplicationContext(),ImageSavingActivity.class));

            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        if(item.getItemId() == R.id.redoImage)
        {
            if(MainActivity.imagePosition < (MainActivity.images.size() - 1))
            {
                try
                {


                    // MainActivity.images.remove(MainActivity.imagePosition);
                    MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(MainActivity.imagePosition++);
                    userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
                }
                catch (Exception e)
                {

                }


                
            }
            else
            {
                render.setAnimation(Attention.Shake(userSelectedImage));
                render.start();
                Toast.makeText(this, "You reached to the end", Toast.LENGTH_LONG).show();
            }
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_OK_CODE)
            {

                if(MainActivity.imagePosition == MainActivity.images.size() - 1)
                {
                    isNeededToDelete = false;
                }
                else {
                    isNeededToDelete = true;

                    MainActivity.bitmap = MainActivity.images.get(MainActivity.imagePosition);
                    for (int i = (MainActivity.imagePosition); i < MainActivity.images.size(); i++) {
                        try {
                            MainActivity.images.remove(++i);
                        } catch (Exception e) {

                        }

                    }
                }

                Uri resultImageUri = Uri.parse(data.getStringExtra(TextOnImage.IMAGE_OUT_URI));

                //Bitmap  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultImageUri);
                //MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(MainActivity.imagePosition);
                userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));

                render.setAnimation(Zoom.InRight(userSelectedImage));
                render.start();

            }
            else if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_FAILED_CODE)
            {
                String errorInfo = data.getStringExtra(TextOnImage.IMAGE_OUT_ERROR);
                Log.d("MainActivity", "onActivityResult: "+errorInfo);
            }

        }
        if(requestCode == PhotoOnPhoto.IMAGE_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == PhotoOnPhoto.IMAGE_ON_IMAGE_RESULT_OK_CODE)
            {

                if(MainActivity.imagePosition == MainActivity.images.size() - 1)
                {
                    isNeededToDelete = false;
                }
                else
                {

                    isNeededToDelete = true;
                    MainActivity.bitmap = MainActivity.images.get(MainActivity.imagePosition);
                    for(int i = (MainActivity.imagePosition + 1); i < MainActivity.images.size(); i++)
                    {
                        Toast.makeText(this, "Deleted " + i, Toast.LENGTH_SHORT).show();
                        MainActivity.images.remove(i);
                    }

                }



                Uri resultImageUri = Uri.parse(data.getStringExtra(PhotoOnPhoto.IMAGE_OUT_URI));
                //MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(MainActivity.imagePosition);
                userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
                render.setAnimation(Zoom.InRight(userSelectedImage));
                render.start();
            }
            if(requestCode == PhotoOnPhoto.IMAGE_ON_IMAGE_RESULT_FAILED_CODE)
            {


            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri CurrentWorkingFilePath = data.getData();
            try
            {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), CurrentWorkingFilePath);
                addImageOnImage(CurrentWorkingFilePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == StickerOnPhoto.STICKER_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == StickerOnPhoto.STICKER_ON_IMAGE_RESULT_OK_CODE)
            {

                if(MainActivity.imagePosition == MainActivity.images.size() - 1)
                {
                    isNeededToDelete = false;
                }
                else
                {

                    isNeededToDelete = true;
                    MainActivity.bitmap = MainActivity.images.get(MainActivity.imagePosition);
                    for(int i = (MainActivity.imagePosition + 1); i < MainActivity.images.size(); i++)
                    {
                        MainActivity.images.remove(i);
                    }

            /*
            MainActivity.bitmap = MainActivity.images.get(MainActivity.imagePosition);
            MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(MainActivity.imagePosition);
            MainActivity.images.clear();
            MainActivity.filePaths.clear();
            MainActivity.imagePosition = 0;
            MainActivity.images.add(MainActivity.bitmap);
            MainActivity.filePaths.add(MainActivity.CurrentWorkingFilePath);*/


                }


                Uri resultImageUri = Uri.parse(data.getStringExtra(StickerOnPhoto.STICKER_OUT_URI));
                //MainActivity.CurrentWorkingFilePath = MainActivity.filePaths.get(MainActivity.imagePosition);
                userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
                render.setAnimation(Zoom.InRight(userSelectedImage));
                render.start();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if(CropType.equals("NormalCrop"))
            {
                addCrop.setEnabled(true);

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap = null;
                    try
                    {

                        if(MainActivity.imagePosition == MainActivity.images.size() - 1)
                        {
                            isNeededToDelete = false;
                        }
                        else
                        {
                            isNeededToDelete = true;

                            MainActivity.bitmap = MainActivity.images.get(MainActivity.imagePosition);
                            for(int i = (MainActivity.imagePosition); i < MainActivity.images.size(); i++)
                            {
                                try
                                {
                                    MainActivity.images.remove(++i);
                                }
                                catch (Exception e)
                                {

                                }

                            }
                            try
                            {
                                MainActivity.images.remove(MainActivity.imagePosition + 1);
                            }
                            catch (Exception e)
                            {

                            }


                        }




                        ++MainActivity.imagePosition;
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);


                        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        DisplayMetrics metrics = new DisplayMetrics();
                        display.getMetrics(metrics);

                        bitmap = scale(bitmap,metrics.widthPixels,metrics.heightPixels);


                        MainActivity.images.add(bitmap);
                    /*
                    AsyncTask.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //get Date and time
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());
                            helper.AddImage(helper.getBytes((MainActivity.images.get(MainActivity.imagePosition))),currentDateandTime);
                        }
                    });*/
                        userSelectedImage.setImageBitmap(bitmap);
                        render.setAnimation(Zoom.InRight(userSelectedImage));
                        render.start();

                        MainActivity.deleteUndoRedoImages();
                        //GlideBitmapPool.clearMemory();


                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
            }
            else if(CropType.equals("PhotoOnPhotoCrop"))
            {

            }



            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                //Exception error = result.getError();
            }
        }
        if(requestCode == 10  && resultCode == 11)
        {
            userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
            render.setAnimation(Zoom.InRight(userSelectedImage));
            render.start();
        }
        if(requestCode == 20  && resultCode == 21)
        {
            userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
            render.setAnimation(Zoom.InRight(userSelectedImage));
            render.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);




/*        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/



        render = new Render(EditorActivity.this);


        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);


        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;







        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#114f5e")));

        dia = new Dialog(this, R.style.DialogSlideAnim);

        userSelectedImage = (ImageView) findViewById(R.id.userSelectedImage);
        addText = (ImageView) findViewById(R.id.addText);
        addImage = (ImageView) findViewById(R.id.addImage);
        addSticker =(ImageView) findViewById(R.id.addSticker);
        addCrop = (ImageView) findViewById(R.id.addCrop);
        addEffect = (ImageView) findViewById(R.id.addEffect);
        addBlur = (ImageView) findViewById(R.id.addBlur);


        addBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.images.get(MainActivity.imagePosition) != null && MainActivity.images.size() > 0)
                {
                    render.setAnimation(Attention.Bounce(addBlur));
                    render.start();
                    startActivityForResult(new Intent(getApplicationContext(),AdjustImage.class),10);
                }
                else
                {
                    Toast.makeText(EditorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(MainActivity.images.get(MainActivity.imagePosition) != null && MainActivity.images.size() > 0)
                {
                    render.setAnimation(Attention.Bounce(addEffect));
                    render.start();
                    startActivityForResult(new Intent(getApplicationContext(),AddEffects.class),20);
                }
                else
                {
                    Toast.makeText(EditorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }


            }
        });

        try
        {
            userSelectedImage.setImageBitmap(MainActivity.images.get(MainActivity.imagePosition));
        }
        catch (Exception e)
        {

        }





        Bitmap b;
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji1);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji2);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji3);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji4);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji5);
        stickerList5.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji6);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji7);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji8);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji9);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji10);
        stickerList5.add(b);


        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji11);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji12);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji13);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji14);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji15);
        stickerList5.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji16);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji17);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji18);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji19);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji20);
        stickerList5.add(b);

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji21);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji22);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji23);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji24);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji25);
        stickerList5.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji26);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji27);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji28);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji29);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji30);
        stickerList5.add(b);

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji31);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji32);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji33);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji34);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji35);
        stickerList5.add(b);

        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji36);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji37);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji38);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji39);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji40);
        stickerList5.add(b);


        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji41);
        stickerList1.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji42);
        stickerList2.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji43);
        stickerList3.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji44);
        stickerList4.add(b);
        b = BitmapFactory.decodeResource(getResources(),R.drawable.emoji45);
        stickerList5.add(b);




        try
        {
            imageWidth = MainActivity.images.get(MainActivity.imagePosition).getWidth();
            imageHeight = MainActivity.images.get(MainActivity.imagePosition).getHeight();
        }
        catch (Exception e)
        {

        }







        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(MainActivity.images.get(MainActivity.imagePosition) != null && MainActivity.images.size() > 0)
                {
                    render.setAnimation(Attention.Bounce(addText));
                    render.start();
                    showPopup();
                }
                else
                {
                    Toast.makeText(EditorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.images.get(MainActivity.imagePosition) != null && MainActivity.images.size() > 0)
                {
                    CropType = "PhotoOnPhotoCrop";
                    render.setAnimation(Attention.Bounce(addImage));
                    render.start();
                    showFileChooser();
                }
                else
                {
                    Toast.makeText(EditorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addSticker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                render.setAnimation(Attention.Bounce(addSticker));
                render.start();
                showStickerPopup();
            }
        });


        addCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.images.get(MainActivity.imagePosition) != null && MainActivity.images.size() > 0)
                {
                    CropType = "NormalCrop";
                    render.setAnimation(Attention.Bounce(addCrop));
                    render.start();
                    addCrop.setEnabled(false);
                    final Uri uri = getImageUri(getApplicationContext(),MainActivity.images.get(MainActivity.imagePosition),false);
                    CropImage.activity(uri).start(EditorActivity.this);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            File f = new File(getRealPathFromDocumentUri(getApplicationContext(),uri));
                            if(f.exists())
                            {
                                f.delete();
                            }
                        }
                    },5000);

                }
                else
                {
                    Toast.makeText(EditorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addTextOnImage(String text)
    {

        //pass the data to add it in image
        Intent intent = new Intent(EditorActivity.this,TextOnImage.class);
        Bundle bundle = new Bundle();
        bundle.putString(TextOnImage.IMAGE_IN_URI,MainActivity.CurrentWorkingFilePath.toString()); //image uri
        bundle.putString(TextOnImage.TEXT_COLOR,"#FFFFFF");                 //initial color of the text
        bundle.putFloat(TextOnImage.TEXT_FONT_SIZE,30.0f);                  //initial text size
        bundle.putString(TextOnImage.TEXT_TO_WRITE,text);                   //text to be add in the image
        intent.putExtras(bundle);
        startActivityForResult(intent, TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE); //start activity for the result
    }
    private void addImageOnImage(Uri uri)
    {

        Intent intent = new Intent(EditorActivity.this,PhotoOnPhoto.class);
        Bundle bundle = new Bundle();
        bundle.putString(PhotoOnPhoto.IMAGE_IN_URI,MainActivity.CurrentWorkingFilePath.toString());
        bundle.putString("IMAGE_ON_IMAGE_URI",uri.toString());
        bundle.putInt(PhotoOnPhoto.IMAGE_SIZE,1000);
        intent.putExtras(bundle);
        startActivityForResult(intent, PhotoOnPhoto.IMAGE_ON_IMAGE_REQUEST_CODE);
    }
    public void showPopup()
    {



        final AlertDialog dialogBuilder = new AlertDialog.Builder(this,R.style.DialogSlideAnim).create();
        //dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialogBuilder.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,screenHeight / 2);

        Window window = dialogBuilder.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_popup_screen, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.updateText);
        Button button1 = (Button) dialogView.findViewById(R.id.update);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(editText.getText().toString().equals(""))
                {
                    Toast.makeText(EditorActivity.this, "Enter a text", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogBuilder.dismiss();
                addTextOnImage(editText.getText().toString());
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }
    private void showFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void addStickerOnImage()
    {


        Intent intent = new Intent(EditorActivity.this,StickerOnPhoto.class);
        Bundle bundle = new Bundle();
        bundle.putString(StickerOnPhoto.STICKER_IN_URI,MainActivity.CurrentWorkingFilePath.toString());
        bundle.putInt(StickerOnPhoto.STICKER_SIZE,1000);
        intent.putExtras(bundle);
        startActivityForResult(intent, StickerOnPhoto.STICKER_ON_IMAGE_REQUEST_CODE);
    }
    private void showStickerPopup()
    {

        dia.setContentView(R.layout.activity_sticker_popup_screen);
        dia.setCancelable(true);
        dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dia.getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT,screenHeight / 2);

        Window window = dia.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);


        ListView list_alert = (ListView) dia.findViewById(R.id.stickeList);
        ListViewAdapter adapter = new ListViewAdapter(stickerList1,stickerList2,stickerList3,stickerList4,stickerList5);
        list_alert.setAdapter(adapter);
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
        public int getCount() {
            return stic5.size();
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
            View view = getLayoutInflater().inflate(R.layout.stickerrow,null);
            ImageView OneSticker = (ImageView) view.findViewById(R.id.OneSticker);
            ImageView TwoSticker = (ImageView) view.findViewById(R.id.TwoSticker);
            ImageView ThreeSticker = (ImageView) view.findViewById(R.id.ThreeSticker);
            ImageView FourSticker = (ImageView) view.findViewById(R.id.FourSticker);
            ImageView FiveSticker = (ImageView) view.findViewById(R.id.FiveSticker);


            OneSticker.setImageBitmap(getResizedBitmap(stic1.get(position),80));
            TwoSticker.setImageBitmap(getResizedBitmap(stic2.get(position),80));
            ThreeSticker.setImageBitmap(getResizedBitmap(stic3.get(position),80));
            FourSticker.setImageBitmap(getResizedBitmap(stic4.get(position),80));
            FiveSticker.setImageBitmap(getResizedBitmap(stic5.get(position),80));



            Animation pulse = AnimationUtils.loadAnimation(EditorActivity.this, R.anim.pulse2);
            OneSticker.startAnimation(pulse);
            TwoSticker.startAnimation(pulse);
            ThreeSticker.startAnimation(pulse);
            FourSticker.startAnimation(pulse);
            FiveSticker.startAnimation(pulse);







            OneSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = stic1.get(position);
                    selectedSticker = x;
                    addStickerOnImage();
                    dia.dismiss();
                }
            });
            TwoSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = stic2.get(position);
                    selectedSticker = x;
                    addStickerOnImage();
                    dia.dismiss();
                }
            });
            ThreeSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = stic3.get(position);
                    selectedSticker = x;
                    addStickerOnImage();
                    dia.dismiss();
                }
            });
            FourSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = stic4.get(position);
                    selectedSticker = x;
                    addStickerOnImage();
                    dia.dismiss();
                }
            });
            FiveSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bitmap x = stic5.get(position);
                    selectedSticker = x;
                    addStickerOnImage();
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
    public Uri getImageUri(Context inContext, Bitmap inImage,Boolean isNeededToDelete)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);


        if(isNeededToDelete)
        {
            String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
            Log.d("image",realPath);

            File file = new File(realPath);
            if(file.exists())
            {
                file.delete();
                Log.d("image","deleted");
            }
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
