package com.futec.h2o;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.andexert.library.RippleView;

public class SettingsActivity extends AppCompatActivity {
    public static final String MyPREFERENCES="H2OPreference";
    SharedPreferences sharedPreferences;
    Typeface font;
    NumberPicker np_font_size, np_col_tables,np_col_items, np_font_size_ar, np_itemicon_height;
    int fontsize,fontsize_ar, no_col_items, no_col_tables,  item_icon_height;
    boolean style;
    RippleView rippleView_save;
    EditText et_port, et_ip;
    String ip, port,Msg_wantosave="Do you want to save changes?", Msg_yes="Yes",Msg_no="No";
    Switch styleSwitch;
    Context context= this;

    Button btnsave;
    boolean lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        lang= sharedPreferences.getBoolean("language",false);
        if (lang)
        {   setContentView(R.layout.activity_settings_layout_ar);
            Msg_wantosave= getResources().getString(R.string.save_changes_ar);
            Msg_no= getResources().getString(R.string.no_ar);
            Msg_yes= getResources().getString(R.string.yes_ar);
            Msg_yes= "نعم-لا";
        }
        else setContentView(R.layout.activity_settings_layout);
        //find view by id
        font= Typeface.createFromAsset(getAssets(), "ArabiaWeather.ttf");
        np_font_size=(NumberPicker) findViewById(R.id.np_font_size);
        np_col_items=(NumberPicker)findViewById(R.id.column_i_np);
        np_col_tables=(NumberPicker) findViewById(R.id.column_t_np);
        np_font_size_ar=(NumberPicker) findViewById(R.id.np_ar_font_size);
        np_itemicon_height=(NumberPicker) findViewById(R.id.np_item_icon_height);


        btnsave = findViewById(R.id.btn_save);


        styleSwitch= (Switch) findViewById(R.id.switch_grid_or_menu);

        rippleView_save= (RippleView) findViewById(R.id.rippleview_save);
        et_ip=(EditText) findViewById(R.id.et_hostip);
        et_port =(EditText) findViewById(R.id.et_port);

        //get info from saved share prefs
        ip= sharedPreferences.getString("ip", "");
        port= sharedPreferences.getString("port", "");

        //get saved settings
        no_col_items= sharedPreferences.getInt("no_col_items", 4);
        no_col_tables=sharedPreferences.getInt("no_col_tables",5);
        fontsize= sharedPreferences.getInt("font_size", 18);
        fontsize_ar= sharedPreferences.getInt("font_size_ar",20);
        item_icon_height= sharedPreferences.getInt("item_icon_height",8);
        style= sharedPreferences.getBoolean("style_type", false); // drawer is default

        //name_colwidth=sharedPreferences.getInt("name_colwidth", 12);
        //price_colwidth=sharedPreferences.getInt("price_colwidth",5);
        //qyt_colwidth=sharedPreferences.getInt("qyt_colwidth",4);
        //total_colwidth=sharedPreferences.getInt("total_colwidth",6);
        //set info to controls
        try{
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            et_ip.setText(ip);
            et_port.setText(port);

            et_ip.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            et_port.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

            et_ip.addTextChangedListener(txtwatcherIP);
            et_port.addTextChangedListener(txtwatcherPort);

            styleSwitch.setChecked(style);

            np_font_size_ar.setMinValue(10);
            np_font_size_ar.setMaxValue(70);
            np_font_size_ar.setValue(fontsize_ar);
            np_font_size_ar.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            np_font_size.setMinValue(10);
            np_font_size.setMaxValue(70);
            np_font_size.setValue(fontsize);
            np_font_size.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            np_col_tables.setMinValue(1);
            np_col_tables.setMaxValue(8);
            np_col_tables.setValue(no_col_tables);
            np_col_tables.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            np_col_items.setMinValue(1);
            np_col_items.setMaxValue(8);
            np_col_items.setValue(no_col_items);
            np_col_items.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            np_itemicon_height.setMinValue(1);
            np_itemicon_height.setMaxValue(30);
            np_itemicon_height.setValue(item_icon_height);
            np_itemicon_height.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


            btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ip = et_ip.getText().toString();
                    port = et_port.getText().toString();
                    fontsize=np_font_size.getValue();
                    fontsize_ar= np_font_size_ar.getValue();

                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString("ip",ip);
                    editor.putString("port", port);
                    editor.putInt("font_size", fontsize);
                    editor.putInt("font_size_ar",fontsize_ar);
                    editor.putInt("no_col_items",np_col_items.getValue());
                    editor.putInt("no_col_tables",np_col_tables.getValue());
                    editor.putInt("item_icon_height",np_itemicon_height.getValue());
                    editor.putBoolean("style_type", styleSwitch.isChecked());
                    editor.apply();
                    finish();
                }
            });
        rippleView_save.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                ip = et_ip.getText().toString();
                port = et_port.getText().toString();
                fontsize=np_font_size.getValue();
                fontsize_ar= np_font_size_ar.getValue();

                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("ip",ip);
                editor.putString("port", port);
                editor.putInt("font_size", fontsize);
                editor.putInt("font_size_ar",fontsize_ar);
                editor.putInt("no_col_items",np_col_items.getValue());
                editor.putInt("no_col_tables",np_col_tables.getValue());
                editor.putInt("item_icon_height",np_itemicon_height.getValue());
                editor.putBoolean("style_type", styleSwitch.isChecked());
                editor.apply();
                finish();
            }

        });
        }catch (Exception e){}

    }


   public TextWatcher txtwatcherIP =  new TextWatcher() {
        // remove spaces in ip editText
        @Override
        public void afterTextChanged(Editable s) {
            String strIP = s.toString().replaceAll(" ", "");
            if (!s.toString().equals(strIP)) {
                et_ip.setText(strIP);
                et_ip.setSelection(strIP.length());
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
        int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
        int before, int count) {

        }
    };

    public TextWatcher txtwatcherPort =  new TextWatcher() {
        // remove spaces in ip editText
        @Override
        public void afterTextChanged(Editable s) {
            String strIP = s.toString().replaceAll(" ", "");
            if (!s.toString().equals(strIP)) {
                et_port.setText(strIP);
                et_port.setSelection(strIP.length());

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {

        }
    };

    @Override
    public void onBackPressed() {
        showDialogSave();
    }

    private void showDialogSave(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder
                .setMessage(Msg_wantosave)
                .setCancelable(false)
                .setPositiveButton(Msg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ip = et_ip.getText().toString();
                        port = et_port.getText().toString();
                        fontsize = np_font_size.getValue();
                        fontsize_ar = np_font_size_ar.getValue();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ip", ip);
                        editor.putString("port", port);
                        editor.putInt("font_size", fontsize);
                        editor.putInt("font_size_ar", fontsize_ar);
                        editor.putInt("no_col_items", np_col_items.getValue());
                        editor.putInt("no_col_tables", np_col_tables.getValue());
                        editor.putInt("item_icon_height", np_itemicon_height.getValue());
                        editor.putBoolean("style_type", styleSwitch.isChecked());
                        editor.apply();
                        finish();

                    }
                }).setNegativeButton(Msg_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
