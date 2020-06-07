package com.sba.sinhalaphotoeditor.CallBacks;

import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;

public interface OnBitmapChanged
{
    public void bitmapChanged(Bitmap bitmap, Filter filter);
}
