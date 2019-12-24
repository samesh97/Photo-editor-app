package com.sba.sinhalaphotoeditor.walkthrough;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{

    ArrayList<Drawable> image1 = new ArrayList<android.graphics.drawable.Drawable>();
    ArrayList<Drawable> image2 = new ArrayList<Drawable>();
    ArrayList<Drawable> image3 = new ArrayList<Drawable>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();
    ArrayList<Drawable> topImages = new ArrayList<Drawable>();


    public ViewPagerAdapter(@NonNull FragmentManager fm,ArrayList<Drawable> image1,ArrayList<Drawable> image2,ArrayList<Drawable> image3,ArrayList<String> titles,ArrayList<String> description,ArrayList<Drawable> topImages)
    {
        super(fm);
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.titles = titles;
        this.description = description;
        this.topImages = topImages;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {

        Drawable im1;
        Drawable im2;
        Drawable im3;

        if((image1.size() - 1) >= position)
        {
            im1 = image1.get(position);
        }
        else
        {
            im1 = null;
        }



        if((image2.size() - 1) >= position)
        {
            im2 = image2.get(position);
        }
        else
        {
            im2 = null;
        }

        if((image3.size() - 1) >= position)
        {
            im3 = image3.get(position);
        }
        else
        {
            im3 = null;
        }

        ViewPagerFragment fragment = new ViewPagerFragment(im1,im2,im3,titles.get(position),description.get(position),topImages.get(position));
        return fragment;
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }
}
