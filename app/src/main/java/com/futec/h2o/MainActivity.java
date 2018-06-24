package com.futec.h2o;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.devspark.appmsg.AppMsg;
import com.futec.h2o.ar.FloorSelectorActivityAr;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    String password,waiterID,waiterName;
    Typeface font;
    Boolean language_bl=false; //default is english
    TextView welcome_tv;
    EditText et_password;
    GridLayout numberPad_grid;
    //port and ip
    String port,ip, MsgWrongPass="wrong password please try again",
            MsgWait="please wait",
            Msgcanconnect="unable to connect to server",
            MsgpleaseEnterip="please enter host and port address before attempting to login",
            MsgException="Error in retrieving data";

    // declare key buttons and string builder for building password
    StringBuilder stringBuilder;
    Button keys[]=new Button[11];
    Button btn_clear, btn_login;

    //button and string ids in array of int
    int buttonIDs[]={R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3,R.id.btn_4,R.id.btn_5
            ,R.id.btn_6,R.id.btn_7,R.id.btn_8,R.id.btn_9};
    int stringIDs[]={R.string.k_0, R.string.k_1, R.string.k_2, R.string.k_3, R.string.k_4, R.string.k_5,
            R.string.k_6, R.string.k_7, R.string.k_8, R.string.k_9};
    //string arrays for the key display text
    static final String keyString[]={"0","1","2","3","4","5","6","7","8","9"};
    String keyStringAr[]= new String[11];

    //declare Shared preference and its editor
    public static final String MyPREFERENCES="H2OPreference";
    SharedPreferences sharedPreferences;
    //declaring fabs
    FloatingActionButton fab_aboutUs,fab_lang, fab_settings;


    //declaring all string arrays
    String ItemIDs[],CategItemsIDs[],flCatIDs[],categIDs[],flavorID[],sectionIDs[];
    String allItemNamesAR[],flavorNamesAr[],floorNamesAR[],categNamesAR[];
    String categNamesEN[],flavorNamesEn[],allItemNamesEN[],floorNamesEN[];
    double itemPrice[];
    int fontsize, fontsize_ar;
    android.app.AlertDialog progress_dialog;
    Context context= this;
    Animation slide_down, slide_up;
    String  Msg_yes="Enter",Msg_no="Back",Msg_enterSpass="Enter Settings Password",settingspassword="1998";

    //gcm prefs

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        //find view by id
        welcome_tv=(TextView)findViewById(R.id.welcome_tv);
        fab_aboutUs = (FloatingActionButton) findViewById(R.id.fab_info);
        fab_lang = (FloatingActionButton) findViewById(R.id.fab_lang);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        et_password=(EditText) findViewById(R.id.et_password);
        numberPad_grid=(GridLayout) findViewById(R.id.grid_numberPad);

        btn_login=(Button) findViewById(R.id.btn_save);
        btn_clear=(Button) findViewById(R.id.btn_clear);

//-----------------------
        //push notifications not used in the current version
        // related to these services which are not used as well MyGcmListenerService, MyInstanceIDListenerService, RegistrationIntentService
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(RegistrationIntentService.QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();}
//            }
//        };


//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
//------------------------------
        try {

            //load sliding animations which will be used later in displaying sliding messages
            slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down );

            slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            // get font from file
            font = Typeface.createFromAsset(getAssets(), "ArabiaWeather.ttf");
            //set font to tvs
            welcome_tv.setTypeface(font);
            et_password.setTypeface(font);
            btn_login.setTypeface(font);
            //remove full screen key board on password edit text
            et_password.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //enable text selection for edit text
            et_password.setRawInputType(InputType.TYPE_CLASS_TEXT);
            et_password.setTextIsSelectable(true);

            // get info from saved shared preference
            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            language_bl = sharedPreferences.getBoolean("language", false);
            ip = sharedPreferences.getString("ip", "");
            port = sharedPreferences.getString("port", "");
            fontsize=sharedPreferences.getInt("font_size", 18);
            fontsize_ar=sharedPreferences.getInt("font_size_ar",20);
            //call constructor of string builder
            stringBuilder = new StringBuilder();
            //initialize number keybuttons
            initializebuttons();
            //shadow for textviews
            welcome_tv.setShadowLayer(1, 0, 0, Color.rgb(44, 44, 44));

            if (language_bl)
                initializeViewsAr();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ip.isEmpty() && !port.isEmpty()) {
                    btn_login.setEnabled(false);
                new httpGet().execute();
                }
                else {
                    AppMsg.makeText(MainActivity.this,MsgpleaseEnterip , AppMsg.STYLE_CONFIRM, (float)fontsize)
                            .setAnimation(slide_down, slide_up).show();
                }
            }
        });

        fab_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (language_bl) { //change from arabic to english
                    language_bl = false;
                    initializeViewsEn();
                } else {
                    //change from english to arabic
                    language_bl = true;
                    initializeViewsAr();
                }
                editor.putBoolean("language", language_bl);
                editor.apply();
            }
        });

        fab_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this,Aboutus.class);
                startActivity(intent);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringBuilder.delete(0, stringBuilder.length());
                et_password.setText(stringBuilder.toString());
            }
        });

        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPasswordDialog();
            }
        });

        }catch (Exception e)
        {
            String x="";
        }
    }

//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        ip = sharedPreferences.getString("ip", "");
        port = sharedPreferences.getString("port", "");
        stringBuilder.delete(0, stringBuilder.length());
        et_password.setText(stringBuilder.toString());
        //related to push notifications which is not used in the current time
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        //related to push notifications which is not used in the current time
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void initializebuttons() {
        int len = buttonIDs.length;
        for(int i=0;i<len;i++)
        {   keys[i]= (Button) findViewById(buttonIDs[i]);
            keys[i].setTypeface(font);
            keys[i].setOnClickListener(keysClickListener);
            keyStringAr[i]=getResources().getString(stringIDs[i]);

        }

    }

    private void changeKeysLang(String keyText[]){
        int len= keyText.length;
        for(int i=0;i<len-1;i++)
        {   keys[i]= (Button) findViewById(buttonIDs[i]);
            keys[i].setText(keyText[i]);

        }

    }

    private void initializeViewsAr(){
        fab_lang.setImageResource(R.drawable.ic_en);
        welcome_tv.setText(R.string.welcome_ar);
        btn_login.setText(R.string.login_ar);
        MsgWrongPass= getResources().getString(R.string.wrong_password);
        MsgWait= getResources().getString(R.string.waiting_ar);
        Msgcanconnect= getResources().getString(R.string.fail_to_connect);
        MsgpleaseEnterip=getResources().getString(R.string.enter_host_beforelogin);
        MsgException= getResources().getString(R.string.MsgException_ar);
        Msg_yes=getResources().getString(R.string.login_ar);
        Msg_no= getResources().getString(R.string.back_ar);
        Msg_enterSpass=getResources().getString(R.string.enter_password_ar);

        changeKeysLang(keyStringAr);

    }


    private  void initializeViewsEn(){
        fab_lang.setImageResource(R.drawable.ic_ar);
        welcome_tv.setText(R.string.welcome_en);
        btn_login.setText("Login");
        MsgWrongPass="wrong password please try again";
        MsgWait="please wait";
        Msgcanconnect="unable to connect to server";
        MsgpleaseEnterip="please enter host and port address before attempting to login";
        Msg_enterSpass="Enter Settings Password";
        MsgException="Error in retrieving data";
        Msg_yes="Enter";
        Msg_no="Back";
        changeKeysLang(keyString);

    }

    private View.OnClickListener keysClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String num, pass;
            int c=0;
            while(buttonIDs[c]!= v.getId() && c<buttonIDs.length)
                c++;
            num=keyString[c];
            stringBuilder.append(num);
            pass = stringBuilder.toString();
            et_password.setText(pass);
            et_password.setSelection(pass.length());
        }

    };



    //async tasks
    public class httpGet extends AsyncTask<Void,Void,String> {
        String result,rs;
        JSONArray jArray;
        int resultint=0;
        Boolean serverisOnline;

        //procedure for doing ping on server and getting the password from UI and it works in the foreground
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            serverisOnline = ping();

            password= et_password.getText().toString();

        }

        // it does http connection in the background and it gets all the data at once after it connects
        // it cannot access UI components
        @Override
        protected String doInBackground(Void... params) {
            if(serverisOnline)
            try {
                String link = "http://"+ip+":"+port+"/H2O.asmx/Check_Waiter?Password="+password;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                //stream reader reads the response line by line
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                String[] separated = result.split(">");
                JSONObject jObject = new JSONObject(separated[2]);
                jArray = jObject.getJSONArray("Waiter");
                if(jArray.length()>0) {
                    JSONObject json_data = jArray.getJSONObject(0);
                    waiterID = json_data.getString("UserNo");
                    waiterName = json_data.getString("UserName");
                    resultint=1;
                }
                else resultint=2;//wrong password

            } catch (Exception e) {
            resultint=0;
            }
            if(resultint==1)
            try{
                String link ="http://"+ip+":"+port+"/H2O.asmx/Layout_Get_Sections";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"),8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {sb.append(line + "\n");
                }
                result = sb.toString();
                String[] separated = result.split(">");//removes xml header
                result=separated[2];
                JSONObject jObject = new JSONObject(result);
                jArray= jObject.getJSONArray("Sections");
                int jlen=jArray.length();
                floorNamesEN=new String[jlen];
                floorNamesAR=new String[jlen];
                sectionIDs=new String[jlen];
                for(int i=0; i<jlen; i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    floorNamesEN[i] = json_data.getString("Section_EN_Name");
                    floorNamesAR[i] = json_data.getString("Section_AR_Name");
                    sectionIDs[i] = json_data.getString("SectionID");
                }
                resultint=3; //sucessful retrieve

            } catch (Exception e){
                resultint=0;
            }
            if(resultint==3)
                try{
                    String link ="http://"+ip+":"+port+"/H2O.asmx/Menu_Get_Categories";
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"),8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {sb.append(line + "\n");
                    }
                    result = sb.toString();
                    String[] separated = result.split(">");
                    result=separated[2];
                    JSONObject jObject = new JSONObject(result);
                    jArray= jObject.getJSONArray("Categories");
                    int jlen=jArray.length();
                    categNamesAR=new String[jlen];
                    categNamesEN=new String[jlen];
                    categIDs=new String[jlen];
                    for(int i=0; i<jlen; i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        categNamesAR[i] = json_data.getString("Cat_AR_Name");
                        categNamesEN[i] = json_data.getString("Cat_EN_Name");
                        categIDs[i]=json_data.getString("CatID");
                    }
                    resultint=4;
                } catch (Exception e){
                resultint=0;
                }
            if(resultint==4)
            try{
                String link ="http://"+ip+":"+port+"/H2O.asmx/Menu_Get_Products";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {sb.append(line + "\n");
                }
                rs= sb.toString();
                String[] separated = rs.split(">");
                rs=separated[2];
                JSONObject jObject = new JSONObject(rs);
                jArray= jObject.getJSONArray("Products");
                int jArraylen=jArray.length();
                allItemNamesAR=new String[jArraylen];
                allItemNamesEN=new String[jArraylen];
                ItemIDs=new String[jArraylen];
                CategItemsIDs=new String[jArraylen];
                itemPrice=new double[jArraylen];
                for(int i=0; i<jArraylen; i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    allItemNamesAR[i] = json_data.getString("Pro_AR_Name");
                    allItemNamesEN[i] = json_data.getString("Pro_EN_Name");
                    CategItemsIDs[i] = json_data.getString("CatID");
                    ItemIDs[i] = json_data.getString("ProID");
                    itemPrice[i]=json_data.getDouble("Price");
                }
            resultint=5;
            } catch (Exception e){
            }
            if(resultint==5)
            try{
                String link ="http://"+ip+":"+port+"/H2O.asmx/Get_Flavors";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {sb.append(line + "\n");
                }
                rs= sb.toString();
                String[] separated = rs.split(">");
                rs=separated[2];
                JSONObject jObject = new JSONObject(rs);
                jArray= jObject.getJSONArray("Flavors");
                int jArraylen=jArray.length();
                flavorNamesEn=new String[jArraylen];
                flavorNamesAr=new String[jArraylen];
                flavorID=new String[jArraylen];
                flCatIDs=new String[jArraylen];
                for(int i=0; i<jArraylen; i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    flavorID[i] = json_data.getString("FlavorNo");
                    flavorNamesEn[i] = json_data.getString("FlavorEN");
                    flavorNamesAr[i]=json_data.getString("FlavorAR");
                    flCatIDs[i]=json_data.getString("Category");
                }
                resultint=6;
            } catch (Exception e){
                resultint=0;
            }
            rs="success";
            return rs;
        }

        // for updating UI after completing the doInBackground procedure, it handles the errors as well
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!serverisOnline)
                AppMsg.makeText(MainActivity.this, Msgcanconnect, AppMsg.STYLE_ALERT, (float)fontsize)
                        .setAnimation(slide_down,slide_up).show();
            else{
            switch (resultint){
                case 0:
                    AppMsg.makeText(MainActivity.this, MsgException, AppMsg.STYLE_ALERT,(float)fontsize)
                            .setAnimation(slide_down, slide_up).show();
                    break;
                case 2:
                    AppMsg.makeText(MainActivity.this, MsgWrongPass, AppMsg.STYLE_ALERT,(float)fontsize)
                            .setAnimation(slide_down, slide_up).show();

                    break;
                case 6:
                    AppMsg.makeText(MainActivity.this, "User ID:" + waiterID + "\n Username:" + waiterName,
                            AppMsg.STYLE_INFO, (float)fontsize).setAnimation(slide_down, slide_up).show();
                    if(language_bl)
                        startFloorsActivityAr();
                    else
                        startFloorsActivity();
                    break;
                }
            }

            progress_dialog.hide();
            btn_login.setEnabled(true);
        }

    }

    private void startFloorsActivity(){
        String loginTime= formatTime( System.currentTimeMillis());

        Intent intentFSelector = new Intent(MainActivity.this, FloorSelectorActivity.class);
        intentFSelector.putExtra("catNamesEN",categNamesEN);
        intentFSelector.putExtra("catIDs",categIDs);
        intentFSelector.putExtra("productNamesEN",allItemNamesEN);
        intentFSelector.putExtra("proPrice",itemPrice);
        intentFSelector.putExtra("proCatIDs",CategItemsIDs);
        intentFSelector.putExtra("proIDs", ItemIDs);
        intentFSelector.putExtra("waiterID",waiterID);
        intentFSelector.putExtra("flavorID",flavorID);
        intentFSelector.putExtra("flavorNamesEn",flavorNamesEn);
        intentFSelector.putExtra("flCatIDs",flCatIDs);
        intentFSelector.putExtra("floorNamesEN",floorNamesEN);
        intentFSelector.putExtra("sectionIDs", sectionIDs);
        intentFSelector.putExtra("waiterName",waiterName);
        intentFSelector.putExtra("loginTime",loginTime);
        startActivity(intentFSelector);

    }

    private void startFloorsActivityAr(){
        String loginTime= formatTime( System.currentTimeMillis());

        Intent intentFSelector = new Intent(MainActivity.this, FloorSelectorActivityAr.class);
        intentFSelector.putExtra("catIDs",categIDs);
        intentFSelector.putExtra("proPrice",itemPrice);
        intentFSelector.putExtra("proCatIDs",CategItemsIDs);
        intentFSelector.putExtra("catNamesAR", categNamesAR);
        intentFSelector.putExtra("productNamesAR", allItemNamesAR);

        intentFSelector.putExtra("proIDs", ItemIDs);
        intentFSelector.putExtra("waiterID",waiterID);
        intentFSelector.putExtra("flavorID",flavorID);
        intentFSelector.putExtra("flavorNamesAr",flavorNamesAr);
        intentFSelector.putExtra("flCatIDs",flCatIDs);
        intentFSelector.putExtra("floorNamesAR",floorNamesAR);
        intentFSelector.putExtra("sectionIDs", sectionIDs);

        intentFSelector.putExtra("waiterName",waiterName);
        intentFSelector.putExtra("loginTime",loginTime);
        startActivity(intentFSelector);

    }

    public String formatTime(long timenow){

        Date date = new Date(timenow);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return sdf.format(date);

    }

    private void showProgressDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        View promptView;
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        if(language_bl)
            promptView = layoutInflater.inflate(R.layout.progress_dialog_ar, null);
        else
            promptView= layoutInflater.inflate(R.layout.progress_dialog, null);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        progress_dialog=alertDialogBuilder.create();
        progress_dialog.show();
    }

    private void showPasswordDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        alertDialogBuilder.setMessage(Msg_enterSpass).setView(editText).setCancelable(false)
                .setPositiveButton(Msg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String entered_pass = editText.getText().toString();
                        if (settingspassword.equals(entered_pass)) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);

                        } else {
                        AppMsg.makeText(MainActivity.this, MsgWrongPass ,AppMsg.STYLE_ALERT,(float) fontsize)
                                .setAnimation(slide_down, slide_up).show();
                        }
                    }
                })
                .setNegativeButton(Msg_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               // to use the enter button in keyboard to login into the system settings
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String entered_pass = editText.getText().toString();
                   // if(!entered_pass.isEmpty()) {
                        if (settingspassword.equals(entered_pass)) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        //}
                    } else {
                        AppMsg.makeText(MainActivity.this, MsgWrongPass ,AppMsg.STYLE_ALERT,(float) fontsize)
                                .setAnimation(slide_down, slide_up).show();
                    }
                    alertDialog.cancel();
                }
                return false;
            }
        });

        alertDialog.show();
    }

    public boolean ping() {
        Runtime runtime = Runtime.getRuntime();

        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 "+ip);
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
