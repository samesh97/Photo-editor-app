package com.sba.sinhalaphotoeditor.custom.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;


import com.sba.sinhalaphotoeditor.activities.DrawOnBitmapActivity;
import com.sba.sinhalaphotoeditor.model.DrawnPath;
import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.util.ArrayList;

public class PaintView extends View
{
    private Bitmap btmBackground,btmView,userBitmap;
    private Paint mPaint;
    private Path mPath;
    private int colorBackground,sizeBrush,sizeEraser;

    private Canvas mCanvas;

    private int drawingColor = Color.RED;

    private Context context;
    private ArrayList<DrawnPath> paths;
    private ArrayList<DrawnPath> undonePaths;

    public PaintView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        sizeEraser = sizeBrush = 12;
        colorBackground = Color.TRANSPARENT;

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(sizeBrush);

        paths = new ArrayList<>();
        undonePaths = new ArrayList<>();
        mPath = new Path();
    }
    private float brushSizeToPx()
    {
        return sizeBrush * 1.0f *(getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        userBitmap = resize(userBitmap,w,h);
        btmBackground = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        btmView = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);

    }
    public void setUserBitmap(Context context,Bitmap bitmap)
    {
        this.context = context;
        userBitmap = bitmap.copy(bitmap.getConfig(),true);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(colorBackground);
        canvas.drawBitmap(userBitmap,(getWidth() - userBitmap.getWidth()) / 2.0f ,(getHeight() - userBitmap.getHeight()) / 2.0f,null);
        canvas.drawBitmap(btmBackground,(getWidth() - userBitmap.getWidth()) / 2.0f ,(getHeight() - userBitmap.getHeight()) / 2.0f,null);
        canvas.drawBitmap(btmView,(getWidth() - userBitmap.getWidth()) / 2.0f ,(getHeight() - userBitmap.getHeight()) / 2.0f,null);

    }
    public void setColorBackground(int color)
    {
        colorBackground = color;
        invalidate();
    }
    public void setImage(Bitmap bitmap)
    {
        btmView = bitmap;
        invalidate();
    }
    public void setSizeBrush(int size)
    {
        sizeBrush = size;
        mPaint.setStrokeWidth(sizeBrush);
    }
    public void setBrushColor(int color)
    {
        drawingColor = color;
        mPaint.setColor(color);
    }
    public void setSizeEraser(int s)
    {
        sizeEraser = s;
        mPaint.setStrokeWidth(sizeEraser);
    }
    public void enableEraser()
    {
        //mPaint.setColor(Color.TRANSPARENT);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    public void disableEraser()
    {
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setMaskFilter(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float x = event.getX();
        float y = event.getY();

        if(getWidth() > userBitmap.getWidth())
        {
            x =  x - (getWidth() - userBitmap.getWidth()) / 2.0f;
        }
        if(getHeight() > userBitmap.getHeight())
        {
            y = y - (getHeight() - userBitmap.getHeight()) / 2.0f;
        }


        if(event.getPointerCount() == 2)
        {
            if(context != null && context instanceof DrawOnBitmapActivity)
            {
                if(((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                {
                    ((DrawOnBitmapActivity)context).setMotionEvent(event);
                }

            }
        }


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(event.getPointerCount() == 1)
                {
                    if(context != null && context instanceof DrawOnBitmapActivity)
                    {
                        if(!((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                        {
                            touchStart(x,y);
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1)
                {
                    if(context != null && context instanceof DrawOnBitmapActivity)
                    {
                        if(!((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                        {
                            touchMove(x,y);
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if(context != null && context instanceof DrawOnBitmapActivity)
                {
                    if(((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                    {
                        ((DrawOnBitmapActivity)context).zoomOver(true,event);
                    }

                }
                if(event.getPointerCount() == 1)
                {
                    if(context != null && context instanceof DrawOnBitmapActivity)
                    {
                        if(((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                        {
                            ((DrawOnBitmapActivity)context).setFocusXAndY(x ,y);
                        }

                    }
                }

                if(event.getPointerCount() == 1)
                {
                    if(context != null && context instanceof DrawOnBitmapActivity)
                    {
                        if(!((DrawOnBitmapActivity)context).getUserWantToZoomOrDrawState())
                        {
                            touchUp();
                        }
                    }

                }

                break;


        }

        return true;
    }

    private void touchUp()
    {
        if(mPath != null && !mPath.isEmpty())
        {
            DrawnPath p = new DrawnPath();
            p.setPath(mPath);
            Paint paint = new Paint(mPaint);
            p.setPaint(paint);
            paths.add(p);
        }
        mPath = null;
        drawPaths();

    }

    private void touchMove(float x, float y)
    {
        mPath.lineTo(x,y);
        mCanvas.drawPath(mPath,mPaint);
        postInvalidate();
    }
    public void drawPaths()
    {
        btmView = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);
        for(DrawnPath path : paths)
        {
            mCanvas.drawPath(path.getPath(),path.getPaint());
        }

        postInvalidate();
    }

    private void touchStart(float x, float y)
    {
        mPath = new Path();
        mPath.moveTo(x,y);
    }
    public Bitmap getBitmap()
    {

        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        bitmap = Methods.CropBitmapTransparency(bitmap);
        return bitmap;

    }
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight)
    {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    public void setOpacity(int level)
    {
        mPaint.setColor(adjustAlpha(drawingColor,level));
    }
    public  int adjustAlpha(@ColorInt int color, float factor)
    {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
    public void undo()
    {
        if(paths != null && paths.size() > 0)
        {
            DrawnPath path = paths.get(paths.size() - 1);
            undonePaths.add(path);
            paths.remove(paths.size() - 1);
            drawPaths();
        }
    }
    public void redo()
    {
        if(paths != null && undonePaths != null && undonePaths.size() > 0)
        {
            DrawnPath path = undonePaths.get(undonePaths.size() - 1);
            paths.add(path);
            undonePaths.remove(undonePaths.size() - 1);
            drawPaths();

        }
    }
    public int getPathSize()
    {
        return paths.size();
    }
}
