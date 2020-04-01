package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sba.sinhalaphotoeditor.CallBacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShadowColorAdapter extends RecyclerView.Adapter<ShadowColorAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<Integer> colorList;
    private OnTextAttributesChangedListner listner;

    public ShadowColorAdapter(Context context, OnTextAttributesChangedListner listner)
    {
        this.context = context;
        this.listner = listner;
        setColorsList();
    }
    @NonNull
    @Override
    public ShadowColorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.text_color_row,null,false);
        return new ShadowColorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShadowColorAdapter.MyViewHolder holder, final int position)
    {
        if(colorList != null && colorList.size() > position)
        {
            holder.color_lay.setClipToOutline(true);
            holder.color_lay.setBackgroundColor(colorList.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listner.onTextShadowColorChanged(colorList.get(position));
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
        ImageView color_lay;
        private MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            color_lay = itemView.findViewById(R.id.color_lay);
        }
    }
    private void setColorsList()
    {
        if(colorList == null)
        {
            colorList = new ArrayList<>();
        }

        colorList.add(Color.BLACK);
        colorList.add(Color.WHITE);
        colorList.add(Color.GRAY);
        colorList.add(Color.LTGRAY);
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        colorList.add(Color.DKGRAY);
        colorList.add(Color.BLUE);
        colorList.add(Color.YELLOW);
        colorList.add(Color.CYAN);
        colorList.add(Color.MAGENTA);

    }
}
