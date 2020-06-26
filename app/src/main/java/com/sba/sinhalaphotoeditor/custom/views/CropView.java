package com.sba.sinhalaphotoeditor.custom.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.config.ExifUtil;
import com.sba.sinhalaphotoeditor.sdk.Methods;

public class CropView extends View
{
    private Rect rectangle;
    private Paint mPaint;

    private Paint mPaintRect;


    //4 handler positions
    private float leftTopX = 100;
    private float leftTopY = 200;
    private float leftBottomX = 100;
    private float leftBottomY = 500;
    private float rightTopX = 400;
    private float rightTopY = 200;
    private float rightBottomX = 400;
    private float rightBottomY = 500;


    //4 handler MAX positions
    private float leftTopMaxX = 100;
    private float leftTopMaxY = 200;
    private float leftBottomMaxX = 100;
    private float leftBottomMaxY = 500;
    private float rightTopMaxX = 400;
    private float rightTopMaxY = 200;
    private float rightBottomMaxX = 400;
    private float rightBottomMaxY = 500;


    //radius of the handler
    private int handlerRadius = 10;

    //source image
    private Bitmap cropBitmap;
    //holds original bitmap
    private Bitmap originalBitmap;


    //rotation value for bitmap
    private int rotateDegree = 0;


    //remove handlers to get the finalBitmap
    private boolean isCropFinished = false;


    //image actual height and width
    private int finalImageWidth,finalImageHeight;


    //cnvas width height
    private int canvasWidth,canvasHeight = 0;


    //padding between the layout
    private int padding = 32;

    public CropView(Context context)
    {

        super(context);
        init();
    }

    public CropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {

        rectangle = new Rect();
        rectangle.set((int) leftTopX,(int)leftTopY,(int)rightTopX,(int)rightBottomY);

        mPaint = new Paint();
        mPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mPaintRect = new Paint();
        mPaintRect.setColor(Color.WHITE);
        mPaintRect.setAntiAlias(true);
        mPaintRect.setStrokeCap(Paint.Cap.ROUND);
        mPaintRect.setStrokeWidth(10);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setDither(true);
        mPaintRect.setStrokeJoin(Paint.Join.ROUND);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);

        //draw image
        if(cropBitmap != null)
        {
            canvas.drawBitmap(cropBitmap,(getWidth() - cropBitmap.getWidth()) / 2.0f ,(getHeight() - cropBitmap.getHeight()) / 2.0f,null);
        }

        if(!isCropFinished)
        {
            rectangle.set((int) leftTopX,(int)leftTopY,(int)rightTopX,(int)rightBottomY);

            //draw rectangle
            canvas.drawRect(rectangle,mPaintRect);
            //left top handler
            canvas.drawCircle(leftTopX,leftTopY,handlerRadius,mPaint);
            //left bottom handler
            canvas.drawCircle(leftBottomX,leftBottomY,handlerRadius,mPaint);
            //right top handler
            canvas.drawCircle(rightTopX,rightTopY,handlerRadius,mPaint);
            //right bottom handler
            canvas.drawCircle(rightBottomX,rightBottomY,handlerRadius,mPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);



        canvasWidth = w;
        canvasHeight = h;


        this.cropBitmap = resize(cropBitmap,canvasWidth - padding,canvasHeight - padding);
        this.originalBitmap = resize(originalBitmap,canvasWidth - padding,canvasHeight - padding);

        setHandlerPositions();


    }
    public void setHandlerPositions()
    {
        float x = (getWidth() - cropBitmap.getWidth()) / 2.0f;
        float y = (getHeight() - cropBitmap.getHeight()) / 2.0f;

        float xx = (getWidth() + cropBitmap.getWidth()) / 2.0f;
        float yy = (getHeight() + cropBitmap.getHeight()) / 2.0f;


        leftTopY = y;
        rightTopY = y;

        leftTopX = x;
        leftBottomX = x;

        rightTopX = xx;
        rightBottomX = xx;

        leftBottomY = yy;
        rightBottomY = yy;



        leftTopMaxY = y;
        rightTopMaxY = y;

        leftTopMaxX = x;
        leftBottomMaxX = x;

        rightTopMaxX = xx;
        rightBottomMaxX = xx;

        leftBottomMaxY = yy;
        rightBottomMaxY = yy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean value  = super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            moveHandler(event.getX(), event.getY());
        }

        return true;
    }

    private void moveHandler(float x, float y)
    {
        //consider as clicked the handler through this area
        float tolerance = 75;
        float minSpace = 150;
        float reduceSpace = 5;

        //check for the top left handler was dragged
        if(x <= (leftTopX + tolerance) && x >= leftTopX - tolerance)
        {
            if(y <= leftTopY + tolerance && y >= leftTopY - tolerance)
            {

                //check for the min collaps
                if((rightTopX - leftTopX) <= minSpace)
                {
                    leftTopX -= reduceSpace;
                    leftBottomX = leftTopX;
                    return;
                }

                if((leftBottomY - leftTopY) <= minSpace)
                {
                    leftTopY -= reduceSpace;
                    rightTopY = leftTopY;
                    return;
                }



                //check for the max expanded
                if(leftTopMaxY <= y)
                {
                    leftTopY = y;
                    rightTopY = y;
                }
                else
                {
                    leftTopY = leftTopMaxY;
                    rightTopY = rightTopMaxY;
                }


                if(leftTopMaxX <= x)
                {
                    leftTopX = x;
                    leftBottomX = x;
                }
                else
                {
                    leftTopX = leftTopMaxX;
                    leftBottomX = leftBottomMaxX;
                }

                postInvalidate();
            }
        }

        //check for the top right handler was dragged
        if(x <= (rightTopX + tolerance) && x >= rightTopX - tolerance)
        {
            if(y <= rightTopY + tolerance && y >= rightTopY - tolerance)
            {

                //check for the min collaps
                if((rightTopX - leftTopX) <= minSpace)
                {
                    rightTopX += reduceSpace;
                    rightBottomX = rightTopX;
                    return;
                }

                if((rightBottomY - rightTopY) <= minSpace)
                {
                    rightTopY -= reduceSpace;
                    leftTopY = rightTopY;
                    return;
                }


                if(rightTopMaxY <= y)
                {
                    rightTopY = y;
                    leftTopY = y;

                }
                else
                {
                    rightTopY = rightTopMaxY;
                    leftTopY = leftTopMaxY;
                }


                if(rightTopMaxX >= x)
                {
                    rightTopX = x;
                    rightBottomX = x;
                }
                else
                {
                    rightTopX = rightTopMaxX;
                    rightBottomX = rightBottomMaxX;
                }

                postInvalidate();
            }
        }

        //check for the left bottom handler was dragged
        if(x <= (leftBottomX + tolerance) && x >= leftBottomX - tolerance)
        {
            if(y <= leftBottomY + tolerance && y >= leftBottomY - tolerance)
            {


                //check for the min collaps
                if((leftBottomY - leftTopY) <= minSpace)
                {
                    leftBottomY += reduceSpace;
                    rightBottomY = leftBottomY;
                    return;
                }

                if((rightBottomX - leftBottomX) <= minSpace)
                {
                    leftBottomX -= reduceSpace;
                    leftTopX = leftBottomX;
                    return;
                }




                if(leftBottomMaxY >= y)
                {
                    leftBottomY = y;
                    rightBottomY = y;

                }
                else
                {
                    leftBottomY = leftBottomMaxY;
                    rightBottomY = rightBottomMaxY;
                }


                if(leftBottomMaxX <= x)
                {
                    leftBottomX = x;
                    leftTopX = x;
                }
                else
                {
                    leftBottomX = leftBottomMaxX;
                    leftTopX = leftTopMaxX;
                }

                postInvalidate();
            }
        }

        //check for the right bottom handler was dragged
        if(x <= (rightBottomX + tolerance) && x >= rightBottomX - tolerance)
        {
            if(y <= rightBottomY + tolerance && y >= rightBottomY - tolerance)
            {


                //check for the min collaps
                if((rightBottomY - rightTopY) <= minSpace)
                {
                    rightBottomY += reduceSpace;
                    leftBottomY = rightBottomY;
                    return;
                }

                if((rightBottomX - leftBottomX) <= minSpace)
                {
                    rightBottomX += reduceSpace;
                    rightTopX = rightBottomX;
                    return;
                }




                if(rightBottomMaxY >= y)
                {
                    rightBottomY = y;
                    leftBottomY = y;

                }
                else
                {
                    rightBottomY = rightBottomMaxY;
                    leftBottomY = leftBottomMaxY;
                }


                if(rightBottomMaxX >= x)
                {
                    rightBottomX = x;
                    rightTopX = x;
                }
                else
                {
                    rightBottomX = rightBottomMaxX;
                    rightTopX = rightTopMaxX;
                }

                postInvalidate();
            }
        }



    }
    public void setBitmap(Bitmap bitmap)
    {
        this.cropBitmap = bitmap.copy(bitmap.getConfig(),true);
        finalImageHeight = bitmap.getHeight();
        finalImageWidth = bitmap.getWidth();
        originalBitmap = bitmap;

        //keeping the padding with the layout
        this.cropBitmap = resize(cropBitmap,canvasWidth - padding,canvasHeight - padding);

        setHandlerPositions();
        postInvalidate();
    }
    public void flipInHorizontal()
    {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, cropBitmap.getWidth() / 2.0f, cropBitmap.getHeight() / 2.0f);

        cropBitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);

        postInvalidate();
    }
    public void flipInVertical()
    {
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1,cropBitmap.getWidth() / 2.0f, cropBitmap.getHeight() / 2.0f);
        cropBitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);

        postInvalidate();
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
    public Bitmap getBitmap()
    {

        isCropFinished = true;
        postInvalidate();

        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        bitmap =  Bitmap.createBitmap(bitmap,rectangle.left,rectangle.top,rectangle.right - rectangle.left,rectangle.bottom - rectangle.top);
        bitmap = resize(bitmap,finalImageWidth,finalImageHeight);
        return bitmap;
    }
    public int getCanvasWidth()
    {
        return canvasWidth;
    }
    public int getCanvasHeight()
    {
        return canvasHeight;
    }
}
