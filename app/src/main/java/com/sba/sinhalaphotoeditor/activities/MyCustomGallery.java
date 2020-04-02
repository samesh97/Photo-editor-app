package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.Config.ExifUtil;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class MyCustomGallery extends AppCompatActivity {


    private ImageView pickedImage;
    public static Bitmap selectedBitmap = null;

    public static final int IMAGE_PICK_RESULT_CODE = 235;

    private String activityString = null;
    private static ArrayList<File> allImages = new ArrayList<>();
    private ArrayList<File> images = new ArrayList<>();

    GridView gridView;

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

                Methods.showCustomToast(MyCustomGallery.this,getResources().getString(R.string.select_an_image_first_text));

                //Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
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
        public int getCount()
        {
            if(images.size() % 2 == 0)
            {
                return images.size() / 2;
            }
            else
            {
                return (images.size() / 2) + 1;
            }

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
                holder.imgView2 = (ImageView)convertView.findViewById(R.id.image2);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            if(images.size() > position * 2)
            {
                Glide.with(getApplicationContext()).load(images.get(position * 2)).into(holder.imgView);
            }
            if(images.size() > position + 1)
            {
                Glide.with(getApplicationContext()).load(images.get(position + 1)).into(holder.imgView2);
            }


            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(getBitmap(images.get(position * 2).toString()) != null)
                    {
                        Glide.with(getApplicationContext()).load(images.get(position * 2)).into(pickedImage);
                        selectedBitmap = getBitmap(images.get(position * 2).toString());

                        selectedBitmap = ExifUtil.rotateBitmap(images.get(position * 2).toString(), selectedBitmap);






                        Bitmap bluredImage = getBlurBitmap(selectedBitmap.copy(selectedBitmap.getConfig(),true),getApplicationContext());
                        //Bitmap bluredImage = new BlurUtils().blur(MyCustomGallery.this,selectedBitmap.copy(selectedBitmap.getConfig(),true), 100);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bluredImage);

                        pickedImage.setBackground(ob);
                        bluredImage = null;
                    }

                }
            });

            holder.imgView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(getBitmap(images.get(position + 1).toString()) != null)
                    {
                        Glide.with(getApplicationContext()).load(images.get(position + 1)).into(pickedImage);
                        selectedBitmap = getBitmap(images.get(position + 1).toString());

                        selectedBitmap = ExifUtil.rotateBitmap(images.get(position + 1).toString(), selectedBitmap);






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
            private ImageView imgView2;
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
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };

        Log.d("CheckGallery",projection.toString());

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
                            adapter.notifyDataSetChanged();
//                            if(images.size() % 6 == 0)
//                            {
//                                adapter.notifyDataSetChanged();
//                            }

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
