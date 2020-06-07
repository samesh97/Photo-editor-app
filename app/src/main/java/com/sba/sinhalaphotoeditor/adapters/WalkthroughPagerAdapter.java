package com.sba.sinhalaphotoeditor.adapters;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sba.sinhalaphotoeditor.fragments.WalkthroughPagerFragment;

import java.util.ArrayList;

public class WalkthroughPagerAdapter extends FragmentStatePagerAdapter
{

    private ArrayList<Drawable> image1 = new ArrayList<android.graphics.drawable.Drawable>();
    private ArrayList<Drawable> image2 = new ArrayList<Drawable>();
    private ArrayList<Drawable> image3 = new ArrayList<Drawable>();

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();
    private ArrayList<Drawable> topImages = new ArrayList<Drawable>();


    public WalkthroughPagerAdapter(@NonNull FragmentManager fm, ArrayList<Drawable> image1, ArrayList<Drawable> image2, ArrayList<Drawable> image3, ArrayList<String> titles, ArrayList<String> description, ArrayList<Drawable> topImages)
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

        WalkthroughPagerFragment fragment = new WalkthroughPagerFragment(im1,im2,im3,titles.get(position),description.get(position),topImages.get(position));
        return fragment;
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }
}
