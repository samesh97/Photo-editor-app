package com.sba.sinhalaphotoeditor.progressdialog;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.R;


public class PhotoEditorProgressDialog
{
    private Activity activity;

    private LinearLayout mainLinearLayout = null;
    private LinearLayout lastMainLayout = null;


    public PhotoEditorProgressDialog()
    {

    }

    public PhotoEditorProgressDialog(Activity context)
    {
        this.activity = context;
    }
    public void show()
    {

        //stoping if currently running the animation to avoid duplicate views on the screen
        dismiss();
        //initialize the required views programmatically
        init();
        //animating initialized views


        //add view to the screen
        activity.addContentView(lastMainLayout,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }

    private void init()
    {

        //getting the density of the screen to maintain the view sizes according the device screen size
        DisplayMetrics metrics = activity.getApplicationContext().getResources().getDisplayMetrics();
        int density = (int)metrics.density;



        //creating required views to create constraint programatically
        mainLinearLayout = new LinearLayout(activity.getApplicationContext());
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainLinearLayout.setLayoutParams(params);
        mainLinearLayout.setId(R.id.linearLayout);

        ImageView gif = new ImageView(activity.getApplicationContext());
        Glide.with(activity.getApplicationContext()).load(R.raw.roundgif).into(gif);
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(180 * density, 180 * density);
        gif.setLayoutParams(param2);

        mainLinearLayout.addView(gif);



        lastMainLayout = new LinearLayout(activity.getApplicationContext());
        LinearLayout.LayoutParams mainParams;
        mainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        lastMainLayout.setOrientation(LinearLayout.VERTICAL);
        lastMainLayout.setGravity(Gravity.CENTER);
        lastMainLayout.setLayoutParams(mainParams);
        lastMainLayout.setBackgroundColor(Color.TRANSPARENT);


        lastMainLayout.addView(mainLinearLayout);
    }

    public void dismiss()
    {

        if(mainLinearLayout != null)
        {
            mainLinearLayout.removeAllViews();
            mainLinearLayout = null;
        }
        if(lastMainLayout != null)
        {
            lastMainLayout.removeAllViews();
            lastMainLayout = null;
        }


    }



}
