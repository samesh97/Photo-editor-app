package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UsePreviouslyEditedImageActivity extends AppCompatActivity {

    ListView ImageList;
    Adapter adapter;
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    DatabaseHelper helper = new DatabaseHelper(UsePreviouslyEditedImageActivity.this);
    Bitmap selectedImage = null;

    ProgressDialog pdLoading;

    Button deleteAll;




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

        new AsyncCaller().cancel(true);

        if(pdLoading != null)
        {
            pdLoading.dismiss();
        }
        //GlideBitmapPool.clearMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_previously_edited_image);

        pdLoading = new ProgressDialog(UsePreviouslyEditedImageActivity.this);

        //GlideBitmapPool.clearMemory();

        //overridePendingTransition(R.anim.slidein, R.anim.slideout);

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

        ImageList = (ListView) findViewById(R.id.ImageList);
        adapter = new Adapter();
        ImageList.setAdapter(adapter);


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
            //new DatabaseHelper(UsePreviouslyEditedImageActivity.this).createDBAgain();
        }

        //ids = helper.FlipArrayForId(helper.GetAllImagesWithCursor());
        //dates = helper.FlipArrayForDateAndTime(helper.GetAllImagesWithCursor());


        ImageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                selectedImage = images.get(position);
                new AsyncCaller().execute();

            }
        });




    }
    public class Adapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return images.size();
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
            View view = getLayoutInflater().inflate(R.layout.row,null);
            ImageView image = view.findViewById(R.id.image);
            TextView date = view.findViewById(R.id.date);
            ImageView delete = view.findViewById(R.id.delete);
            date.setText(dates.get(position));
            image.setImageBitmap(images.get(position));


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(UsePreviouslyEditedImageActivity.this);
                    builder.setTitle("Delete Image");
                    builder.setIcon(R.drawable.delete);
                    builder.setMessage("Are sure you want to delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(helper.deleteImage(ids.get(position)))
                            {
                                ids.remove(position);
                                images.remove(position);
                                dates.remove(position);
                                Toast.makeText(UsePreviouslyEditedImageActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(UsePreviouslyEditedImageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });
            return view;
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 0, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            ImageList.setEnabled(false);
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params)
        {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            if(!isCancelled())
            {
                if(selectedImage != null)
                {


                    // EditorActivity.imageWidth = Integer.parseInt(width.getText().toString());
                    // EditorActivity.imageHeight = Integer.parseInt(height.getText().toString());

                    MainActivity.images.clear();
                    MainActivity.filePaths.clear();
                    MainActivity.imagePosition = 0;


                    MainActivity.images.add(selectedImage);
                    MainActivity.filePaths.add(getImageUri(getApplicationContext(),selectedImage));
                    MainActivity.CurrentWorkingFilePath = getImageUri(getApplicationContext(),selectedImage);

                    startActivity(new Intent(getApplicationContext(),EditorActivity.class));

                    //ImageList.setEnabled(true);
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            //this method will be running on UI thread

            ImageList.setEnabled(true);
            pdLoading.dismiss();

            images.clear();
            dates.clear();
            ids.clear();
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
}


