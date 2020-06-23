package com.sba.sinhalaphotoeditor.aynctask;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.sba.sinhalaphotoeditor.callbacks.OnAFileAddedListner;
import com.sba.sinhalaphotoeditor.activities.MyCustomGallery;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.sba.sinhalaphotoeditor.sdk.Methods.getBitmap;

public class GalleryImageHandler extends AsyncTask<Void,Void,Void>
{
    private static HashMap<String, GalleryImage> imageList = null;
    private OnAFileAddedListner listner;
    private ContentResolver contentResolver;

    public GalleryImageHandler(ContentResolver contentResolver,OnAFileAddedListner listner)
    {
        this.contentResolver = contentResolver;
        this.listner = listner;
        if(imageList == null)
        {
            imageList = new HashMap<>();
        }

    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        getAllImages();
        return null;
    }
    public void getAllImages()
    {


        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

        String absolutePathOfImage, imageName;

        //get all images from external storage

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };


        cursor = contentResolver.query(uri, projection, null,
                null, orderBy);



        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);




        while (cursor.moveToNext())
        {

            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            if(absolutePathOfImage != null && !absolutePathOfImage.equals(""))
            {
                try
                {
                    if(getBitmap(new File(absolutePathOfImage).toString()) != null)
                    {
                        GalleryImage imgObj = new GalleryImage(new File(absolutePathOfImage));
                        imageList.put(absolutePathOfImage,imgObj);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            if(isCancelled() && imageList.size() >= MyCustomGallery.imageLimit)
            {
                Log.d("Running","Stopped " + imageList.size());
                break;
            }
            Log.d("Running","Running " + imageList.size());

        }

        // Get all Internal storage images

        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;



        cursor = contentResolver.query(uri, projection, null,
                null, orderBy);




        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);



        while (cursor.moveToNext())
        {

            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            if(absolutePathOfImage != null && !absolutePathOfImage.equals(""))
            {
                try
                {
                    if(getBitmap(new File(absolutePathOfImage).toString()) != null)
                    {
                        GalleryImage imgObj = new GalleryImage(new File(absolutePathOfImage));
                        imageList.put(absolutePathOfImage,imgObj);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(isCancelled() && imageList.size() >= MyCustomGallery.imageLimit)
            {
                Log.d("Running","Stopped " + imageList.size());
                break;
            }

            Log.d("Running","Running " + imageList.size());
        }


        cursor.close();
    }
    public void getList(ArrayList<GalleryImage> list,int size)
    {


            if(imageList != null)
            {

                int currentSize = list.size();
                int requiredSize = currentSize + size;

                ArrayList<GalleryImage> currentList = new ArrayList<>(imageList.values());

                for(int i = currentSize; i < requiredSize; i++)
                {
                    if(i < currentList.size())
                    {
                        list.add(currentList.get(i));
                        if(listner != null)
                        {
                            listner.notifyAdapter();
                        }
                    }
                }
            }

    }
    private int getImageCount()
    {
        int count = 0;
        Uri uri;
        Cursor cursor;
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null, imageName;

        //get all images from external storage

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };


        cursor = contentResolver.query(uri, projection, null,
                null, orderBy);

        if(cursor != null)
        count+= cursor.getCount();

        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        cursor = contentResolver.query(uri, projection, null,
                null, orderBy);

        if(cursor != null)
        count+= cursor.getCount();

        if(cursor != null)
        cursor.close();

        return count;
    }
    public void setListEmpty()
    {
        if(imageList != null)
        {
            imageList.clear();
        }
    }
    public int getListSize()
    {
        if(imageList != null)
        {
            return imageList.size();
        }
        return 0;
    }
}
