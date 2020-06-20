package com.sba.sinhalaphotoeditor.aynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import com.sba.sinhalaphotoeditor.CallBacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.activities.EditorActivity;
import com.sba.sinhalaphotoeditor.singleton.ImageList;


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

        listner.startActivityForResult();
        super.onPostExecute(aVoid);

    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        if(imageToBeAdded != null)
        {
            ImageList.getInstance().addBitmap(imageToBeAdded,true);
        }
        return null;
    }
}