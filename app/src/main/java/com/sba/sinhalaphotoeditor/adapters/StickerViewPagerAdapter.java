package com.sba.sinhalaphotoeditor.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sba.sinhalaphotoeditor.fragments.StickerFragment;

public class StickerViewPagerAdapter extends FragmentStatePagerAdapter
{
    private int size;

    public StickerViewPagerAdapter(@NonNull FragmentManager fm,int size)
    {
        super(fm);
        this.size = size;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        return new StickerFragment();
    }

    @Override
    public int getCount()
    {
        return size;
    }
}
