package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.RecyclerView.RecyclerViewAdapter;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsePreviouslyEditedImageActivity extends AppCompatActivity {

    RecyclerView ImageList;
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    DatabaseHelper helper = new DatabaseHelper(UsePreviouslyEditedImageActivity.this);

    ProgressDialog pdLoading;

    Button deleteAll;

    RecyclerViewAdapter adapter;

    @Override
    protected void onStop() {
        super.onStop();
        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pdLoading != null)
        {
            pdLoading.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(pdLoading != null)
        {
            pdLoading.dismiss();
        }
        GlideBitmapPool.clearMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_previously_edited_image);


        setTextViewFontAndSize();

        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        GlideBitmapPool.initialize(maxMemory);
        GlideBitmapPool.clearMemory();


        helper.deleteUnnessaryImages();

        pdLoading = new ProgressDialog(UsePreviouslyEditedImageActivity.this);

        GlideBitmapPool.clearMemory();

        //overridePendingTransition(R.anim.slidein, R.anim.slideout);


        ImageList = (RecyclerView) findViewById(R.id.ImageList);
        ImageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RecyclerViewAdapter(UsePreviouslyEditedImageActivity.this,images,ids,dates);
        ImageList.setAdapter(adapter);


        ImageView topGreenPannel;
        topGreenPannel = findViewById(R.id.topGreenPannel);
        Glide.with(getApplicationContext()).load(R.drawable.samplewalpaper).into(topGreenPannel);


        deleteAll = (Button) findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(helper.getNumberOfRows() > 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UsePreviouslyEditedImageActivity.this);
                    alert.setTitle("Delete All");
                    alert.setIcon(R.drawable.delete);
                    alert.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            helper.deleteAll();
                            ids.clear();
                            images.clear();
                            dates.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                }
                else
                {
                    Toast.makeText(UsePreviouslyEditedImageActivity.this, "No Images Available", Toast.LENGTH_SHORT).show();
                }

            }
        });




        try {
            Cursor cursor = helper.GetAllImages();
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                ids.add(cursor.getInt(0));
                images.add(helper.getImage(cursor.getBlob(1)));
                dates.add(cursor.getString(2));

                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something is not right. We'll fix it soon", Toast.LENGTH_SHORT).show();
        }





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
    public void setTextViewFontAndSize()
    {

        TextView textView = findViewById(R.id.textView);
        Button deleteAll = findViewById(R.id.deleteAll);


        Typeface typeface;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        int pos = pref.getInt("LanguagePosition",-99);
        if(pos != 99) {
            switch (pos) {
                case 1:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.gemunulibresemibold);
                    textView.setTypeface(typeface);
                    deleteAll.setTypeface(typeface);

                    break;
                case 2:

                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.englishfont);
                    textView.setTypeface(typeface);


                    deleteAll.setTypeface(typeface);





                    break;
                case 3:


                    typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.tamilfont);


                    textView.setTypeface(typeface);
                    textView.setTextSize(20);

                    deleteAll.setTypeface(typeface);
                    deleteAll.setTextSize(14);

                    break;
            }
        }
    }
}


