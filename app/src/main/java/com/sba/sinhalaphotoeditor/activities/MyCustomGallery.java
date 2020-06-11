package com.sba.sinhalaphotoeditor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sba.sinhalaphotoeditor.Config.ExifUtil;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.GridViewAdapter;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyCustomGallery extends AppCompatActivity {


    public static Bitmap selectedBitmap = null;
    public static final int IMAGE_PICK_RESULT_CODE = 235;
    private ArrayList<GalleryImage> allImages = new ArrayList<>();

    private static ArrayList<GalleryImage> showingImages;

    private RecyclerView gridView;
    private static GridViewAdapter adapter;
    private SetData setData;
    private boolean isStillShowing = false;
    private LinearLayoutManager manager;
    private static int lastScrolledPosition = 0;




    @Override
    protected void onDestroy()
    {
        if(setData != null && !setData.isCancelled())
        {
            setData.cancel(true);
        }
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_custom_gallery);



        gridView = findViewById(R.id.gridView);
        manager = new LinearLayoutManager(MyCustomGallery.this);

        if(adapter == null)
        {
            showingImages = new ArrayList<>();
            gridView.setLayoutManager(manager);
            adapter = new GridViewAdapter(MyCustomGallery.this,showingImages);
            gridView.setAdapter(adapter);

        }
        else
        {
            adapter.setContext(this);
            gridView.setAdapter(adapter);
            gridView.setLayoutManager(manager);

            gridView.scrollToPosition(lastScrolledPosition);
        }

        setData = new SetData();
        setData.execute();



        gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    setShowingImageList();
                }

                lastScrolledPosition =   manager.findFirstCompletelyVisibleItemPosition();

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }
    public void getAllImages(Context activity)
    {

        allImages.clear();

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null, imageName;

        //get all images from external storage

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };


        cursor = activity.getContentResolver().query(uri, projection, null,
                null, "date_modified DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        while (cursor.moveToNext())
        {

            if(setData.isCancelled())
            {
                break;
            }
            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            if(absolutePathOfImage != null && !absolutePathOfImage.equals(""))
            {
                if(getBitmap(new File(absolutePathOfImage).toString()) != null)
                {
                    GalleryImage imgObj = new GalleryImage(new File(absolutePathOfImage));

                    allImages.add(imgObj);




                   if(showingImages.size() <= 11)
                   {

                       showingImages.add(allImages.get(0));
                       allImages.remove(0);

                       runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                adapter.notifyDataSetChanged();
                            }
                        });
                   }

                }

            }


        }

        // Get all Internal storage images

        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, "date_modified DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        while (cursor.moveToNext())
        {
            if(setData.isCancelled())
            {
                break;
            }

            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            if(absolutePathOfImage != null && !absolutePathOfImage.equals(""))
            {
                if(getBitmap(new File(absolutePathOfImage).toString()) != null)
                {
                    GalleryImage imgObj = new GalleryImage(new File(absolutePathOfImage));


                    allImages.add(imgObj);

                    if(showingImages.size() <= 11)
                    {

                        showingImages.add(allImages.get(0));
                        allImages.remove(0);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }


        cursor.close();
    }
    public Bitmap getBitmap(String path)
    {

        Bitmap bitmap = null;

        try {

            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }
    public class SetData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if(!setData.isCancelled())
            {
                setData.cancel(true);
            }
            adapter.notifyDataSetChanged();

        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if(!this.isCancelled())
            {
                getAllImages(getApplicationContext());
            }

            return null;
        }
    }
    public void setShowingImageList()
    {

        Log.d("ImageListSizes","MainList = " + allImages.size() + " ShowingList = " + showingImages.size());

        for(int i = 0; i < 10; i++)
        {
            if(allImages != null && allImages.size() > 0)
            {
                showingImages.add(allImages.get(0));
                allImages.remove(0);
                adapter.notifyDataSetChanged();
            }

            //when the last position reaches
            if(i == 9)
            {
                adapter.notifyItemInserted(adapter.getItemCount() + (allImages.size() / 3));
            }
        }
    }
    public void showSelectView(File file)
    {
        if(!isStillShowing)
        {
            isStillShowing = true;
            final ConstraintLayout select_image_layout = findViewById(R.id.select_image_layout);
            CircleImageView picture = findViewById(R.id.picture);
            Button select_btn = findViewById(R.id.select_btn);

            final Bitmap bitmap;
            try
            {
                bitmap = Methods.getResizedBitmapWithURI(getApplicationContext(),Uri.fromFile(file),10);
                if(bitmap != null)
                {
                    Glide.with(MyCustomGallery.this)
                            .load(file).into(picture);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            select_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(selectedBitmap != null)
                    {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        setResult(IMAGE_PICK_RESULT_CODE,intent);
                        finish();
                        overridePendingTransition(R.anim.activity_start_animation__for_tools,R.anim.activity_exit_animation__for_tools);
                        if(setData != null)
                        {
                            if(!setData.isCancelled())
                            {
                                setData.cancel(true);
                            }

                        }

                    }
                    else
                    {
                        Methods.showCustomToast(MyCustomGallery.this,getResources().getString(R.string.select_an_image_first_text));
                    }
                }
            });
            select_image_layout.setVisibility(View.VISIBLE);
            select_image_layout.animate().setDuration(250).translationYBy(200).translationY(0).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    isStillShowing = false;
                    select_image_layout.animate().setDuration(500).translationYBy(0).translationY(200).start();
                }
            },2000);

        }

    }
}