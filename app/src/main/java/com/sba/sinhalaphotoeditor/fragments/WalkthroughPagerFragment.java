package com.sba.sinhalaphotoeditor.fragments;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.R;

import static android.content.Context.MODE_PRIVATE;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_ENGLISH;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.config.Constants.SHARED_PREF_NAME;


public class WalkthroughPagerFragment extends Fragment
{

    private Drawable image1,image2,image3;
    private String title,description;

    public WalkthroughPagerFragment(Drawable image1, Drawable image2, Drawable image3, String title, String description)
    {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.title = title;
        this.description = description;
    }
    public WalkthroughPagerFragment()
    {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ImageView imageView1 = view.findViewById(R.id.image1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView descTextView = view.findViewById(R.id.description);




        if(image1 != null)
        {
            Glide.with(getActivity()).load(image1).into(imageView1);
        }
        else {

            imageView1.setVisibility(View.GONE);
        }
        if(image2 != null)
        {
            Glide.with(getActivity()).load(image2).into(imageView2);
        }
        else
        {
            imageView2.setVisibility(View.GONE);
        }
        if(image3 != null)
        {
            Glide.with(getActivity()).load(image3).into(imageView3);
        }
        else
        {
            imageView3.setVisibility(View.GONE);
        }
        titleTextView.setText(title);
        descTextView.setText(description);



        changeTypeFace(view);


        return view;
    }
    public void changeTypeFace(View view)
    {
        if(getActivity() != null)
        {
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String language = pref.getString(LANGUAGE_KEY,LANGUAGE_SINHALA);
            Typeface typeface = null;

            if(language.equals(LANGUAGE_ENGLISH))
            {
                //english
                typeface = ResourcesCompat.getFont(getActivity().getApplicationContext(),R.font.englishfont);
            }
            else
            {
                //sinhala
                typeface = ResourcesCompat.getFont(getActivity().getApplicationContext(),R.font.bindumathi);
            }


            TextView titleTextView = view.findViewById(R.id.title);
            TextView descTextView = view.findViewById(R.id.description);
            titleTextView.setTypeface(typeface);
            descTextView.setTypeface(typeface);

        }

    }

}
