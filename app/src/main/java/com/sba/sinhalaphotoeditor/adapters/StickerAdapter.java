package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.MyViewHolder>
{
    private ArrayList<String> imageList;
    private Context context;
    public StickerAdapter(Context context,ArrayList<String> imageList)
    {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public StickerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_row,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerAdapter.MyViewHolder holder, int position)
    {
        if(imageList != null && imageList.size() > position)
        {
            Glide.with(context).load(imageList.get(position)).into(holder.sticker);
        }
    }

    @Override
    public int getItemCount()
    {
        if(imageList != null)
            return imageList.size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView sticker;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            sticker = itemView.findViewById(R.id.sticker);
        }
    }
}
