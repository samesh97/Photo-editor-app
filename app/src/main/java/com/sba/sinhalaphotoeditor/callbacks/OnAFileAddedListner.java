package com.sba.sinhalaphotoeditor.callbacks;

import com.sba.sinhalaphotoeditor.model.GalleryImage;

public interface OnAFileAddedListner
{
    public void fileAdded(GalleryImage image);
    public void notifyAdapter();
}
