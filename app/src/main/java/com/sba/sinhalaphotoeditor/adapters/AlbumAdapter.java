package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.callbacks.OnAlbumClick;
import com.sba.sinhalaphotoeditor.model.AlbumModel;
import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>
{

    private Context context;
    private ArrayList<AlbumModel> list;
    private OnAlbumClick onAlbumClick;

    public AlbumAdapter(Context context,ArrayList<AlbumModel> list,OnAlbumClick onAlbumClick)
    {
        this.context = context;
        this.list = list;
        this.onAlbumClick = onAlbumClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.album_row,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {
        if(list != null && list.size() > position)
        {
            final AlbumModel model = list.get(position);
            Glide.with(context)
                    .load(model.getFirstPic())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.album_pic);


            holder.text_view.setText(String.valueOf(model.getNumberOfPics()));
            holder.album_name.setText(model.getFolderName());
            holder.album_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(onAlbumClick != null)
                    {
                        onAlbumClick.onAlbumChanged(model.getPath(),position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        if (list != null)
        return list.size();

        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView album_pic,blacked;
        TextView text_view;
        TextView album_name;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            album_pic = itemView.findViewById(R.id.album_pic);
            text_view = itemView.findViewById(R.id.text_view);
            album_name = itemView.findViewById(R.id.album_name);
            blacked = itemView.findViewById(R.id.blacked);

            int width = (int) Methods.getDeviceWidthInPX(context);
            width = width / 5;

            int padding = 12;
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width - padding,width - padding);
            album_pic.setLayoutParams(params);
            blacked.setLayoutParams(params);

            ConstraintLayout layout = itemView.findViewById(R.id.layout);
            ConstraintSet set = new ConstraintSet();
            set.clone(layout);
            set.connect(album_pic.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, padding);
            set.connect(album_pic.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,padding);
            set.connect(album_pic.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,padding);
            set.connect(album_pic.getId(),ConstraintSet.BOTTOM,layout.getId(),ConstraintSet.BOTTOM,padding);

            set.connect(blacked.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, padding);
            set.connect(blacked.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,padding);
            set.connect(blacked.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,padding);
            set.connect(blacked.getId(),ConstraintSet.BOTTOM,layout.getId(),ConstraintSet.BOTTOM,padding);
            set.applyTo(layout);
        }
    }
}
