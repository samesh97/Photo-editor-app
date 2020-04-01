package com.sba.sinhalaphotoeditor.CallBacks;

import android.graphics.Typeface;

public interface OnTextAttributesChangedListner
{
    public void onTextColorChanged(int color);
    public void onTextShadowColorChanged(int color);
    public void onTextFontChanged(Typeface typeFace);
}
