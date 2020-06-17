package com.sba.sinhalaphotoeditor.CallBacks;

import com.sba.sinhalaphotoeditor.model.GalleryImage;

public interface OnAFileAddedListner
{
    public void fileAdded(GalleryImage image);
    public void notifyAdapter();
}
