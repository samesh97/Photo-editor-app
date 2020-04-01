package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.CallBacks.OnBitmapChanged;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.singleton.ImageList;
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
    private Bitmap currentBitmap = null;

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
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sample_filter_pic);

            Bitmap bitmap = icon.copy(icon.getConfig(),true);
            Glide.with(context).load(filters.get(position).processFilter(bitmap)).into(holder.previewImage);
            String name= filters.get(position).getName();
            if(name != null)
            {
                holder.filterName.setText(name);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(previouPosition != position)
                    {
                        currentBitmap = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(),true);
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
        TextView filterName;

        public FilterViewHolder(@NonNull View itemView)
        {
            super(itemView);
            previewImage = itemView.findViewById(R.id.filter_preview);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
    public int returnPrevoiusPositin()
    {
        return previouPosition;
    }
}
