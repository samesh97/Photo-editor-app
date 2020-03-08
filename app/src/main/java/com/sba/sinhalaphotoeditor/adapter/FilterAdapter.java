package com.sba.sinhalaphotoeditor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.CallBacks.OnBitmapChanged;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>
{
    private Context context;
    private List<Filter> filters;
    private int previouPosition = -99;
    private OnBitmapChanged listner;

    public FilterAdapter(Context context, List<Filter> filters, OnBitmapChanged listner)
    {
        this.context = context;
        this.filters = filters;
        this.listner = listner;
    }
    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_row,null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, final int position)
    {
        if(filters != null)
        {
            Methods methods = new Methods(context);
            final Bitmap currentBitmap = MainActivity.images.get(MainActivity.imagePosition).copy(MainActivity.images.get(MainActivity.imagePosition).getConfig(),true);


            final Bitmap smallBitmap = methods.getResizedBitmap(filters.get(position).processFilter(currentBitmap),100);
            Glide.with(context).load(smallBitmap).into(holder.previewImage);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(previouPosition != position)
                    {
                        Bitmap largeBitmap = filters.get(position).processFilter(currentBitmap);
                        listner.bitmapChanged(largeBitmap);
                        previouPosition = position;
                    }

                }
            });
        }


    }


    @Override
    public int getItemCount()
    {
        if(filters != null)
        {
            return filters.size();
        }
        return 0;

    }

    public class FilterViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView previewImage;

        public FilterViewHolder(@NonNull View itemView)
        {
            super(itemView);
            previewImage = itemView.findViewById(R.id.filter_preview);
        }
    }
    public int returnPrevoiusPositin()
    {
        return previouPosition;
    }
}
