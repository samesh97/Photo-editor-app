package com.sba.sinhalaphotoeditor.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sba.sinhalaphotoeditor.callbacks.OnTextAttributesChangedListner;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.model.TextViewPlus;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TextFontAdapter extends RecyclerView.Adapter<TextFontAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList<Typeface> fonts = new ArrayList<>();
    private String text;
    private OnTextAttributesChangedListner listner;
    private HashMap<Integer, ImageView> viewList;
    private TextViewPlus selectedTextView;

    public TextFontAdapter(Context context, String text, TextViewPlus selectedTextView, OnTextAttributesChangedListner listner)
    {
        this.context = context;
        this.text = text;
        this.listner = listner;
        this.selectedTextView = selectedTextView;
        fonts = new ArrayList<>();
        viewList = new HashMap<Integer, ImageView>();
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
            viewList.put(position,holder.is_selected);
            holder.fontText.setText(text);
            holder.fontText.setTypeface(fonts.get(position));

            if(selectedTextView != null)
            {
                if(selectedTextView.getTypeface() == fonts.get(position))
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

                    listner.onTextFontChanged(fonts.get(position));

                    ArrayList<Integer> keyList = new ArrayList<>(viewList.keySet());
                    for(int i = 0; i < keyList.size(); i++)
                    {
                        if (position == keyList.get(i))
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
                            if (view != null)
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
        return fonts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fontText;
        ImageView is_selected;
        private MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            fontText = itemView.findViewById(R.id.fontText);
            is_selected = itemView.findViewById(R.id.is_selected);
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

        //new fonts
        typeface = ResourcesCompat.getFont(context, R.font.aa_sinhala);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.aa_sinhala_9);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_ajith);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_arunalu);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_bindu);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_hiru);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_kalana);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_kavithi);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_leterpress);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_manage);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_manahara);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_mindada);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_nisansala);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_panhida);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_priyantha);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_saduni);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_sewana);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_sewana_bold);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_sewwandi);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_shilpa);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_stripe);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_supun);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_supuni);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_swarna);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_tharu);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_thushantha);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ams_uthmax);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.anuradhapura);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.astro01);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.fm_chuti);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.fm_gangaani);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.fm_sandhyanee);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.fn_kalpa);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.kotu);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.okdbo);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.ranmadu);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.sara_baron);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.sara_ramanee);
        fonts.add(typeface);
        typeface = ResourcesCompat.getFont(context, R.font.somefont);
        fonts.add(typeface);

        //end of new fonts


        if(selectedTextView != null)
        {

            for(int i = 0; i < fonts.size(); i++)
            {
                if(fonts.get(i) == selectedTextView.getTypeface())
                {
                    fonts.remove(i);
                    fonts.add(0,selectedTextView.getTypeface());
                    notifyDataSetChanged();
                    break;
                }
            }
        }

    }
}
