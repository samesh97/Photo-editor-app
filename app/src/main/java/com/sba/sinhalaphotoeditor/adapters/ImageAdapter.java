package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.callbacks.OnAlbumClick;
import com.sba.sinhalaphotoeditor.model.AlbumModel;
import com.sba.sinhalaphotoeditor.model.pictureFacer;
import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>
{

    private Context context;
    private ArrayList<pictureFacer> list;
    private OnAlbumClick onAlbumClick;

    public ImageAdapter(Context context, ArrayList<pictureFacer> list, OnAlbumClick onAlbumClick)
    {
        this.context = context;
        this.list = list;
        this.onAlbumClick = onAlbumClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.image_row,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {
        if(list != null && list.size() > position)
        {
            final pictureFacer model = list.get(position);
            Glide.with(context)
                    .load(model.getPicturePath())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.image_pic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(onAlbumClick != null)
                    {
                        onAlbumClick.onAlbumChanged(model.getPicturePath(),position);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount()
    {
        if(list != null)
        {
            return list.size();
        }
        return 0;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image_pic;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image_pic = itemView.findViewById(R.id.image_pic);
            int width = (int) Methods.getDeviceWidthInPX(context);
            width = width / 4;

            int padding = 12;
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width - padding,width - padding);
            image_pic.setLayoutParams(params);

            ConstraintLayout layout = itemView.findViewById(R.id.layout);
            ConstraintSet set = new ConstraintSet();
            set.clone(layout);
            set.connect(image_pic.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, Methods.convertDpToPixel(padding,context));
            set.connect(image_pic.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,Methods.convertDpToPixel(padding,context));
            set.connect(image_pic.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,Methods.convertDpToPixel(padding / 4.0f,context));
            set.connect(image_pic.getId(),ConstraintSet.BOTTOM,layout.getId(),ConstraintSet.BOTTOM,Methods.convertDpToPixel(padding / 4.0f,context));
            set.applyTo(layout);

        }
    }
}
