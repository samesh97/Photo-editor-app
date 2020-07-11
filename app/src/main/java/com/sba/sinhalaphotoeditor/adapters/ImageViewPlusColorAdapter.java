package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.callbacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.custom.views.ImageViewPlus;
import com.sba.sinhalaphotoeditor.model.TextViewPlus;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageViewPlusColorAdapter extends RecyclerView.Adapter<ImageViewPlusColorAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<Integer> colorList;
    private OnTextAttributesChangedListner listner;
    private HashMap<Integer, ImageView> viewList;
    private ImageViewPlus selectedImageView;

    public ImageViewPlusColorAdapter(Context context, ImageViewPlus selectedImageView, OnTextAttributesChangedListner listner)
    {
        this.context = context;
        this.listner = listner;
        this.selectedImageView = selectedImageView;
        colorList = new ArrayList<>();
        viewList = new HashMap<Integer, ImageView>();
        setColorsList();
    }

    @NonNull
    @Override
    public ImageViewPlusColorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.text_color_row,null,false);
        return new ImageViewPlusColorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewPlusColorAdapter.MyViewHolder holder, final int position)
    {
        if(colorList != null && colorList.size() > position)
        {
            viewList.put(position,holder.is_selected);
            holder.color_lay.setClipToOutline(true);
            ColorDrawable colorDrawable = new ColorDrawable(colorList.get(position));
            holder.color_lay.setImageDrawable(colorDrawable);
            holder.color_lay.setImageDrawable(colorDrawable);

            if(selectedImageView != null)
            {
                if(selectedImageView.getBorderColor() == colorList.get(position))
                {
                    ImageView view  = viewList.get(position);
                    if(view != null)
                    {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listner.onTextColorChanged(colorList.get(position));

                    ArrayList<Integer> keyList = new ArrayList<>(viewList.keySet());
                    for(int i = 0; i < keyList.size(); i++)
                    {
                        if(position == keyList.get(i))
                        {
                            if (viewList.get(keyList.get(i)) != null)
                            {
                                ImageView view = viewList.get(keyList.get(i));
                                if (view != null)
                                {
                                    view.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                        else
                        {
                            ImageView view = viewList.get(keyList.get(i));
                            if(view != null)
                            {
                                view.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount()
    {
        return colorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView color_lay;
        ImageView is_selected;
        private MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            color_lay = itemView.findViewById(R.id.color_lay);
            is_selected = itemView.findViewById(R.id.is_selected);
        }
    }
    private void setColorsList()
    {

        colorList.add(Color.BLACK);
        colorList.add(Color.WHITE);
        colorList.add(Color.GRAY);
        colorList.add(Color.LTGRAY);
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        colorList.add(Color.BLUE);
        colorList.add(Color.DKGRAY);
        colorList.add(Color.YELLOW);
        colorList.add(Color.CYAN);
        colorList.add(Color.MAGENTA);

        if(selectedImageView != null)
        {

            for(int i = 0; i < colorList.size(); i++)
            {
                if(colorList.get(i) == selectedImageView.getBorderColor())
                {
                    colorList.remove(i);
                    colorList.add(0,selectedImageView.getBorderColor());
                    notifyDataSetChanged();
                    break;
                }
            }
        }

    }
}
