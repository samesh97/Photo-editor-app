package com.sba.sinhalaphotoeditor.custom.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sba.sinhalaphotoeditor.R;


public class GridView extends View
{

    private int screenWidth;
    private int screenHeight;
    public  int GAP_WIDTH_DP = 50;
    private float GAP_WIDTH_PIXEL;
    private  Paint paint = new Paint();

    public GridView(Context context) {
        super(context);
        init(context);
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void init(Context context)
    {

        // set paint color
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);

        // get view dimentions
        getScreenDimensions();

    }

    private void getScreenDimensions()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //gap size in pixel
        GAP_WIDTH_PIXEL = convertDpToPixel(GAP_WIDTH_DP,  getContext());

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getScreenDimensions();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawColor(Color.TRANSPARENT);
        // draw Horizontal line from Y= 0 -> Y+=Gap... till screen width


        float verticalPosition = (getHeight() % GAP_WIDTH_PIXEL) / 2.0f;

        while (verticalPosition <= screenHeight) {

            canvas.drawLine(0, verticalPosition,
                    screenWidth, verticalPosition, paint);

            verticalPosition += GAP_WIDTH_PIXEL;

        }

        // draw Vertical line from X=0 -> X+=Gap... till screen Height 0|||hor+gap|||W
        float horizontalPosition = (getWidth() % GAP_WIDTH_PIXEL) / 2.0f;

        while (horizontalPosition <= screenWidth) {

            canvas.drawLine(horizontalPosition, 0,
                    horizontalPosition, screenHeight,paint);

            horizontalPosition += GAP_WIDTH_PIXEL;

        }

    }
    public void setGapWidth(int size)
    {
        if(size <= 25)
        {
            return;
        }

        GAP_WIDTH_DP = size;
        GAP_WIDTH_PIXEL = convertDpToPixel(GAP_WIDTH_DP,  getContext());
        postInvalidate();

    }
    public int getGapWidth()
    {
        return GAP_WIDTH_DP;
    }
}
