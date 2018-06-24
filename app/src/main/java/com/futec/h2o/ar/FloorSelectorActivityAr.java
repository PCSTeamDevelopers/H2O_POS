package com.futec.h2o.ar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.futec.h2o.OrderActivity;
import com.futec.h2o.OrderActivity_gridstyle;
import com.futec.h2o.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FloorSelectorActivityAr extends AppCompatActivity  {

    //declaring all string arrays
    String ItemIDs[],CategItemsIDs[],flCatIDs[],categIDs[],flavorID[],sectionIDs[];
    double itemPrice[];
    String allItemNamesAR[],flavorNamesAr[],floorNamesAR[],categNamesAR[];
    int tsectionIDs[], index, spinnerIndex,tvLen;
    String waiterID,ip,port,sectionID;
    //string arrays
    ArrayList<String> tableNamesAR,tableNamesEN,tableIDs,tablestatus,checkout,tStartTime,tLastUpdate;
    int numberOfPeople;
    GridView gridview;
    ArrayList<TextView> time_tvs,time_tvs2;
    ArrayList<Long> time_tvsval,time_tvsval2;
    long timelong[],timelong2[];
    //prefs
    public static final String MyPREFERENCES="H2OPreference";
    SharedPreferences sharedPreferences;
    Typeface font;
    Spinner spinner_navigation;
    int fontsize,  no_col_tables;
    Boolean dialogShown=false, style, refresh=false;
    Context context= this;
    JSONArray jArray;
    String tblid, Msg_ide,Msg_busy, waiterName, loginTime,tblname;
    android.app.AlertDialog progress_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_selector_ar);

        sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        ip=sharedPreferences.getString("ip", "");
        port=sharedPreferences.getString("port","");
        //find view by ids
        Toolbar toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
         spinner_navigation = (Spinner)findViewById(R.id.spinner_nav);
        gridview=(GridView) findViewById(R.id.tables_grid);
        TextView waiterName_tv=(TextView)findViewById(R.id.tv_waiter);
        TextView loginTime_tv=(TextView) findViewById(R.id.tv_logintime);
        //get data from previous activity
        Intent intent = getIntent();
        categIDs=intent.getStringArrayExtra("catIDs");
        flavorID=intent.getStringArrayExtra("flavorID");
        flCatIDs=intent.getStringArrayExtra("flCatIDs");
        CategItemsIDs=intent.getStringArrayExtra("proCatIDs");
        ItemIDs=intent.getStringArrayExtra("proIDs");
        categNamesAR=intent.getStringArrayExtra("catNamesAR");
        allItemNamesAR=intent.getStringArrayExtra("productNamesAR");
        flavorNamesAr=intent.getStringArrayExtra("flavorNamesAr");
        floorNamesAR=intent.getStringArrayExtra("floorNamesAR");
        itemPrice=intent.getDoubleArrayExtra("proPrice");
        waiterID=intent.getStringExtra("waiterID");
        sectionIDs=intent.getStringArrayExtra("sectionIDs");
        waiterName=intent.getStringExtra("waiterName");
        loginTime=intent.getStringExtra("loginTime");
        //get saved settings
        no_col_tables=sharedPreferences.getInt("no_col_tables", 5);
        fontsize= sharedPreferences.getInt("font_size_ar", 18);
        style= sharedPreferences.getBoolean("style_type",false);

        try{
            //showProgressDialog();
            font= Typeface.createFromAsset(getAssets(), "ArabiaWeather.ttf");
            //setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().hide();
            //setup navigation spinner
           SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(),
                   floorNamesAR);
            spinner_navigation.setAdapter(adapter);
            String waiter=getResources().getString(R.string.waiter_ar);
            String login=getResources().getString(R.string.login_time_ar);
            Msg_busy= getResources().getString(R.string.busy_ar);
            Msg_ide= getResources().getString(R.string.idle_ar);
           // MsgWait=getResources().getString(R.string.waiting_ar);
            waiterName_tv.setText(waiter+ " "+waiterName);
            loginTime_tv.setText(login+" "+loginTime);
            waiterName_tv.setTextSize(fontsize);
            loginTime_tv.setTextSize(fontsize);

            gridview.setNumColumns(no_col_tables);
            sectionID=sectionIDs[0];
            //new httpGetTables().execute();

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tblid= tableIDs.get(position);
                    tblname = tableNamesAR.get(position);
                    if (tablestatus.get(position).equalsIgnoreCase("null")||tablestatus.get(position).equalsIgnoreCase("Canceled")) {
                        if(!dialogShown)
                            showDialog();
                    } else {
                        startNextActivity(tblid,tblname);
                    }
                }
            });

            FloatingActionButton fab_refresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
            fab_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   refresh= true;
                    new httpGetTables().execute();
                }
            });
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_floors);
            fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 spinner_navigation.performClick();
             }
         });


    }catch (Exception e){}

    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        new httpGetTables().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public class httpGetTables extends AsyncTask<Void,Void,String> {
        String result, rs;

        boolean bl_result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String link = "http://"+ip+":"+port+"/H2O.asmx/Layout_Get_Tables";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                String[] separated = result.split(">");
                JSONObject jObject = new JSONObject(separated[2]);
                jArray = jObject.getJSONArray("Tables");
                bl_result=true;
                rs = "sucessful";
            } catch (Exception e) {
                bl_result=false;
                rs = "Fail";
            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(bl_result)
            updateTables(jArray);
            if(!refresh)
            progress_dialog.hide();
            refresh=false;

        }
    }

    public class httpOpenTable extends AsyncTask<Void,Void,String> {
        String rs;
        boolean result_bl=false;
        String TableNo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                 TableNo=tblid;
                String link ="http://"+ip+":"+port+"/H2O.asmx/Open_Table?TableNo="+TableNo+"&Waiter="
                        +waiterID+"&Customer=0&SeatCount="+numberOfPeople;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                client.execute(request);
                result_bl=true;
            } catch (Exception e){
                rs="Fail";
            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(result_bl)
                startNextActivity(TableNo, tblname);
            progress_dialog.hide();
        }


    }

    private void updateTables(JSONArray jArray){
        int jlen=jArray.length();
        tableNamesEN = new ArrayList<>();
        tableNamesAR = new ArrayList<>();
        tableIDs = new ArrayList<>();
        checkout = new ArrayList<>();
        tStartTime=new ArrayList<>();
        tsectionIDs = new int[jlen];
        tablestatus = new ArrayList<>();
        tLastUpdate=new ArrayList<>();
        try{
        for (int i = 0; i < jlen; i++) {
            JSONObject json_data = jArray.getJSONObject(i);
            tsectionIDs[i] = json_data.getInt("SectionID");
            if (tsectionIDs[i] == Integer.parseInt(sectionID)) {
                tableNamesAR.add(json_data.getString("Table_AR_Name"));
                tablestatus.add(json_data.getString("Status"));
                tableNamesEN.add(json_data.getString("Table_EN_Name"));
                tableIDs.add(json_data.getString("TableID"));
                checkout.add(json_data.getString("CheckOut"));
                tStartTime.add(json_data.getString("StartTime"));
                tLastUpdate.add(json_data.getString("LastUpdate"));
            }
        }
            gridAdapter  newAdapter = new gridAdapter(FloorSelectorActivityAr.this,tableNamesAR);
            gridview.setAdapter(newAdapter);
            timeTicking();
        }catch (Exception e){}

    }

    public class gridAdapter extends BaseAdapter {
        ArrayList<String> tablenames;
        Context mContext;
        ViewHolder holder;
        int count=1;
        public gridAdapter(Context context,ArrayList<String> s) {
            mContext = context;
            this.tablenames = s;
            time_tvs= new ArrayList<>();
            time_tvs2=new ArrayList<>();
            time_tvsval= new ArrayList<>();
            time_tvsval2=new ArrayList<>();
        }

        @Override
        public int getCount() {
            return tablenames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            TextView tablename;
            TextView timerbusy;
            TextView timeridle;
            ImageView tableicon;
            //RippleView rippleView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            // if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_holder_tables, parent, false);
            holder = new ViewHolder();
            //holder.rippleView= (RippleView)  convertView.findViewById(R.id.rippleview_grid_item);
            holder.timerbusy=(TextView)convertView.findViewById(R.id.textView_busy);
            holder.timeridle=(TextView)convertView.findViewById(R.id.textView_idle);
            holder.tablename = (TextView) convertView.findViewById(R.id.grid_text);
            holder.tableicon = (ImageView)convertView.findViewById(R.id.grid_image_blue);
            holder.tablename.setTypeface(font);
            holder.timerbusy.setTypeface(font);
            holder.timeridle.setTypeface(font);
            convertView.setTag(holder);
            //} else {  holder = (ViewHolder) convertView.getTag();}
            if(tStartTime.get(position)!="null") {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                long lon= getTime(tStartTime.get(position));
                time_tvsval.add(lon);
                //holder.timerbusy.setId(count+10);
                Date date= new Date(lon);
                String s = sdf.format(date);

                holder.timerbusy.setText( Msg_busy +" "+s);
                time_tvs.add(holder.timerbusy);
                count++;
            }
            if(tLastUpdate.get(position)!="null"){
                holder.timeridle.setId(count + 100);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                long lon = getTime(tLastUpdate.get(position));
                time_tvsval2.add(lon);

                Date date= new Date(lon);
                String s = sdf.format(date);
                holder.timeridle.setText( Msg_ide + " " +s);
                time_tvs2.add(holder.timeridle);
                count++;
            }
            holder.tablename.setText(tablenames.get(position));
            String s1=tablestatus.get(position);
            if(checkout.get(position).equalsIgnoreCase("true") && s1.equalsIgnoreCase("Pending"))
            {holder.tableicon.setImageResource(R.drawable.button_blue_plain);} //light blue
            else if (s1.equalsIgnoreCase("Pending"))
            {
                holder.tableicon.setImageResource(R.drawable.button_plain_green);}//green
            else{
                holder.tableicon.setImageResource(R.drawable.button_plain_gray);} //gray
            index = position;

            convertView.setTag(holder);
            return convertView;
        }



    }

    public class SpinnerAdapter extends BaseAdapter
    {
        LayoutInflater inflator;
        String[] stringArray;

        public SpinnerAdapter( Context context ,String[] counting)
        {
            inflator = LayoutInflater.from(context);
            stringArray=counting;
        }

        @Override
        public int getCount()
        {
            return stringArray.length;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = inflator.inflate(R.layout.spinner_item_invisible, null);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent,
                    false);
            RippleView rippleView= (RippleView) row.findViewById(R.id.rippleview_spinner);
            TextView txtview = (TextView) row.findViewById(R.id.spinner_tv);
            txtview.setTypeface(font);
            txtview.setTextSize(fontsize);
            txtview.setText(stringArray[position]);
            final int spinnerIndex=position;
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{sectionID=sectionIDs[spinnerIndex];
                        updateTables(jArray);
                    }catch (Exception e){}
                }
            });
            return row;
        }
    }

    public long getTime(String dt){
        long t=0;
        //Calendar rightNow = Calendar.getInstance();
        //long offset = rightNow.get(Calendar.ZONE_OFFSET)+rightNow.get(Calendar.DST_OFFSET);
        try {
            String oldDate = dt.substring(dt.lastIndexOf("(") + 1, dt.lastIndexOf(")"));
            long oDate = Long.valueOf(oldDate);
            long timenow = System.currentTimeMillis();

            //long resultime= Math.abs(timenow-oDate);
            //+date2.getTime();

            Date date1 = new Date(oDate);
            Date date2= new Date(timenow);
            //int hours= date2.getHours();
            //int minutes= date2.getMinutes();
            //int sec= date2.getSeconds();
            int hours = Math.abs(date2.getHours() - date1.getHours());
            int minutes = Math.abs(date2.getMinutes() - date1.getMinutes());
            int sec = Math.abs(date2.getSeconds() - date1.getSeconds());

            Date date= new Date(0,0,0,hours,minutes,sec);

            return date.getTime();
        }catch (Exception e){

            return t;}
    }

    public String updateTime(long dt){
        Date date = new Date(dt);

        return String.format("%02d",date.getHours())+":"+String.format(
                "%02d",date.getMinutes())+":"+String.format("%02d",date.getSeconds());

    }

    private void timeTicking(){
        stopDisconnectTimer();
        if(time_tvs!= null && time_tvsval!=null && time_tvs2!=null )
        try {


            disconnectHandler.postDelayed(r2, 1000);
        }catch(Exception e){}


    }


    private Runnable  r2= new Runnable() {
        @Override
        public void run() {
            //Do something after 1000ms

            tvLen = time_tvs.size();
            timelong = new long[tvLen];
            timelong2 = new long[tvLen];
            for (int j = 0; j < tvLen; j++) {
                timelong[j] = time_tvsval.get(j);
                timelong2[j] = time_tvsval2.get(j);
            }

            disconnectHandler.postDelayed(r, 2000);

        }
    };

    private  Runnable r = new Runnable() {
        public void run() {
            disconnectHandler.postDelayed(this, 1000);
            int size = time_tvs.size();
            for(int j=0; j<size; j++ )
            {   try{
                if(timelong2.length>0 && timelong.length>0)
                {timelong[j]+=1000;
                    timelong2[j]+=1000;
                    String busy= updateTime(timelong[j]);

                    time_tvs.get(j).setText(Msg_busy +" "+busy );
                    String idle=updateTime(timelong2[j]);
                    time_tvs2.get(j).setText(Msg_ide+" "+idle);}


            }catch (Exception e){}
            }

        }
    };

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };


    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(r);
        disconnectHandler.removeCallbacks(r2);
    }

    private void startNextActivity( String tableId, String tblname){
        Intent appIntent;
        if(style)
            appIntent = new Intent(FloorSelectorActivityAr.this, OrderActivity_gridstyleAr.class);
        else appIntent = new Intent(FloorSelectorActivityAr.this, OrderActivityAr.class);

        appIntent.putExtra("catNamesAR", categNamesAR);
        appIntent.putExtra("productNamesAR", allItemNamesAR);
        appIntent.putExtra("flavorNamesAr", flavorNamesAr);
        appIntent.putExtra("floorNamesAR",floorNamesAR);

        appIntent.putExtra("selectedTableID",tableId);
        appIntent.putExtra("selectedTableName",tblname);
        appIntent.putExtra("catIDs",categIDs);
        appIntent.putExtra("proPrice",itemPrice);
        appIntent.putExtra("proCatIDs",CategItemsIDs);
        appIntent.putExtra("proIDs", ItemIDs);
        appIntent.putExtra("waiterID",waiterID);
        appIntent.putExtra("SectionID", sectionID);
        appIntent.putExtra("waiterID", waiterID);
        appIntent.putExtra("flavorID", flavorID);
        appIntent.putExtra("flCatIDs", flCatIDs);
        startActivity(appIntent);
    }

    private void showDialog(){
        dialogShown=true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        final NumberPicker numberPicker= new NumberPicker(context);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(50);

        alertDialogBuilder.setTitle(R.string.open_table_ar);
        alertDialogBuilder
                .setMessage(R.string.open_table_dialog_ar).setView(numberPicker)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_ar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        numberOfPeople=numberPicker.getValue();
                        new httpOpenTable().execute();
                        dialogShown=false;

                    }
                })
                .setNegativeButton(R.string.no_ar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialogShown = false;
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showProgressDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        View promptView;
        LayoutInflater layoutInflater = LayoutInflater.from(FloorSelectorActivityAr.this);
        promptView = layoutInflater.inflate(R.layout.progress_dialog_ar, null);

        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        progress_dialog=alertDialogBuilder.create();
        progress_dialog.show();
    }
}
