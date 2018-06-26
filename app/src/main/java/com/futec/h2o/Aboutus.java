package com.futec.h2o;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;



public class Aboutus extends AppCompatActivity {
    boolean language=true; //true Arabic,false English
//Obadah 25/6/2018
    public static final String MyPREFERENCES="H2OPreference";
    SharedPreferences prefs;
    TextView aboutus_tv,aboutus_txt,tv_jordan,tv_ksa;
    int fontsize, fontsize_ar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus_layout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        aboutus_tv=(TextView)findViewById(R.id.aboutus_view);
        aboutus_txt=(TextView)findViewById(R.id.about_us_txt);
        tv_jordan=(TextView)findViewById(R.id.tv_jordan);
        tv_ksa=(TextView)findViewById(R.id.tv_ksa);

        Typeface font= Typeface.createFromAsset(getAssets(), "ArabiaWeather.ttf");
        aboutus_tv.setTypeface(font);
        aboutus_txt.setTypeface(font);
        tv_jordan.setTypeface(font);
        tv_ksa.setTypeface(font);

String x;
x="test";

        prefs=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        language=prefs.getBoolean("language", false);
        fontsize= prefs.getInt("font_size",18);
        fontsize_ar= prefs.getInt("font_size_ar",20);
        if(language)
        {
            aboutus_tv.setTextSize(fontsize_ar);
            aboutus_txt.setTextSize(fontsize_ar);
            tv_jordan.setTextSize(fontsize_ar);
            tv_ksa.setTextSize(fontsize_ar);

            aboutus_txt.setText(R.string.about_us_txt_ar);
            aboutus_tv.setText(R.string.about_us_ar);
            tv_jordan.setText(R.string.jordan_ar);
            tv_ksa.setText(R.string.ksa_ar);
        }
        else
        {
            aboutus_tv.setTextSize(fontsize);
            aboutus_txt.setTextSize(fontsize);
            tv_jordan.setTextSize(fontsize);
            tv_ksa.setTextSize(fontsize);

            aboutus_tv.setText("About Us");
            aboutus_txt.setText(R.string.about_us_txt);
            tv_jordan.setText("Jordan");
            tv_ksa.setText("KSA");
        }
    }


 


}
