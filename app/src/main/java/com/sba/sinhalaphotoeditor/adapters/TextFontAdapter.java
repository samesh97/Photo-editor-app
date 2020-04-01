package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sba.sinhalaphotoeditor.CallBacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TextFontAdapter extends RecyclerView.Adapter<TextFontAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<Typeface> fonts = new ArrayList<>();
    private String text;
    private OnTextAttributesChangedListner listner;

    public TextFontAdapter(Context context, String text, OnTextAttributesChangedListner listner)
    {
        this.context = context;
        this.text = text;
        this.listner = listner;
        setFonts();
    }
    @NonNull
    @Override
    public TextFontAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.fontrow,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextFontAdapter.MyViewHolder holder, final int position)
    {
        if(fonts != null && fonts.size() > position)
        {
            holder.fontText.setText(text);
            holder.fontText.setTypeface(fonts.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listner.onTextFontChanged(fonts.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return fonts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fontText;
        private MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            fontText = itemView.findViewById(R.id.fontText);
        }
    }
    private void setFonts()
    {
        Typeface typeface;
        typeface = ResourcesCompat.getFont(context, R.font.iskpota);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.kaputaunicode);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.dinaminauniweb);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.sarasaviunicode);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.fmmalithiuw46);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.hodipotha3);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.puskolapotha2010);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.nyh);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.warna);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.winnie);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.winnie1);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.sulakna);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.gemunulibrebold);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.gemunulibrextrabold);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.gemunulibresemibold);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.postnobillscolombobold);
        fonts.add(typeface);

    }
}
