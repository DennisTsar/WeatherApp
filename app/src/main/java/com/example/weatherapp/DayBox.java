package com.example.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DayBox extends LinearLayout {
    private TextView date;
    private TextView high;
    private TextView low;
    private ImageView icon;
    public DayBox(Context context, AttributeSet attrs){
        super(context,attrs);
        date = findViewById(R.id.b_date);
        high = findViewById(R.id.b_high);
        low = findViewById(R.id.b_low);
        icon  = findViewById(R.id.b_icon);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.day_box,this);
    }
    public void set(String d, String h, String l, Bitmap b){
        //i don't like how thius runs everytime method is called
        date = findViewById(R.id.b_date);
        high = findViewById(R.id.b_high);
        low = findViewById(R.id.b_low);
        icon = findViewById(R.id.b_icon);

        date.setText(d+"");
        high.setText(h);
        low.setText(l);
        icon.setImageBitmap(b);
    }
}
