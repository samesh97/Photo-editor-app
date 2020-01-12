package com.sba.sinhalaphotoeditor.walkthrough;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment
{

    private Drawable image1,image2,image3,topImage;
    private String title,description;

    public ViewPagerFragment(Drawable image1,Drawable image2,Drawable image3,String title,String description,Drawable topImage)
    {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.title = title;
        this.description = description;
        this.topImage = topImage;
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

        ImageView topGreenPannel = view.findViewById(R.id.topGreenPannel);




        if(image1 != null)
        {
           // imageView1.setImageDrawable(image1);
            Glide.with(getActivity()).load(image1).into(imageView1);
        }
        else {

            imageView1.setVisibility(View.GONE);
        }
        if(image2 != null)
        {
            //imageView2.setImageDrawable(image2);
            Glide.with(getActivity()).load(image2).into(imageView2);
        }
        else
        {
            imageView2.setVisibility(View.GONE);
        }
        if(image3 != null)
        {
            //imageView3.setImageDrawable(image3);
            Glide.with(getActivity()).load(image3).into(imageView3);
        }
        else
        {
            imageView3.setVisibility(View.GONE);
        }
        titleTextView.setText(title);
        descTextView.setText(description);


        if(topImage != null)
        {
            //topGreenPannel.setBackground(topImage);
            Glide.with(getActivity()).load(topImage).into(topGreenPannel);
        }

        setTextViewFontAndSize(view);


        return view;
    }
    private void setTextViewFontAndSize(View view)
    {

        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);



        Typeface typeface;

        SharedPreferences pref = getActivity().getSharedPreferences("com.sba.sinhalaphotoeditor", 0);
        int pos = pref.getInt("LanguagePosition",-99);
        if(pos != 99) {
            switch (pos) {
                case 1:


                    typeface = ResourcesCompat.getFont(getActivity(), R.font.gemunulibresemibold);
                    title.setTypeface(typeface);
                    description.setTypeface(typeface);


                    break;
                case 2:

                    typeface = ResourcesCompat.getFont(getActivity(), R.font.englishfont);
                    title.setTypeface(typeface);


                    description.setTypeface(typeface);






                    break;
                case 3:


                    typeface = ResourcesCompat.getFont(getActivity(), R.font.tamilfont);


                    title.setTypeface(typeface);
                    title.setTextSize(20);

                    description.setTypeface(typeface);




                    break;
            }
        }
    }

}
