package com.sba.sinhalaphotoeditor.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

public class TextViewPlus
{
    private Context context;
    private TextView textView;
    private int id;
    private int textColor;
    private int shadowColor;
    private String text;
    private int textSize = 18;
    private Typeface typeface;
    private float shadowYDirection;
    private float shadowXDirection;
    private float shadowRadius;
    private float textOpacity;

    public TextViewPlus(Context context,int id,String text,Typeface typeface)
    {
        this.context = context;
        textView = new TextView(context);
        this.text = text;
        this.typeface = typeface;
        this.id = id;
        shadowYDirection = 50;
        shadowXDirection = 50;
        shadowRadius = 2.5f;
        textOpacity = 100;
        textColor = Color.WHITE;
        shadowColor = Color.BLACK;
        setTextViewAttributes();
    }
    public TextViewPlus(Context context)
    {
        this.context = context;
        textView = new TextView(context);
    }

    private void setTextViewAttributes()
    {
        textView.setId(id);
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTypeface(typeface);
        textView.setTextColor(textColor);
    }

    public TextView getTextView()
    {
        return textView;
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
        this.textView.setTextColor(textColor);
    }
    public int getShadowColor()
    {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor)
    {
        this.shadowColor = shadowColor;
        setupShadowLayer();
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
        this.textView.setText(text);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize)
    {
        this.textSize = textSize;
        this.textView.setTextSize(textSize);
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface)
    {
        this.typeface = typeface;
        this.textView.setTypeface(typeface);
    }

    public float getShadowYDirection()
    {
        return shadowYDirection;
    }

    public void setShadowYDirection(float shadowYDirection)
    {
        this.shadowYDirection = shadowYDirection;
        setupShadowLayer();
    }

    public float getShadowXDirection() {
        return shadowXDirection;
    }

    public void setShadowXDirection(float shadowXDirection)
    {
        this.shadowXDirection = shadowXDirection;
        setupShadowLayer();
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius)
    {
        this.shadowRadius = shadowRadius;
        textView.setShadowLayer(getShadowRadius(),getShadowXDirection(),getShadowYDirection(),getShadowColor());
    }

    public float getTextOpacity() {
        return textOpacity;
    }

    public void setTextOpacity(float textOpacity)
    {
        this.textOpacity = textOpacity;
        float opacityLevel = (getTextOpacity() / 100.0f);
        textView.setAlpha(opacityLevel);
    }
    private void setupShadowLayer()
    {
        float finalY =(getShadowXDirection() / 2) - 25;
        float finalX =(getShadowYDirection() / 2) - 25;
        textView.setShadowLayer(getShadowRadius(),finalX,finalY,getShadowColor());
    }

    public int getTextColor() {
        return textColor;
    }
}
