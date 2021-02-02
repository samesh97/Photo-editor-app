package com.sba.sinhalaphotoeditor.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sba.sinhalaphotoeditor.fragments.StickerFragment;
import com.sba.sinhalaphotoeditor.model.FirebaseStickerCategory;

import java.util.ArrayList;

public class StickerViewPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<FirebaseStickerCategory> categoryList;

    public StickerViewPagerAdapter(@NonNull FragmentManager fm,ArrayList<FirebaseStickerCategory> categoryList)
    {
        super(fm);
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        if(categoryList != null && categoryList.size() > position)
        {
            return new StickerFragment(categoryList.get(position));
        }

        return null;

    }

    @Override
    public int getCount()
    {
        if(categoryList != null)
            return categoryList.size();
        else
            return 0;
    }
}
