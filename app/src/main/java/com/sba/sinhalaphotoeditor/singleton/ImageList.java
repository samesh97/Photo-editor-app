package com.sba.sinhalaphotoeditor.singleton;

import android.graphics.Bitmap;

import com.glidebitmappool.GlideBitmapPool;
import com.sba.sinhalaphotoeditor.activities.MainActivity;

import java.util.ArrayList;

public class ImageList
{
    private static ImageListModel object = null;
    private ImageList()
    {

    }
    public static ImageListModel getInstance()
    {
        if(object == null)
        {
            object = new ImageListModel();
        }

        return object;
    }
    public static class ImageListModel
    {
        private ArrayList<Bitmap> bitmapList = new ArrayList<>();
        private int imagePosition = 0;

        private ImageListModel()
        {

        }
        public void addBitmap(Bitmap bitmap,boolean isImagePositionIncrease)
        {
            if(object == null)
            {
                getInstance();
            }
            object.bitmapList.add(bitmap);
            if(isImagePositionIncrease)
            {
                setImagePosition(object.bitmapList.size() - 1);
            }
        }
        public void addBitmapToThisPosition(Bitmap bitmap,int position,boolean isImagePositionIncrease)
        {
            if(object == null)
            {
                getInstance();
            }
            object.bitmapList.add(position,bitmap);
            if(isImagePositionIncrease)
            {
                increaseImagePosition();
            }
        }
        public void removeBitmap(int postion,boolean isImagePositioDecrease)
        {
            if(object.bitmapList != null && object.bitmapList.size() > postion && postion <= 0)
            {
                object.bitmapList.remove(postion);
                if(isImagePositioDecrease)
                {
                    decreaseImagePosition();
                }
            }

        }
        private void increaseImagePosition()
        {
            object.imagePosition++;
        }
        private void setImagePosition(int size)
        {
            object.imagePosition = size;
        }
        private void decreaseImagePosition()
        {
            if(object.imagePosition >= 1)
            {
                object.imagePosition--;
            }

        }
        public Bitmap getCurrentBitmap()
        {
            Bitmap bitmap = null;
            if(object.bitmapList.size() > object.imagePosition && object.imagePosition >= 0)
            {
                bitmap = object.bitmapList.get(object.imagePosition);
            }
            else
            {
                bitmap = object.bitmapList.get(object.bitmapList.size() - 1);
            }
            return bitmap;
        }
        public void deleteUndoRedoImages()
        {

            if(object.bitmapList.size() > 6)
            {
                int size = object.bitmapList.size() - 6;
                object.imagePosition = object.imagePosition - size;
                for(int i = 1; i <= size; i++)
                {
                    if(i == object.imagePosition)
                    {
                        size++;
                    }
                    else
                    {

                        object.bitmapList.remove(i);
                        GlideBitmapPool.clearMemory();
                    }
                }


            }
        }
        public void clearImageList()
        {
            if(object == null)
            {
                getInstance();
            }
            object.bitmapList.clear();
            object.imagePosition = 0;
        }
        public int getCurrentPosition()
        {
            return object.imagePosition;
        }
        public int getImageListSize()
        {
            return object.bitmapList.size();
        }
        public void substractImagePosition(int size)
        {
            object.imagePosition = object.imagePosition - size;
        }
        public void increaseImagePosition(int size)
        {
            object.imagePosition = object.imagePosition + size;
        }



    }

}
