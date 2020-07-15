package com.sba.sinhalaphotoeditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.callbacks.OnBitmapChanged;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.sdk.Methods;
import com.sba.sinhalaphotoeditor.singleton.ImageList;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>
{
    private Context context;
    private List<Filter> filters;
    private int previousPosition = -99;
    private OnBitmapChanged listner;
    private Bitmap currentBitmap = null;

    private HashMap<Integer,CircleImageView> circleImageList;
    private Bitmap croppedBitmap = null;
    private ProgressBar progressBar;

    public FilterAdapter(Context context, List<Filter> filters, OnBitmapChanged listner,ProgressBar progressBar)
    {
        this.context = context;
        this.filters = filters;
        this.listner = listner;
        circleImageList = new HashMap<>();
        this.progressBar = progressBar;

    }
    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_row,null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterViewHolder holder, final int position)
    {
        if(filters != null)
        {


            AsyncTask.execute(new Runnable() {
                @Override
                public void run()
                {

                    if(croppedBitmap == null)
                    {
                        croppedBitmap = Methods.resize(ImageList.getInstance().getCurrentBitmap(),100,100);
                    }

                    Bitmap bitmap = croppedBitmap.copy(croppedBitmap.getConfig(),true);
                    final Bitmap filtered = filters.get(position).processFilter(bitmap);

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Glide.with(context).load(filtered).into(holder.previewImage);
                        }
                    });

                }
            });


            String name= filters.get(position).getName();
            if(name != null)
            {
                holder.filterName.setText(name);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(previousPosition != position)
                    {
                        if(progressBar != null)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run()
                            {
                                currentBitmap = ImageList.getInstance().getCurrentBitmap().copy(ImageList.getInstance().getCurrentBitmap().getConfig(),true);
                                Bitmap largeBitmap = filters.get(position).processFilter(currentBitmap);
                                listner.bitmapChanged(largeBitmap,filters.get(position));
                            }
                        });

                        previousPosition = position;


                        Set<Integer> keys = circleImageList.keySet();
                        for(Integer key : keys)
                        {
                            if(position == key)
                            {
                                circleImageList.get(key).setAlpha(1f);
                                circleImageList.get(key).setBorderWidth(4);
                                circleImageList.get(key).setBorderColor(Color.WHITE);

                                circleImageList.get(key).animate().scaleX(1.08f).scaleY(1.08f).start();
                            }
                            else
                            {
                                circleImageList.get(key).animate().scaleX(1f).scaleY(1f).start();
                                circleImageList.get(key).setAlpha(0.7f);
                                circleImageList.get(key).setBorderWidth(2);
                                circleImageList.get(key).setBorderColor(Color.WHITE);
                            }
                        }


                    }


                }
            });
        }

        circleImageList.put(position,holder.previewImage);


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
        return previousPosition;
    }
}
