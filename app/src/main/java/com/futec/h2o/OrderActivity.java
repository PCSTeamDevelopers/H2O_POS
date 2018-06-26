package com.futec.h2o;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.devspark.appmsg.AppMsg;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;


public class OrderActivity extends AppCompatActivity {
    //declaring all string arrays
    String categNamesEN[],categIDs[];
    String allItemNamesEN[],ItemIDs[],CategItemsIDs[];
    String flavorID[],flavorNamesEn[],flCatIDs[],floorNamesEN[],sectionIDs[];
    double itemPrice[];
    int fontsize,  no_col_items, itemGridIndex,listIndexfl,flGridIndex, invoiceListIndex,item_icon_height,
            name_colwidth,price_colwidth,qyt_colwidth,total_colwidth;
    String waiterID,ip,port, orderId,selectedTableID,Qyt="1", flavor;
    boolean isChanged=false, addPro=false,otherNote_b,cancel_table =false, deleteFlavor=false;
    TextView grandTotal_tv;
    //font
    Typeface font;
    Context context= this;
    //prefs
    public static final String MyPREFERENCES="H2OPreference";
    SharedPreferences sharedPreferences;
    //declaring views
    ListView invoiceListView,menuDrawerList;
    GridView itemsGrid, flavorsGrid;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    //selected category arraylist (changes depending on selection)
    ArrayList<String> selectedCategProd,selectedCategPrice,selectedCatProID;
    //selected flavor array list
    ArrayList<String> selectedflavorID,selectedflavorNameEn,selectedflavorNameAr,note, isPrinted1,isPrinted2,isPrinted3;
    //invoice arrays
    ArrayList<String> lineIDs,productNames,unitPrice,subtotal,QYTs,FatherCateg;
    AlertDialog alertDialogFL;
    android.app.AlertDialog progress_dialog;
    float scale;
    Animation slide_down, slide_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_layout);
String x1;
x1="test2";
        //get info from shared preference
        sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        ip=sharedPreferences.getString("ip", "");
        port=sharedPreferences.getString("port", "");
        fontsize=sharedPreferences.getInt("font_size", 18);
        no_col_items=sharedPreferences.getInt("no_col_items", 4);
        item_icon_height= sharedPreferences.getInt("item_icon_height",8) *10;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //get info from intent
        Intent intent=getIntent();
        font= Typeface.createFromAsset(getAssets(), "ArabiaWeather.ttf");
        categIDs=intent.getStringArrayExtra("catIDs");
        flavorID=intent.getStringArrayExtra("flavorID");
        flCatIDs=intent.getStringArrayExtra("flCatIDs");
        CategItemsIDs=intent.getStringArrayExtra("proCatIDs");
        ItemIDs=intent.getStringArrayExtra("proIDs");
        categNamesEN=intent.getStringArrayExtra("catNamesEN");
        allItemNamesEN=intent.getStringArrayExtra("productNamesEN");
        flavorNamesEn=intent.getStringArrayExtra("flavorNamesEn");
        itemPrice=intent.getDoubleArrayExtra("proPrice");
        waiterID=intent.getStringExtra("waiterID");
        floorNamesEN= intent.getStringArrayExtra("floorNamesEN");
        sectionIDs=intent.getStringArrayExtra("sectionIDs");
        selectedTableID=intent.getStringExtra("selectedTableID");
        String tblname= intent.getStringExtra("selectedTableName");
        //find view by ids
        menuDrawerList=(ListView) findViewById(R.id.navList);
        invoiceListView= (ListView) findViewById(R.id.invoice_listview);
        itemsGrid=(GridView) findViewById(R.id.items_gridview);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        grandTotal_tv= (TextView) findViewById(R.id.grand_total_tv);
        Button fab_menu=(Button)findViewById(R.id.fab_menu);
        Button fab_cancel=(Button)findViewById(R.id.fab_cancel);
        Button fab_back=(Button)findViewById(R.id.fab_back);

        TextView firstcolumn_tv= (TextView) findViewById(R.id.delete_tv);
        TextView itemName_tv=(TextView) findViewById(R.id.item_name_tv);
        TextView price_tv= (TextView) findViewById(R.id.price_tv);
        TextView qyt_tv= (TextView) findViewById(R.id.qyt_tv);
        TextView total_tv=(TextView) findViewById(R.id.total_tv);
        TextView items_tv= (TextView) findViewById(R.id.items_tv);
        TextView invoice_tv= (TextView) findViewById(R.id.invoice_tv);

        try {
            slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            itemName_tv.setTextSize(fontsize);
            price_tv.setTextSize(fontsize);
            qyt_tv.setTextSize(fontsize);
            total_tv.setTextSize(fontsize);
            items_tv.setTextSize(fontsize);
            invoice_tv.setTextSize(fontsize);
            invoice_tv.setText(tblname);
            new httpGetOrderID().execute();
            try {
                itemsGrid.setNumColumns(no_col_items);
                categMenuadapter adapter = new categMenuadapter(OrderActivity.this, 1, categNamesEN);
                menuDrawerList.setAdapter(adapter);
                getSupportActionBar().hide();

            }catch (Exception e){}
            setupDrawer();
            fab_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            fab_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(menuDrawerList);
                }
            });

            fab_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean print = false;
                    if(isPrinted1!= null && isPrinted2!= null && isPrinted3!= null)
                    {
                    int size = isPrinted1.size();
                    for (int i = 0; i < size; i++) {
                        if (isPrinted1.get(i).equalsIgnoreCase("true") || isPrinted2.get(i).equalsIgnoreCase("true")
                                || isPrinted3.get(i).equalsIgnoreCase("true")) {
                            print = true;
                            break;
                        }
                    }
                    if (print)
                        AppMsg.makeText(OrderActivity.this, "Cant delete a table with prepared items",
                                AppMsg.STYLE_ALERT, (float) fontsize)
                                .setAnimation(slide_down, slide_up).show();
                    else showDialogCancelTable();
                }else showDialogCancelTable();
                }
            });



            itemsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemGridIndex = position;
                    Qyt="1";
                    new httpAddProduct().execute();
                }
            });

            itemsGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    itemGridIndex = position;
                    showDialogAddwithQyt();
                    return true;
                }
            });
        }catch (Exception e){}

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(!cancel_table )
        {
            new httpPrintOrder().execute();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void addDrawerItems(RadioGroup radioGroup, String s[]) {

        int len=s.length;
        for(int i=0; i<len; i++){
            RadioButton category_name_rb=new RadioButton(context);
            category_name_rb.setId(i);
            category_name_rb.setTextSize(fontsize);
            category_name_rb.setText(s[i]);
            category_name_rb.setTypeface(font);
            category_name_rb.setPadding(0, 5, 0, 5);
            radioGroup.addView(category_name_rb);}

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                selectedCategProd = new ArrayList<>();
                selectedCategPrice = new ArrayList<>();
                selectedCatProID = new ArrayList<>();
                int selectedCatID = Integer.parseInt(categIDs[checkedId]);
                int len = allItemNamesEN.length;
                for (int i = 0; i < len - 1; i++) {
                    if (selectedCatID == Integer.parseInt(CategItemsIDs[i])) {
                        selectedCategProd.add(allItemNamesEN[i]);
                        selectedCatProID.add(ItemIDs[i]);
                        selectedCategPrice.add(String.valueOf(itemPrice[i]));
                    }
                }
                //change items grid
                itemsGridAdapter adapter = new itemsGridAdapter(context, selectedCategProd);
                itemsGrid.setAdapter(adapter);
                drawerLayout.closeDrawer(menuDrawerList);
            }
        });
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public boolean onOptionsItemSelected(MenuItem item) {

                if (item != null && item.getItemId() == android.R.id.home) {
                    if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                    } else {
                        drawerLayout.openDrawer(Gravity.RIGHT);
                    }
                    return true;

                }
                return false;
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }


    public class itemsGridAdapter extends BaseAdapter {
        ArrayList<String> names;
        Context mContext;
        ViewHolder holder;
        public itemsGridAdapter(Context context,ArrayList<String> s) {
            this.mContext = context;
            this.names = s;
        }

        @Override
        public int getCount() {
            return names.size();
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
            TextView itemName;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            // if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_holder_items, parent, false);
            holder = new ViewHolder();
            holder.itemName=(TextView)convertView.findViewById(R.id.item_name);
            holder.itemName.setTypeface(font);
            holder.itemName.setText(names.get(position));
            holder.itemName.setHeight(item_icon_height);
            holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
            convertView.setTag(holder);
            //} else {  holder = (ViewHolder) convertView.getTag();}


            convertView.setTag(holder);
            return convertView;
        }

    }

    public class categMenuadapter extends BaseAdapter
    {
        public String list[];
        Activity activity;
        int count;
        public categMenuadapter(Activity activity,int count,String list[]) {
            super();
            this.activity = activity;
            this.list = list;
            this.count= count;
        }
        @Override
        public int getCount() {
            return count;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        private class ViewHolder {
            RadioGroup radioGroup;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater =  activity.getLayoutInflater();

            // if (convertView == null){
            convertView = inflater.inflate(R.layout.radiogroup_list, null);
            holder = new ViewHolder();
            holder.radioGroup = (RadioGroup)convertView.findViewById(R.id.radio_group);
            convertView.setTag(holder);
            addDrawerItems(holder.radioGroup, list);
            //}else{ holder = (ViewHolder) convertView.getTag();}


            return convertView;
        }

    }

    public class flavorsGridAdapter extends BaseAdapter {
        ArrayList<String> Names;
        Context mContext;

        public flavorsGridAdapter(Context context,ArrayList<String> s) {
            mContext = context;
            this.Names = s;
        }
        @Override
        public int getCount() {
            return Names.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View grid;
            flGridIndex=position;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                grid = inflater.inflate(R.layout.grid_holder_flavors, null);
            } else {
                grid = (View) convertView;
            }

            RadioButton rb = (RadioButton) grid.findViewById(R.id.radioButton_flavor);
            rb.setText(Names.get(position));
            rb.setTypeface(font);
            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        otherNote_b = false;
                        flGridIndex = position;
                        String fl = note.get(invoiceListIndex);
                        flavor = selectedflavorNameEn.get(flGridIndex);
                        if (!fl.contains(flavor)) {
                            new httpSetFlavor().execute();
                        }
                        alertDialogFL.cancel();
                    }
                }
            });
            return grid;
        }

    }

    public class invoiceListAdapter extends BaseAdapter {
        ArrayList<String> invoiceRows;
        Activity activity;
        int count=0;
        public invoiceListAdapter(Activity activity,ArrayList<String> s) {
            this.activity= activity;
            this.invoiceRows = s;
        }

        @Override
        public int getCount() {
            return invoiceRows.size();
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
            Button removeItem_btn;// first button delete product
            TextView itemNameTv; //text for product name
            TextView priceTv; // text for price of 1 product
            TextView qyt_tv; // button for edit qyt
            TextView totalpriceTv; // text for total price (qyt * product price)
            TextView noteTv; // text for note (flavors)
            Button add_note_btn; // button for adding notes
            RippleView rippleView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater =  activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_row, null);

            holder.removeItem_btn=(Button)convertView.findViewById(R.id.delete_item_btn);
            holder.itemNameTv=(TextView) convertView.findViewById(R.id.name_tv);
            holder.priceTv=(TextView) convertView.findViewById(R.id.price_tv);
            holder.qyt_tv= (TextView) convertView.findViewById(R.id.qyt_tv);
            holder.totalpriceTv=(TextView) convertView.findViewById(R.id.total_tv);
            holder.noteTv=(TextView) convertView.findViewById(R.id.note_tv);
            holder.add_note_btn=(Button) convertView.findViewById(R.id.btn_flavor);
            holder.rippleView= (RippleView) convertView.findViewById(R.id.rippleview_grid_item);
            //set text
            holder.itemNameTv.setText(productNames.get(position));
            String price=String.format("%.2f", Double.parseDouble(unitPrice.get(position)));
            holder.priceTv.setText(price);
            holder.qyt_tv.setText(QYTs.get(position));
            String totalprice=String.format("%.2f", Double.parseDouble(subtotal.get(position)));
            holder.totalpriceTv.setText(totalprice);
            holder.noteTv.setText(note.get(position));
            // set text size
            holder.itemNameTv.setTextSize(fontsize);
            holder.priceTv.setTextSize(fontsize);
            holder.qyt_tv.setTextSize(fontsize);
            holder.totalpriceTv.setTextSize(fontsize);
            holder.noteTv.setTextSize(fontsize);
            // set text font
            holder.itemNameTv.setTypeface(font);
            holder.priceTv.setTypeface(font);
            holder.qyt_tv.setTypeface(font);
            holder.noteTv.setTypeface(font);
            holder.totalpriceTv.setTypeface(font);
            //set col width

            if (isPrinted1.get(position).equalsIgnoreCase("true") || isPrinted2.get(position).equalsIgnoreCase("true")
                    || isPrinted3.get(position).equalsIgnoreCase("true"))
            holder.rippleView.setBackgroundResource(R.color.light_red);
            else holder.rippleView.setBackgroundResource(R.color.light_gray);

            if(note.get(position).isEmpty())
                holder.noteTv.setHeight(0);

            final int id= position;
            holder.qyt_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isPrinted1.get(id).equalsIgnoreCase("true") || isPrinted2.get(id).equalsIgnoreCase("true")
                            || isPrinted3.get(id).equalsIgnoreCase("true")) {
                        AppMsg.makeText(OrderActivity.this, "Cant change quantity of prepared item",
                                AppMsg.STYLE_ALERT, (float)fontsize)
                                .setAnimation(slide_down, slide_up).show();
                    }else
                    {   invoiceListIndex=id;
                        showDialogChangeQyt();
                    }
                }
            });
            holder.removeItem_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isPrinted1.get(id).equalsIgnoreCase("true") || isPrinted2.get(id).equalsIgnoreCase("true")
                            || isPrinted3.get(id).equalsIgnoreCase("true")) {
                        AppMsg.makeText(OrderActivity.this, "Cant delete a prepared item",
                                AppMsg.STYLE_ALERT, (float)fontsize)
                                .setAnimation(slide_down, slide_up).show();
                    }else
                    {   invoiceListIndex=id;
                        showDialogDelete();
                    }

                }
            });
            final int position_final=position;
            holder.add_note_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    invoiceListIndex= position_final;
                    if (isPrinted1.get(invoiceListIndex).equalsIgnoreCase("true") || isPrinted2.get(invoiceListIndex).equalsIgnoreCase("true")
                            || isPrinted3.get(invoiceListIndex).equalsIgnoreCase("true")) {
                        AppMsg.makeText(OrderActivity.this, "Cant add flavor to a prepared item",
                                AppMsg.STYLE_ALERT, (float)fontsize)
                                .setAnimation(slide_down, slide_up).show();
                        }
                    else
                    {
                        listIndexfl=position_final;
                        selectedflavorID = new ArrayList<>();
                        selectedflavorNameEn = new ArrayList<>();
                        selectedflavorNameAr = new ArrayList<>();
                        int selecteditemCatID = Integer.parseInt(FatherCateg.get(position_final));
                        int len = flavorNamesEn.length;
                        for (int i = 0; i < len; i++) {
                            if (selecteditemCatID == Integer.parseInt(flCatIDs[i])) {
                                selectedflavorID.add(flavorID[i]);
                                selectedflavorNameEn.add(flavorNamesEn[i]);
                            }
                        }

                        showFlavorDialog();
                    }

                    }
            });

            convertView.setTag(holder);
            return convertView;
        }



    }


    public class httpGetOrderID extends AsyncTask<Void,Void,String> {
        String result, rs, grandTotal;
        JSONArray jArray;
        Boolean isEmpty=true,r=false;
        int result_int;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String link = "http://"+ip+":"+port+"/H2O.asmx/Get_OrderID?TableID="+selectedTableID;
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
                String[] separated2= separated[2].split("<");
                orderId=separated2[0];
                result_int=1;
            } catch (Exception e) {
                result_int=0;
            }
            if(result_int==1)
                try {
                    String link = "http://"+ip+":"+port+"/H2O.asmx/Get_Order?OrderID="+orderId;
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
                    jArray = jObject.getJSONArray("OrderItems");
                    int jlen=jArray.length();
                    result_int=2;
                    if(jlen>0) {
                        isEmpty=false;
                        result_int=3; //not empty
                    }
                } catch (Exception e) {
                    result_int=0;
                }
            if(result_int==3)
                try {
                    String link = "http://"+ip+":"+port+"/H2O.asmx/Get_Total?OrderID="+orderId;
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"),8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    String[] separated = result.split(">");
                    JSONObject jObject = new JSONObject(separated[2]);
                    JSONArray jArray = jObject.getJSONArray("OrderTotal");

                    JSONObject json_data = jArray.getJSONObject(0);
                    // sub_total=json_data.getString("OrderSubTotal");
                    // discount=json_data.getString("OrderDiscountTotal");
                    //tax=json_data.getString("OrderTaxTotal");
                    //service=json_data.getString("OrderServiceTotal");
                    grandTotal=json_data.getString("OrderGrandTotal");
                    result_int=4;
                } catch (Exception e) {
                    result_int=0;
                }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (result_int) {
                case 0:
                    //error
                    break;
                case 2:
                    grandTotal_tv.setText("Grand Total: 00.00");
                    invoiceListView.setAdapter(null);
                    break;
                case 4:
                    grandTotal_tv.setText("Grand Total: " +grandTotal);
                    createInvoice(jArray);
                    break;


            }
            progress_dialog.hide();
        }

    }

    public class httpGetOrder extends AsyncTask<Void,Void,String> {
        String result, rs, grandTotal;
        JSONArray jArray;
        Boolean isEmpty=true;
        int result_int;
        BufferedReader reader;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String link = "http://"+ip+":"+port+"/H2O.asmx/Get_Order?OrderID="+orderId;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                String[] separated = result.split(">");
                JSONObject jObject = new JSONObject(separated[2]);
                jArray = jObject.getJSONArray("OrderItems");
                int jlen=jArray.length();
                result_int=1;
                if(jlen>0) {
                    isEmpty=false;
                    result_int=2; //not empty
                }
            } catch (Exception e) {
                result_int=0;
            }
            if(result_int==2)
                try {
                    String link = "http://"+ip+":"+port+"/H2O.asmx/Get_Total?OrderID="+orderId;
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);

                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"),8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    String[] separated = result.split(">");
                    JSONObject jObject = new JSONObject(separated[2]);
                    JSONArray jArray = jObject.getJSONArray("OrderTotal");

                    JSONObject json_data = jArray.getJSONObject(0);
                    // sub_total=json_data.getString("OrderSubTotal");
                    // discount=json_data.getString("OrderDiscountTotal");
                    //tax=json_data.getString("OrderTaxTotal");
                    //service=json_data.getString("OrderServiceTotal");
                    grandTotal=json_data.getString("OrderGrandTotal");
                    result_int=3;
                } catch (Exception e) {
                    result_int=0;
                }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (result_int) {
                case 0:
                    //error
                    break;
                case 1:
                    grandTotal_tv.setText("Grand Total : 00.00");
                    invoiceListView.setAdapter(null);
                    break;
                case 3:
                    grandTotal_tv.setText("Grand Total: " + grandTotal);
                    createInvoice(jArray);
                    break;


            }
            progress_dialog.hide();
        }


    }

    public class httpAddProduct extends AsyncTask<Void,Void,String> {
        String  rs;
        boolean success=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String proID=selectedCatProID.get(itemGridIndex);
                String link = "http://"+ip+":"+port+"/H2O.asmx/Add_Product?OrderID="+orderId+"&ItemNo="+proID
                        +"&Qty="+Qyt+"&Waiter="+waiterID;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                client.execute(request);
                addPro=true;
                isChanged=true;
                success=true;
            } catch (Exception e) {
                addPro=false;
                success=false;
            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(success)
                new httpGetOrder().execute();
            else progress_dialog.hide();
        }

    }

    public class httpRemoveProduct extends AsyncTask<Void,Void,String> {
        String rs;
        boolean success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String rowID=lineIDs.get(invoiceListIndex);
                String link = "http://"+ip+":"+port+"/H2O.asmx/Remove_Item?OrderID="+orderId+"&RowID="+rowID+"&Waiter="+waiterID;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                client.execute(request);
                rs = "sucessful";
                isChanged=true;
                success=true;
            } catch (Exception e) {
                success=true;
            }
            return rs;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(success)
                new httpGetOrder().execute();
            else progress_dialog.hide();

        }

    }

    public class httpChangeQyt extends AsyncTask<Void,Void,String> {
        String rs;
        boolean success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String rowID=lineIDs.get(invoiceListIndex);
                //selectionIndex=itemIndex;
                String link = "http://"+ip+":"+port+"/H2O.asmx/Item_Qty?OrderID="+orderId+"&RowID="+rowID+"&Qty="+Qyt;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                success=true;
                isChanged= true;
            } catch (Exception e) {
                success=false;
            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(success)
                new httpGetOrder().execute();
            else progress_dialog.hide();
        }

    }

    public class httpSetFlavor extends AsyncTask<Void,Void,String> {
        String  rs;
        boolean  result=false;
        String link,rowID,fl="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            rowID=lineIDs.get(invoiceListIndex);
            fl=note.get(invoiceListIndex);
            if(deleteFlavor)
            { fl="";
            flavor="";}
        }

        @Override
        protected String doInBackground(Void... params) {
                try {
                    String query;
                    if(fl.length()>1 ){
                         query = URLEncoder.encode(fl+"\n"+flavor, "utf-8");
                    }
                    else
                    {query = URLEncoder.encode(flavor, "utf-8");}

                    link = "http://"+ip+":"+port+"/H2O.asmx/Set_Flavor?OrderID="+orderId+"&RowID="+rowID+"&Note="+query;

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    client.execute(request);
                    isChanged=true;
                    result=true;
                    flavor="";
                    deleteFlavor=false;
                } catch (Exception e) {
                    result=false;
                }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(result){
                new httpGetOrder().execute();

            }else progress_dialog.hide();
        }
    }

    public class httpCancelTable extends AsyncTask<Void,Void,String> {
        String rs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cancel_table=false;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                String link = "http://"+ip+":"+port+"/H2O.asmx/Cancel_Table?OrderID="+orderId+"&Waiter="+waiterID;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                client.execute(request);
                cancel_table=true;

            } catch (Exception e) {
                cancel_table=false;

            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(cancel_table) {
                finish();

            }
        }

    }

    public class httpPrintOrder extends AsyncTask<Void,Void,String> {
        String  rs;
        int lang;
        boolean result=false;
        @Override
        protected String doInBackground(Void... params) {
            try {
                lang=2;// english

                String link = "http://"+ip+":"+port+"/H2O.asmx/Print_In_Kitchen?OrderID="+orderId+"&Lang="+lang;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                client.execute(request);
                result=true;
            } catch (Exception e) {
                result=false;
            }
            return rs;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }


    public void createInvoice(JSONArray jArray){
        try{
            lineIDs = new ArrayList<>();
            productNames = new ArrayList<>();
            QYTs = new ArrayList<>();
            unitPrice = new ArrayList<>();
            subtotal = new ArrayList<>();
            FatherCateg=new ArrayList<>();
            isPrinted1=new ArrayList<>();
            isPrinted2=new ArrayList<>();
            isPrinted3=new ArrayList<>();
            note=new ArrayList<>();
            int jlen=jArray.length();
            for (int i = jlen-1; i >= 0; i--) {
                if(jArray.getJSONObject(i)!=null) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    lineIDs.add(json_data.getString("LineID"));
                    productNames.add(json_data.getString("ItemDesc"));
                    unitPrice.add(json_data.getString("UnitPrice"));
                    subtotal.add(json_data.getString("SubTotal"));
                    QYTs.add(json_data.getString("Qty"));
                    FatherCateg.add(json_data.getString("Father"));
                    note.add(json_data.getString("LineNote"));
                    isPrinted1.add(json_data.getString("IsPrinted"));
                    isPrinted2.add(json_data.getString("IsPrinted2"));
                    isPrinted3.add(json_data.getString("IsPrinted3"));
                }
            }

        }catch (Exception e){}
        invoiceListAdapter adapter = new invoiceListAdapter(OrderActivity.this, lineIDs);
        invoiceListView.setAdapter(adapter);
    }

    private void showDialogDelete(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Delete product");
        alertDialogBuilder
                .setMessage("Are you sure you want to delete this product?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new httpRemoveProduct().execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogChangeQyt(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Change Quantity");
        final NumberPicker numberPicker= new NumberPicker(context);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        alertDialogBuilder
                .setMessage("Choose New Quantity").setView(numberPicker)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Qyt = ""+ numberPicker.getValue();
                        new httpChangeQyt().execute();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
    }

    private  void showFlavorDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View promptView;
        LayoutInflater layoutInflater = LayoutInflater.from(OrderActivity.this);
        promptView = layoutInflater.inflate(R.layout.dialog_flavors, null);
        final EditText editText = (EditText) promptView.findViewById(R.id.editText_note);
        Button add_note = (Button) promptView.findViewById(R.id.button_note);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherNote_b = true;
                flavor = editText.getText().toString();
                new httpSetFlavor().execute();
                alertDialogFL.cancel();
            }
        });
        if (selectedflavorNameEn.size() > 1) {
            flavorsGrid = (GridView) promptView.findViewById(R.id.gridView_flavors);
            flavorsGridAdapter adapter = new flavorsGridAdapter(OrderActivity.this,
                    selectedflavorNameEn);
            flavorsGrid.setAdapter(adapter);
        }

        alertDialogBuilder.setTitle("Flavors");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Clear current note", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFlavor = true;
                new httpSetFlavor().execute();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogFL = alertDialogBuilder.create();
        alertDialogFL.show();

    }

    private void showDialogCancelTable(){
        try { AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
              alertDialogBuilder.setTitle("Cancel Table");
                    alertDialogBuilder.setMessage("Are you sure you want to cancel this table?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new httpCancelTable().execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

        } catch (Exception e) {
        }

    }

    private void showDialogAddwithQyt(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Add item");
        final NumberPicker numberPicker= new NumberPicker(context);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        alertDialogBuilder
                .setMessage("Choose Quantity").setView(numberPicker)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Qyt = ""+ numberPicker.getValue();
                        new httpAddProduct().execute();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showProgressDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        View promptView;
        LayoutInflater layoutInflater = LayoutInflater.from(OrderActivity.this);
        promptView = layoutInflater.inflate(R.layout.progress_dialog, null);

        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        progress_dialog=alertDialogBuilder.create();
        progress_dialog.show();
    }

}
