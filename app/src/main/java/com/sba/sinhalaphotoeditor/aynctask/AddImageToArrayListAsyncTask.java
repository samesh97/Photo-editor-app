package com.sba.sinhalaphotoeditor.aynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.activities.AddStickerOnImage;
import com.sba.sinhalaphotoeditor.activities.EditorActivity;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddImageToArrayListAsyncTask extends AsyncTask<Void,Void,Void>
{
    private Bitmap imageToBeAdded;
    private OnAsyncTaskState listner;

    public AddImageToArrayListAsyncTask(Bitmap imageToBeAdded, OnAsyncTaskState listner)
    {
        this.imageToBeAdded = imageToBeAdded;
        this.listner = listner;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        listner.startActivityForResult();

    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        if(imageToBeAdded != null)
        {
            ImageList.getInstance().addBitmap(imageToBeAdded,true);


            if (EditorActivity.isNeededToDelete)
            {
                try
                {
                    ImageList.getInstance().removeBitmap(ImageList.getInstance().getCurrentPosition() + 1,false);
                }
                catch (Exception e)
                {
                    if(e.getMessage() != null)
                    {
                        Log.d("Error",e.getMessage());
                    }

                }
            }

        }
        return null;
    }
}
