package com.sba.sinhalaphotoeditor.activities.drawOnBitmap;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;

public class PaintView extends View
{
    private Bitmap btmBackground,btmView,userBitmap;
    private Paint mPaint =new Paint();
    private Path mPath = new Path();
    private int colorBackground,sizeBrush,sizeEraser;

    private float mX,mY;
    private Canvas mCanvas;
    private final int DEFERENECE_SPACE = 4;

    private ArrayList<Bitmap> listAction = new ArrayList<>();

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init()
    {
        sizeEraser = sizeBrush = 12;
        colorBackground = Color.TRANSPARENT;

        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(sizeBrush);
    }
    private float toPx(int sizeBrush)
    {
        return sizeBrush*(getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        userBitmap = resize(userBitmap,w,h);

        btmBackground = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        btmView = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);
    }
    public void setUserBitmap(Bitmap bitmap)
    {
        userBitmap = bitmap.copy(bitmap.getConfig(),true);
//        userBitmap = resize(userBitmap,getWidth(),getHeight());
//        userBitmap = bitmap;


//        btmBackground = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
//        btmView = Bitmap.createBitmap(userBitmap.getWidth(),userBitmap.getHeight(),Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(btmView);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(colorBackground);
        canvas.drawBitmap(userBitmap,(getWidth() - btmView.getWidth()) / 2 ,(getHeight() - btmView.getHeight()) / 2,null);
        canvas.drawBitmap(btmBackground,(getWidth() - btmView.getWidth()) / 2 ,(getHeight() - btmView.getHeight()) / 2,null);
        canvas.drawBitmap(btmView,(getWidth() - btmView.getWidth()) / 2 ,(getHeight() - btmView.getHeight()) / 2,null);

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
        mPaint.setColor(color);
    }
    public void setSizeEraser(int s)
    {
        sizeEraser = s;
        mPaint.setStrokeWidth(sizeEraser);
    }
    public void enableEraser()
    {


       // mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }
    public void desableEraser()
    {
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setMaskFilter(null);
    }
    public void addLastAction(Bitmap bitmap)
    {
        listAction.add(bitmap);
    }
    public void returnLastAction()
    {
       if(listAction.size() > 0)
       {
           listAction.remove(listAction.size() - 1);
           if(listAction.size() > 0)
           {
               btmView = listAction.get(listAction.size() - 1);
           }
           else
           {
               btmView = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);

           }
           mCanvas = new Canvas(btmView);
           invalidate();
       }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float x = event.getX();
        float y = event.getY();


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;

        }


        return true;

    }

    private void touchUp()
    {
        mPath.reset();
    }

    private void touchMove(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if(dx >= DEFERENECE_SPACE || dy >= DEFERENECE_SPACE)
        {
            mPath.quadTo(x,y,(x+mX) / 2,(y + mY) / 2 );
            mY = y;
            mX = x;

            mCanvas.drawPath(mPath,mPaint);


            invalidate();
        }
    }

    private void touchStart(float x, float y)
    {
        mPath.moveTo(x,y);
        mX = x;
        mY = y;




    }
    public Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        bitmap = CropBitmapTransparency(bitmap);
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
    public Bitmap CropBitmapTransparency(Bitmap sourceBitmap)
    {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for(int y = 0; y < sourceBitmap.getHeight(); y++)
        {
            for(int x = 0; x < sourceBitmap.getWidth(); x++)
            {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if(alpha > 0)   // pixel is not 100% transparent
                {
                    if(x < minX)
                        minX = x;
                    if(x > maxX)
                        maxX = x;
                    if(y < minY)
                        minY = y;
                    if(y > maxY)
                        maxY = y;
                }
            }
        }
        if((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }
}
