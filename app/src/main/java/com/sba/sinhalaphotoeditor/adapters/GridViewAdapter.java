package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.Config.ExifUtil;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.MyCustomGallery;
import com.sba.sinhalaphotoeditor.model.GalleryImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sba.sinhalaphotoeditor.MostUsedMethods.Methods.getBitmap;
import static com.sba.sinhalaphotoeditor.activities.MyCustomGallery.selectedBitmap;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<GalleryImage> images;

    public GridViewAdapter(Context context, ArrayList<GalleryImage> images)
    {
        this.context = context;
        this.images = images;
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
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewAdapter.MyViewHolder holder, final int position)
    {


        if(images.size() > position * 2)
        {
            Glide.with(context).load(images.get(position * 2).getFile()).into(holder.imgView);
        }
        if(images.size() > position + 1)
        {
            Glide.with(context).load(images.get(position + 1).getFile()).into(holder.imgView2);
        }


        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(getBitmap(images.get(position * 2).getFile().toString()) != null)
                {
                    selectedBitmap = getBitmap(images.get(position * 2).getFile().toString());
                    selectedBitmap = ExifUtil.rotateBitmap(images.get(position * 2).getFile().toString(), selectedBitmap);

                    showSelectView(images.get(position * 2).getFile());

                }

            }
        });

        holder.imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(getBitmap(images.get(position + 1).getFile().toString()) != null)
                {

                    selectedBitmap = getBitmap(images.get(position + 1).getFile().toString());
                    selectedBitmap = ExifUtil.rotateBitmap(images.get(position + 1).getFile().toString(), selectedBitmap);

                    showSelectView(images.get(position + 1).getFile());
                }


            }
        });


    }

    @Override
    public int getItemCount()
    {
        if(images.size() % 2 == 0)
        {
            return images.size() / 2;
        }
        else
        {
            return (images.size() / 2) + 1;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imgView;
        private ImageView imgView2;
        private ImageView is_selected_image1;
        private ImageView is_selected_image2;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgView = itemView.findViewById(R.id.image);
            imgView2 = itemView.findViewById(R.id.image2);
        }
    }
    private void showSelectView(File file)
    {
       if(context instanceof MyCustomGallery)
       {
           ((MyCustomGallery)context).showSelectView(file);
       }
    }
}
