package com.sba.sinhalaphotoeditor.walkthrough;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sba.sinhalaphotoeditor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment
{

    private Drawable image1,image2,image3;
    private String title,description;

    public ViewPagerFragment(Drawable image1,Drawable image2,Drawable image3,String title,String description)
    {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.title = title;
        this.description = description;
    }
    public ViewPagerFragment()
    {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ImageView imageView1 = view.findViewById(R.id.image1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView descTextView = view.findViewById(R.id.description);

        if(image1 != null)
        {
            imageView1.setImageDrawable(image1);
        }
        else {

            imageView1.setVisibility(View.GONE);
        }
        if(image2 != null)
        {
            imageView2.setImageDrawable(image2);
        }
        else
        {
            imageView2.setVisibility(View.GONE);
        }
        if(image3 != null)
        {
            imageView3.setImageDrawable(image3);
        }
        else
        {
            imageView3.setVisibility(View.GONE);
        }
        titleTextView.setText(title);
        descTextView.setText(description);


        return view;
    }

}
