package com.sba.sinhalaphotoeditor.model;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawnPath
{
    private Paint paint;
    private Path path;

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public DrawnPath()
    {

    }
}
