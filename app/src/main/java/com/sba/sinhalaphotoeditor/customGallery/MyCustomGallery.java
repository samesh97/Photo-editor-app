package com.sba.sinhalaphotoeditor.customGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.AdjustImage;
import com.sba.sinhalaphotoeditor.BlurUtils;
import com.sba.sinhalaphotoeditor.ExifUtil;
import com.sba.sinhalaphotoeditor.MainActivity;
import com.sba.sinhalaphotoeditor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sba.sinhalaphotoeditor.MainActivity.rotateBitmap;

public class MyCustomGallery extends AppCompatActivity {


    private ImageView pickedImage;
    public static Bitmap selectedBitmap = null;

    public static final int IMAGE_PICK_RESULT_CODE = 235;

    private String activityString = null;
    ArrayList<File> allImages = new ArrayList<>();

    ArrayList<File> images = new ArrayList<>();

    GridView gridView;

    ProgressDialog dialog;
    GridViewAdapter adapter;


    SetData setData;


    @Override
    protected void onDestroy()
    {
        if(!setData.isCancelled())
        {
            setData.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.photo_on_photo_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.setImage)
        {
            if(selectedBitmap != null)
            {

                if(activityString.equals("MainActivity"))
                {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    setResult(IMAGE_PICK_RESULT_CODE,intent);
                    finish();
                    if(setData != null)
                    {
                        if(!setData.isCancelled())
                        {
                            setData.cancel(true);
                        }

                    }
                }
                else if(activityString.equals("EditorActivity"))
                {
                    Intent intent = new Intent();
                    setResult(IMAGE_PICK_RESULT_CODE,intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    if(setData != null)
                    {
                        if(!setData.isCancelled())
                        {
                            setData.cancel(true);
                        }

                    }
                }

            }
            else
            {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_custom_gallery);


        Intent intent = getIntent();
        activityString = intent.getStringExtra("Activity");

        pickedImage = (ImageView) findViewById(R.id.pickedImage);
        gridView = findViewById(R.id.gridView);

        dialog = new ProgressDialog(MyCustomGallery.this);
        dialog.setTitle("Loading Images..");
        dialog.setMessage("This may take a while depending on number of images you have in gallery");




        adapter = new GridViewAdapter(images);
        gridView.setAdapter(adapter);
        setData = new SetData();
        setData.execute();







    }
    public class GridViewAdapter extends BaseAdapter
    {
        ArrayList<File> images;

        public GridViewAdapter(ArrayList<File> images)
        {
            this.images = images;
        }
        @Override
        public int getCount() {
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
            ViewHolder holder;

            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.custom_gallery_row, null, false);
                holder = new ViewHolder();
                holder.imgView = (ImageView)convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            Glide.with(getApplicationContext()).load(images.get(position)).into(holder.imgView);

            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(getBitmap(images.get(position).toString()) != null)
                    {
                        Glide.with(getApplicationContext()).load(images.get(position)).into(pickedImage);
                        selectedBitmap = getBitmap(images.get(position).toString());




                        selectedBitmap = ExifUtil.rotateBitmap(images.get(position).toString(), selectedBitmap);






                        Bitmap bluredImage = getBlurBitmap(selectedBitmap.copy(selectedBitmap.getConfig(),true),getApplicationContext());
                        //Bitmap bluredImage = new BlurUtils().blur(MyCustomGallery.this,selectedBitmap.copy(selectedBitmap.getConfig(),true), 100);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bluredImage);

                        pickedImage.setBackground(ob);
                        bluredImage = null;
                    }

                }
            });



            //View view = getLayoutInflater().inflate(R.layout.custom_gallery_row,null);
            //ImageView imageView = view.findViewById(R.id.image);
            //Glide.with(getApplicationContext()).load(images.get(position)).into(imageView);
            return convertView;
        }
        public class ViewHolder
        {
            private ImageView imgView;
        }
    }
    public void getAllImages(Activity activity)
    {

        //Remove older images to avoid copying same image twice

        allImages.clear();
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null, imageName;

        //get all images from external storage

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

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
                    //allImages.add(new File(absolutePathOfImage));

                    images.add(new File(absolutePathOfImage));

                    if(images.size() == 1)
                    {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run()
                                    {

                                        Glide.with(getApplicationContext()).load(images.get(0)).into(pickedImage);
                                        selectedBitmap = getBitmap(images.get(0).toString());

                                        selectedBitmap = ExifUtil.rotateBitmap(images.get(0).toString(), selectedBitmap);


                                        Bitmap bluredImage = getBlurBitmap(selectedBitmap.copy(selectedBitmap.getConfig(),true),getApplicationContext());
                                        //Bitmap bluredImage = new BlurUtils().blur(MyCustomGallery.this,selectedBitmap.copy(selectedBitmap.getConfig(),true), 100);
                                        BitmapDrawable ob = new BitmapDrawable(getResources(), bluredImage);

                                        pickedImage.setBackground(ob);
                                        bluredImage = null;
                                    }
                                });




                    }




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            if(images.size() % 6 == 0)
                            {
                                adapter.notifyDataSetChanged();
                            }

                        }
                    });
                }

            }


        }

        // Get all Internal storage images

        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

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

                    images.add(new File(absolutePathOfImage));

                    if(images.size() == 1)
                    {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {

                                Glide.with(getApplicationContext()).load(images.get(0)).into(pickedImage);
                                selectedBitmap = getBitmap(images.get(0).toString());

                                selectedBitmap = ExifUtil.rotateBitmap(images.get(0).toString(), selectedBitmap);


                                Bitmap bluredImage = getBlurBitmap(selectedBitmap.copy(selectedBitmap.getConfig(),true),getApplicationContext());
                               // Bitmap bluredImage = new BlurUtils().blur(MyCustomGallery.this,selectedBitmap.copy(selectedBitmap.getConfig(),true), 100);
                                BitmapDrawable ob = new BitmapDrawable(getResources(), bluredImage);

                                pickedImage.setBackground(ob);
                                bluredImage = null;
                            }
                        });




                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(images.size() % 6 == 0)
                            {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
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
            dialog.show();
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
            dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if(!this.isCancelled())
            {
                getAllImages(MyCustomGallery.this);
            }

            return null;
        }
    }
    public Bitmap getBlurBitmap(Bitmap image, Context context)
    {
        final float BITMAP_SCALE = 0.01f;
        final float BLUR_RADIUS = 25f;
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
