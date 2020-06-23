package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.callbacks.OnItemClickListner;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.MyCustomGallery;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.util.ArrayList;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<GalleryImage> images;
    public static final int numberOfImagesInARow = 3;
    private OnItemClickListner listner;

    public GridViewAdapter(Context context, ArrayList<GalleryImage> images, OnItemClickListner listner)
    {
        this.context = context;
        this.images = images;
        this.listner = listner;
    }
    public void setContext(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public GridViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View view = LayoutInflater.from(context).inflate(R.layout.custom_gallery_row, parent, false);
       view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Methods.getDeviceHeightInPX(context) / 5)));
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewAdapter.MyViewHolder holder, final int position)
    {


        if(images.size() > (position * numberOfImagesInARow) && images.get(position * numberOfImagesInARow).getFile() != null)
        {

                Glide.with(context)
                    .load(images.get(position * numberOfImagesInARow).getFile())
                    .placeholder(R.drawable.loading_image_placeholder)
                    .error(R.drawable.loading_image_placeholder)
                    .into(holder.imgView);

            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(images.get(position * numberOfImagesInARow).getFile());
                }
            });
        }
        else
        {
            holder.imgView.setImageBitmap(null);
            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(null);
                }
            });
        }
        if(images.size() > ((position * numberOfImagesInARow) + 1) && images.get(((position * numberOfImagesInARow) + 1)).getFile() != null)
        {
            Glide.with(context)
                    .load(images.get(((position * numberOfImagesInARow) + 1)).getFile())
                    .placeholder(R.drawable.loading_image_placeholder)
                    .error(R.drawable.loading_image_placeholder)
                    .into(holder.imgView2);

            holder.imgView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(images.get(((position * numberOfImagesInARow) + 1)).getFile());
                }
            });
        }
        else
        {
            holder.imgView2.setImageBitmap(null);
            holder.imgView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(null);
                }
            });
        }
        if(images.size() > ((position * numberOfImagesInARow) + 2) && images.get(((position * numberOfImagesInARow) + 2)).getFile() != null)
        {
            Glide.with(context)
                    .load(images.get(((position * numberOfImagesInARow) + 2)).getFile())
                    .placeholder(R.drawable.loading_image_placeholder)
                    .error(R.drawable.loading_image_placeholder)
                    .into(holder.imgView3);

            holder.imgView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(images.get(((position * numberOfImagesInARow) + 2)).getFile());
                }
            });
        }
        else
        {
            holder.imgView3.setImageBitmap(null);
            holder.imgView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSelectView(null);
                }
            });
        }

    }

    @Override
    public int getItemCount()
    {

        if(images.size() % numberOfImagesInARow == 0)
        {
            return (images.size() / numberOfImagesInARow);
        }
        else
        {
            return (images.size() / numberOfImagesInARow) + 1;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imgView;
        private ImageView imgView2;
        private ImageView imgView3;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgView = itemView.findViewById(R.id.image);
            imgView2 = itemView.findViewById(R.id.image2);
            imgView3 = itemView.findViewById(R.id.image3);
        }
    }
    private void showSelectView(File file)
    {
        if(file == null)
        {
            return;
        }
        listner.onClicked(file);
        MyCustomGallery.selectedBitmap = Methods.getBitmap(file.getAbsolutePath());
    }
}
