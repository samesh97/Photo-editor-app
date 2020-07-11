package com.sba.sinhalaphotoeditor.custom.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.sba.sinhalaphotoeditor.sdk.Methods;

public class ImageViewPlus extends View
{
    private Bitmap originalBitmap;
    private Bitmap workingBitmap;
    private int canvasWidth;
    private int canvasHeight;
    private  int borderSize;
    private RectF borderRect;
    private Paint paint;
    private Paint bitmapPaint;
    private int borderColor;
    private int borderRadius;

    private int padding = 10;

    public ImageViewPlus(Context context)
    {
        super(context);
        init();
    }

    public ImageViewPlus(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageViewPlus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ImageViewPlus(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        borderRadius = 10;
        borderSize = 10;
        borderColor = Color.WHITE;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(borderSize);
        paint.setColor(borderColor);
        paint.setAntiAlias(true);

        borderRect = new RectF();

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setShadowLayer(3.0f, 0.0f, 2.0f, Color.YELLOW);

    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT);
        if(workingBitmap != null)
        {
            float marginX = (getWidth() - workingBitmap.getWidth()) / 2.0f;
            float marginY = (getHeight() - workingBitmap.getHeight()) / 2.0f;


            if(borderSize > 0)
            {
                float borderStartX = marginX;
                float borderStartY = marginY;
                float borderEndX = marginX + workingBitmap.getWidth();
                float borderEndY = marginY + workingBitmap.getHeight();


                borderRect.left = borderStartX;
                borderRect.right = borderEndX;
                borderRect.top = borderStartY;
                borderRect.bottom = borderEndY;

                canvas.drawRoundRect(borderRect,borderRadius,borderRadius,paint);
            }


            canvas.drawBitmap(workingBitmap,marginX,marginY,null);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasHeight = h;
        canvasWidth = w;
        resizeBitmap();

    }
    private void resizeBitmap()
    {
        if(originalBitmap != null)
        {
            int width = getWidth() - borderSize - padding;
            int height = getHeight() - borderSize - padding;

            if(width < borderSize)
            {
                width = borderSize - padding;
            }
            if(height < borderSize)
            {
                height = borderSize - padding;
            }
            workingBitmap = Methods.resize(originalBitmap.copy(originalBitmap.getConfig(),true),width,height);
            postInvalidate();
        }
    }
    public void setBitmap(Bitmap bitmap)
    {
        this.originalBitmap = bitmap;
        resizeBitmap();
    }
    public void setBorderSize(int size)
    {
        borderSize = size;
        paint.setStrokeWidth(borderSize);
        resizeBitmap();
    }
    public void setBorderColor(int color)
    {
        borderColor = color;
        paint.setColor(borderColor);
        postInvalidate();
    }
    public void setBorderRadius(int radius)
    {

        this.borderRadius = radius;
        postInvalidate();
    }
    public void onSizeChanged()
    {
        Log.d("Called","Canvas Width " + getWidth() + " Canvas Height : " + getHeight());
        Log.d("Called","Bitmap Width " + workingBitmap.getWidth() + " Bitmap Height : " + workingBitmap.getHeight());
        resizeBitmap();
    }
    public int getBorderColor()
    {
        return borderColor;
    }
    public int getBorderSize()
    {
        return borderSize;
    }
    public int getBorderRadius()
    {
        return borderRadius;
    }

}
