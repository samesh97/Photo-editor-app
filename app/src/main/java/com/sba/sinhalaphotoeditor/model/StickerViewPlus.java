package com.sba.sinhalaphotoeditor.model;


import android.content.Context;
import android.widget.ImageView;

public class StickerViewPlus
{
    private Context context;
    private ImageView sticker;
    private float opacityLevel;
    private float angle;


    public StickerViewPlus(Context context)
    {
        this.context = context;
        sticker = new ImageView(context);
        this.opacityLevel = 1f;
        this.angle = 0;
    }
}
