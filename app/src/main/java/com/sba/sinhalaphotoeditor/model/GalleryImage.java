package com.sba.sinhalaphotoeditor.model;

import java.io.File;

public class GalleryImage
{
    private File file;
    private boolean isSelected = false;
    public GalleryImage(File file)
    {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
