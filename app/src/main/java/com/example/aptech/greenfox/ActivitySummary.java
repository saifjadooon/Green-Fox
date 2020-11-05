package com.example.aptech.greenfox;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ActivitySummary extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_summary_sub_recepts_count_qty, tv_summary_status, tv_summary_starts_at, tv_summary_curr_amt,v;
    private ListView lv_summary_sub_orders;
    private File pdfFile;
    private String table_no = "", acc_auto_id = "", table_auto_id = "", table_order_auto_id = "";
    private String food_it = "", f_q = "";
    private String i_detail;
    TextView tv;
    private FloatingActionButton fab_add_items, fab_order_final, fab_printer;

    ProgressDialog progressDialog;

    TextView tname,tprice,ttotal,tquantity;
    static Urls urls = new Urls();
    private static final String URL_PROCESSING = urls.url_processing();
    private String suborder_auto_id;
    private String suborder_auto_id1;
    private String sub_id_copy;
    int pageWidth = 1200;
    String totel="";
    private String name_of_ietm = "", quntity_of_item = "";
    WebView webview;
    Date dateobj;
    DateFormat dateFormat;
    private ArrayList<String> arrayList_sub_orders;
    private ArrayList<String> arrayList_sub_orders_items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        //webview=(WebView) findViewById(R.id.webview1);
        tv_summary_sub_recepts_count_qty = (TextView) findViewById(R.id.tv_summary_sub_recepts_count_qty);
        tv_summary_status = (TextView) findViewById(R.id.tv_summary_status);
        tv_summary_starts_at = (TextView) findViewById(R.id.tv_summary_starts_at);
        tv_summary_curr_amt = (TextView) findViewById(R.id.tv_summary_curr_amt);
        lv_summary_sub_orders = (ListView) findViewById(R.id.lv_summary_sub_orders);
        fab_add_items = (FloatingActionButton) findViewById(R.id.fab_add_items);
        fab_order_final = (FloatingActionButton) findViewById(R.id.fab_order_final);

        tname=(TextView) findViewById(R.id.name_of_item1);
        tprice=(TextView) findViewById(R.id.price_of_item);
        tquantity=(TextView) findViewById(R.id.quantity_of_item);
        ttotal=(TextView) findViewById(R.id.total_price_of_item);

        tv=(TextView) findViewById(R.id.name_of_item1);
        fab_printer = (FloatingActionButton) findViewById(R.id.fab_printer);
        Intent intent = getIntent();
        table_no = intent.getStringExtra("table_no");
        setTitle("GreenFox - " + table_no);
        acc_auto_id = intent.getStringExtra("acc_auto_id");
        table_auto_id = intent.getStringExtra("table_auto_id");
        food_it = intent.getStringExtra("Item_name");
        f_q = intent.getStringExtra("Item_quantity");
        table_order_auto_id = intent.getStringExtra("table_order_auto_id");
        tv_summary_sub_recepts_count_qty.setText(intent.getStringExtra("sub_recepts_count_qty"));
        tv_summary_status.setText(intent.getStringExtra("status"));
        tv_summary_starts_at.setText(intent.getStringExtra("start_at").split(" ")[1]);
        tv_summary_curr_amt.setText(intent.getStringExtra("curr_amt"));

        Log.v("MAIN ACTIVITY DATA", "table id: " + table_auto_id + " order id: " + table_order_auto_id + "item name: " + food_it);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        arrayList_sub_orders = new ArrayList<>();
        arrayList_sub_orders_items = new ArrayList<>();
        fab_order_final.setVisibility(View.INVISIBLE);

        fab_add_items.setOnClickListener(this);
        fab_order_final.setOnClickListener(this);


        getsuborders_usingVolley("get_sub_orders_detail", table_auto_id, table_order_auto_id);

        lv_summary_sub_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                suborder_auto_id = arrayList_sub_orders.get(i).split("#")[0];
                sub_id_copy=suborder_auto_id;
                getsubordersItems_usingVolley("get_sub_orders_items_detail", suborder_auto_id);
            }
        });



        fab_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pdfdoc();
                clicked(view);
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    ;


    /*
        fab_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PdfDocument myPdfDocument = new PdfDocument();
                Paint myPaint = new Paint();
                Paint titlePaint = new Paint();
                PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                Canvas canvas = myPage1.getCanvas();


                titlePaint.setTextAlign(Paint.Align.CENTER);
                titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                titlePaint.setTextSize(40);
                canvas.drawText("Green Fox", pageWidth / 2, 270, titlePaint);

                myPaint.setColor(Color.rgb(0, 113, 188));
                myPaint.setTextSize(50f);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Table# " + table_no, 116, 40, myPaint);
                canvas.drawText(i_detail, 116, 40, myPaint);


                myPdfDocument.finishPage(myPage1);
                File file = new File(Environment.getExternalStorageDirectory(), "Reciept.pdf");

                try {
                    myPdfDocument.writeTo(new FileOutputStream(file));
                    Toast.makeText(ActivitySummary.this, "Done!!", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivitySummary.this, "Nhe hoa Kaam" + e, Toast.LENGTH_SHORT).show();
                }

                myPdfDocument.close();
            }
        });*/
    @Override
    protected void onResume() {
        super.onResume();
        getUpdatedSummary_usingVolley("get_updated_summary", table_auto_id);
        //Toast.makeText(this, "refresh" + table_auto_id + "-" + table_order_auto_id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        if (view == fab_add_items) {
            Intent intent = new Intent(ActivitySummary.this, ActivityAddItems.class);
            intent.putExtra("table_no", table_no);
            intent.putExtra("table_auto_id", table_auto_id);
            intent.putExtra("table_order_auto_id", table_order_auto_id);
            intent.putExtra("acc_auto_id", acc_auto_id);
            startActivity(intent);
        }
        if (view == fab_order_final) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure want to close order and vacant table?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            finalOrder_usingVolley("final_order_and_vacant_table", table_auto_id, table_order_auto_id);
                        }
                    })
                    .setNegativeButton("NO", null).show();
        }
    }

    private void getsuborders_usingVolley(final String action, final String tbl_id, final String ord_id) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        //volley code

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROCESSING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            arrayList_sub_orders.clear();
                            Log.v("response", response);

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                            String response_status = jsonObject_data.getString("response_status");
                            String response_msg = jsonObject_data.getString("response_msg");
                            JSONArray jsonArray_response_data = jsonObject_data.getJSONArray("response_data");

                            //Log.v("response", String.valueOf(jsonArray_response_data));

                            if (response_status.equals("success")) {
                                for (int i = 0; i < jsonArray_response_data.length(); i++) {
                                    JSONObject jsonObject_suborder = jsonArray_response_data.getJSONObject(i);
                                    String sub_order_auto_id = jsonObject_suborder.getString("sub_order_auto_id");
                                    String sub_order_id = jsonObject_suborder.getString("sub_order_id");
                                    String sub_order_amt = jsonObject_suborder.getString("sub_order_amt");
                                    String created_at = jsonObject_suborder.getString("created_at");
                                    totel=sub_order_amt;
                                    arrayList_sub_orders.add(sub_order_auto_id + "#" + sub_order_id + "#" + sub_order_amt + "#" + created_at);

                                }
                                //Log.v("jsonObject_table1", String.valueOf(arrayList_sub_orders));
                                fab_order_final.setVisibility(View.VISIBLE);
                            } else {
                                arrayList_sub_orders.add("No Sub Orders");
                                //show_dialog_message("Invalid Credentials", "No Sub Orders");
                                //Log.v("response", "Invalid Credentials");
                                fab_order_final.setVisibility(View.INVISIBLE);
                            }

                            set_suborders_in_listview();
                        } catch (Exception ex) {
                            show_dialog_message("Network Error", "Check network connection");
                            Log.v("catch err", ex.getMessage());
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        show_dialog_message("Network Error", "Check network connection");
                        //Log.v("Network Error", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("tbl_id", tbl_id);
                params.put("ord_id", ord_id);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void finalOrder_usingVolley(final String action, final String tbl_id, final String ord_id) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        //volley code

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROCESSING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            arrayList_sub_orders.clear();
                            Log.v("response", response);

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                            String response_status = jsonObject_data.getString("response_status");
                            String response_msg = jsonObject_data.getString("response_msg");
                            JSONArray jsonArray_response_data = jsonObject_data.getJSONArray("response_data");

                            //Log.v("response", String.valueOf(jsonArray_response_data));

                            if (response_status.equals("success")) {
                                Toast.makeText(ActivitySummary.this, response_msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                            }
                        } catch (Exception ex) {
                            show_dialog_message("Network Error", "Check network connection");
                            Log.v("catch err", ex.getMessage());
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        show_dialog_message("Network Error", "Check network connection");
                        //Log.v("Network Error", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("tbl_id", tbl_id);
                params.put("ord_id", ord_id);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void set_suborders_in_listview() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList_sub_orders) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //textView.setBackgroundColor(Color.GREEN);

                try {
                    //creating the full list
                    String suborder_auto_id = textView.getText().toString().split("#")[0];
                    String suborder_id = textView.getText().toString().split("#")[1];
                    String suborder_amt = textView.getText().toString().split("#")[2];
                    String suborder_created_at = textView.getText().toString().split("#")[3];
                    textView.setText("\n" + suborder_id + "\n" + "Amount: " + suborder_amt + "\n" + "Created at: " + suborder_created_at + "\n");


                    return view;
                } catch (Exception ex) {
                    return view;
                }
            }
        };

        lv_summary_sub_orders.setAdapter(arrayAdapter);
    }

    private void set_suborders_items_in_custom_listview(ListView listview) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList_sub_orders_items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //textView.setBackgroundColor(Color.GREEN);

                try {
                    String item_name = textView.getText().toString().split("#")[0];
                    String sub_order_item_price = textView.getText().toString().split("#")[1];
                    String sub_order_item_qty = textView.getText().toString().split("#")[2];
                    String sub_order_item_total_amt = textView.getText().toString().split("#")[3];

                    //String tab_n=textView.getText().toString().split("#")[4];
                    textView.setText("\n" + item_name + "\n" + "Price: " + sub_order_item_price + "\n" + "Qty. Ordered: " + sub_order_item_qty + "\n" + "Total: " + sub_order_item_total_amt + "\n");
                    v=textView;
                    return view;
                } catch (Exception ex) {
                    return view;
                }
            }
        };

        listview.setAdapter(arrayAdapter);
    }

    private void pdfdoc()
    {

        String pur_name=tname.getText().toString();
        String pur_price=tprice.getText().toString();
        String pur_quantity=tquantity.getText().toString();
        String pur_total=ttotal.getText().toString();

        String[] name_array=pur_name.split(",");
        String[] price_array=pur_price.split(",");
        String[] quantity_array=pur_quantity.split(",");
        String[] total_array=pur_total.split(",");
        //Arrays.sort(name_array,String.CASE_INSENSITIVE_ORDER);
        //Arrays.sort(price_array,String.CASE_INSENSITIVE_ORDER);
        //Arrays.sort(quantity_array,String.CASE_INSENSITIVE_ORDER);
        //Arrays.sort(total_array,String.CASE_INSENSITIVE_ORDER);


        for(String word:name_array)
        {
            Toast.makeText(ActivitySummary.this,word, Toast.LENGTH_SHORT).show();
            Log.v("Names: ",word);

        }
        //Toast.makeText(ActivitySummary.this, ""+table_no, Toast.LENGTH_SHORT).show();
        //arrayList_sub_orders_items.add(item_name + "#" + sub_order_item_price + "#" + sub_order_item_qty + "#" + sub_order_item_total_amt);


        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();
        Paint p=new Paint();
        Paint p1=new Paint();

        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();

        canvas.drawColor(Color.WHITE);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(70);
        canvas.drawText("Green Fox"+"\n", pageWidth / 2, 270, titlePaint);

        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.BLACK);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        p.setTextSize(50);
        canvas.drawText("Order Details"+"\n", pageWidth/2, 450,p);

        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.LEFT);
        p.setTextSize(50);
        //canvas.drawText("Sub Order ID: "+sub_order_id,80,550,p);
        canvas.drawText(table_no,80,610,p);


        p.setTextSize(40f);
        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        dateFormat=new SimpleDateFormat("dd/mm/yy");
        canvas.drawText("Date: "+dateFormat.format(dateobj),pageWidth-20,640,p);
        dateFormat=new SimpleDateFormat("HH:mm:ss");
        canvas.drawText("Time: "+dateFormat.format(dateobj),pageWidth-20,690,p);

        myPaint.setTextSize(40f);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(70,780,pageWidth-20,860,myPaint);

        myPaint.setColor(Color.BLACK);
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Item name ",80,830,myPaint);
        canvas.drawText("Quantity ",700,830,myPaint);
        canvas.drawText("Price ",870,830,myPaint);
        canvas.drawText("Total ",1000,830,myPaint);

        canvas.drawLine(690,790,690,840,myPaint);
        canvas.drawLine(860,790,860,840,myPaint);
        canvas.drawLine(990,790,990,840,myPaint);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTextSize(40f);


        //Toast.makeText(ActivitySummary.this, name_array.length, Toast.LENGTH_SHORT).show();

        for(int g=0;g<name_array.length;g++) {
            int x=80,y=950;
            for (String word : name_array) {

                //Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                //Log.v("Names: ",word);
                canvas.drawText(word.toString(), x, y, myPaint);
                // x=x+50;
                y=y+100;
            }
        }
        for(int g=0;g<quantity_array.length;g++) {
            int x=730,y=950;
            for (String word : quantity_array) {

                //Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                //Log.v("Names: ",word);
                canvas.drawText(word.toString(), x, y, myPaint);
                // x=x+50;
                y=y+100;
            }
        }
        for(int g=0;g<price_array.length;g++) {
            int x=870,y=950;
            for (String word : price_array) {

                // Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                //Log.v("Names: ",word);
                canvas.drawText(word.toString(), x, y, myPaint);
                //x=x+50;
                y=y+100;
            }
        }
        for(String word:quantity_array)
        {
            //Toast.makeText(ActivitySummary.this,word, Toast.LENGTH_SHORT).show();
            Log.v("Names: ",word);

        }



        for(String word:name_array)
        {
            for(int m=0;m<=word.length();m++) {

            }
            Log.v("Names: ",word);
            //canvas.drawText("Name of Item:      " + word, 800, 520, myPaint);

        }
        // canvas.drawText("Name of Item:      " + name_array.toString(), 800, 520, myPaint);
                                        /*canvas.drawText("Quantity of Item:  "+pur_quantity,470,600,myPaint);
                                        canvas.drawText("Price of Item:     "+pur_price,500,700,myPaint);
                                        canvas.drawText("Total Price:       "+pur_total, 485,800,myPaint);

                                         */

        myPdfDocument.finishPage(myPage1);
        File file=new File(Environment.getExternalStorageDirectory(),"Reciept.pdf");
        try
        {
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(ActivitySummary.this, "Done!! Receipt generated", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ActivitySummary.this, "Error in Generating Reciept" + e, Toast.LENGTH_SHORT).show();
        }

        //OpenPDF();
        myPdfDocument.close();
    }
    private void show_dialog_message(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }



    // Suborder items details


    //------------------------------------------------------- Suborder Details --------------------------------------------------------------->
    private void getsubordersItems_usingVolley(final String action, final String sub_order_id)
    {

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        //volley code
        dateobj=new Date();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROCESSING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("response", response);

                        try
                        {
                            //Log.v("response", response);
                            arrayList_sub_orders_items.clear();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                            String response_status = jsonObject_data.getString("response_status");
                            String response_msg = jsonObject_data.getString("response_msg");
                            JSONArray jsonArray_response_data = jsonObject_data.getJSONArray("response_data");


                            //Log.v("response", String.valueOf(jsonArray_response_data));

                            if(response_status.equals("success"))
                            {
                                for(int i = 0; i < jsonArray_response_data.length(); i++) {
                                    JSONObject jsonObject_suborder = jsonArray_response_data.getJSONObject(i);
                                    String item_name = jsonObject_suborder.getString("item_name");
                                    //String t_no=jsonObject_suborder.getString("")
                                    String sub_order_item_price = jsonObject_suborder.getString("sub_order_item_price");
                                    String sub_order_item_qty = jsonObject_suborder.getString("sub_order_item_qty");
                                    String sub_order_item_total_amt = jsonObject_suborder.getString("sub_order_item_total_amt");
                                    tname.append(item_name+",");
                                    tquantity.append(sub_order_item_qty+",");
                                    tprice.append(sub_order_item_price+",");
                                    ttotal.append(totel+",");



                                    String pur_name=tname.getText().toString();
                                    String pur_price=tprice.getText().toString();
                                    String pur_quantity=tquantity.getText().toString();
                                    String pur_total=ttotal.getText().toString();

                                    String[] name_array=pur_name.split(",");
                                    String[] price_array=pur_price.split(",");
                                    String[] quantity_array=pur_quantity.split(",");
                                    String[] total_array=pur_total.split(",");
                                    //Arrays.sort(name_array,String.CASE_INSENSITIVE_ORDER);
                                    //Arrays.sort(price_array,String.CASE_INSENSITIVE_ORDER);
                                    //Arrays.sort(quantity_array,String.CASE_INSENSITIVE_ORDER);
                                    //Arrays.sort(total_array,String.CASE_INSENSITIVE_ORDER);


                                    for(String word:name_array)
                                    {
                                        Toast.makeText(ActivitySummary.this,word, Toast.LENGTH_SHORT).show();
                                        Log.v("Names: ",word);

                                    }
                                    Toast.makeText(ActivitySummary.this, ""+table_no, Toast.LENGTH_SHORT).show();
                                    arrayList_sub_orders_items.add(item_name + "#" + sub_order_item_price + "#" + sub_order_item_qty + "#" + sub_order_item_total_amt);


                                    PdfDocument myPdfDocument = new PdfDocument();
                                    Paint myPaint = new Paint();
                                    Paint titlePaint = new Paint();
                                    Paint p=new Paint();
                                    Paint p1=new Paint();

                                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                                    Canvas canvas = myPage1.getCanvas();

                                    canvas.drawColor(Color.WHITE);

                                    titlePaint.setTextAlign(Paint.Align.CENTER);
                                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    titlePaint.setColor(Color.BLACK);
                                    titlePaint.setTextSize(70);
                                    canvas.drawText("Green Fox"+"\n", pageWidth / 2, 270, titlePaint);

                                    p.setTextAlign(Paint.Align.CENTER);
                                    p.setColor(Color.BLACK);
                                    p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
                                    p.setTextSize(50);
                                    canvas.drawText("Order Details"+"\n", pageWidth/2, 450,p);

                                    p.setColor(Color.BLACK);
                                    p.setTextAlign(Paint.Align.LEFT);
                                    p.setTextSize(50);
                                    canvas.drawText("Sub Order ID: "+sub_order_id,80,550,p);
                                    canvas.drawText(table_no,80,610,p);


                                    p.setTextSize(40f);
                                    p.setColor(Color.BLACK);
                                    p.setTextAlign(Paint.Align.RIGHT);
                                    p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    dateFormat=new SimpleDateFormat("dd/mm/yy");
                                    canvas.drawText("Date: "+dateFormat.format(dateobj),pageWidth-20,640,p);
                                    dateFormat=new SimpleDateFormat("HH:mm:ss");
                                    canvas.drawText("Time: "+dateFormat.format(dateobj),pageWidth-20,690,p);

                                    myPaint.setTextSize(40f);
                                    myPaint.setStyle(Paint.Style.STROKE);
                                    myPaint.setStrokeWidth(2);
                                    canvas.drawRect(70,780,pageWidth-20,860,myPaint);

                                    myPaint.setColor(Color.BLACK);
                                    myPaint.setTextAlign(Paint.Align.LEFT);
                                    myPaint.setStyle(Paint.Style.FILL);
                                    canvas.drawText("Item name ",80,830,myPaint);
                                    canvas.drawText("Quantity ",700,830,myPaint);
                                    canvas.drawText("Price ",870,830,myPaint);
                                    canvas.drawText("Total ",1000,830,myPaint);

                                    canvas.drawLine(690,790,690,840,myPaint);
                                    canvas.drawLine(860,790,860,840,myPaint);
                                    canvas.drawLine(990,790,990,840,myPaint);
                                    p.setTextAlign(Paint.Align.RIGHT);
                                    p.setTextSize(40f);


                                    //Toast.makeText(ActivitySummary.this, name_array.length, Toast.LENGTH_SHORT).show();

                                    for(int g=0;g<name_array.length;g++) {
                                        int x=80,y=950;
                                        for (String word : name_array) {

                                            //Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                                            //Log.v("Names: ",word);
                                            canvas.drawText(word.toString(), x, y, myPaint);
                                            // x=x+50;
                                            y=y+100;
                                        }
                                    }
                                    for(int g=0;g<quantity_array.length;g++) {
                                        int x=730,y=950;
                                        for (String word : quantity_array) {

                                            //Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                                            //Log.v("Names: ",word);
                                            canvas.drawText(word.toString(), x, y, myPaint);
                                            // x=x+50;
                                            y=y+100;
                                        }
                                    }
                                    for(int g=0;g<price_array.length;g++) {
                                        int x=870,y=950;
                                        for (String word : price_array) {

                                            // Toast.makeText(ActivitySummary.this, word, Toast.LENGTH_SHORT).show();
                                            //Log.v("Names: ",word);
                                            canvas.drawText(word.toString(), x, y, myPaint);
                                            //x=x+50;
                                            y=y+100;
                                        }
                                    }
                                    for(String word:quantity_array)
                                    {
                                        //Toast.makeText(ActivitySummary.this,word, Toast.LENGTH_SHORT).show();
                                        Log.v("Names: ",word);

                                    }
                                    for(int l=0;l<=jsonArray_response_data.length();l++)
                                    {
                                        //canvas.drawText();
                                        //canvas.drawText(name_array.toString(),20,950,myPaint);
                                        // canvas.drawText(sub_order_item_qty,730,950,myPaint);
                                        // canvas.drawText(sub_order_item_price,870,950,myPaint);
                                        canvas.drawText(totel,1030,950,myPaint);

                                    }


                                    for(String word:name_array)
                                    {
                                        for(int m=0;m<=word.length();m++) {

                                        }
                                        Log.v("Names: ",word);
                                        //canvas.drawText("Name of Item:      " + word, 800, 520, myPaint);

                                    }
                                    // canvas.drawText("Name of Item:      " + name_array.toString(), 800, 520, myPaint);
                                        /*canvas.drawText("Quantity of Item:  "+pur_quantity,470,600,myPaint);
                                        canvas.drawText("Price of Item:     "+pur_price,500,700,myPaint);
                                        canvas.drawText("Total Price:       "+pur_total, 485,800,myPaint);

                                         */

                                    myPdfDocument.finishPage(myPage1);
                                    File file=new File(Environment.getExternalStorageDirectory(),"Reciept.pdf");
                                    try
                                    {
                                        myPdfDocument.writeTo(new FileOutputStream(file));
                                        Toast.makeText(ActivitySummary.this, "Done!! Receipt generated", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ActivitySummary.this, "Error in Generating Access" + e, Toast.LENGTH_SHORT).show();
                                    }

                                    //OpenPDF();
                                    myPdfDocument.close();



/*
                                    PdfDocument myPdfDocument = new PdfDocument();
                                    Paint myPaint = new Paint();
                                    Paint titlePaint = new Paint();
                                    Paint p=new Paint();
                                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageI

                                    nfo1);
                                    Canvas canvas = myPage1.getCanvas();


                                    titlePaint.setTextAlign(Pa int.Align.CENTER);
                                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    titlePaint.setTextSize(40);
                                    canvas.drawText("Green Fox", pageWidth / 2, 270, titlePaint);

                                    p.setTextAlign(Paint.Align.CENTER);
                                    p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    p.setTextSize(40);
                                    canvas.drawText("Order Details", pageWidth-20, 270,p);


                                    myPaint.setColor(Color.rgb(0, 113, 188));
                                    myPaint.setTextSize(50f);
                                    myPaint.setTextAlign(Paint.Align.RIGHT);


                                    try {
                                        myPdfDocument.writeTo(new FileOutputStream(file));
                                        Toast.makeText(ActivitySummary.this, "Done!!", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ActivitySummary.this, "Nhe hoa Kaam" + e, Toast.LENGTH_SHORT).show();
                                    }

                                    myPdfDocument.close();*/

                                }
                                //Log.v("jsonObject_table1", String.valueOf(arrayList_sub_orders));
                            }
                            else
                            {
                                arrayList_sub_orders_items.add("No Sub Order Items");
                                //show_dialog_messa
                                // ge("Invalid Credentials", "No Sub Orders");
                                //Log.v("response", "Invalid Credentials");
                            }
                            showSuborderItemsDialog();
                        }
                        catch(Exception ex)
                        {
                            show_dialog_message("Network Error", "Check network connection");
                            Log.v("catch err", ex.getMessage());
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        show_dialog_message("Network Error", "Check network connection");
                        //Log.v("Network Error", error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("sub_order_id", sub_order_id);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    /*private void doPrint() {
        // Get a PrintManager instance

        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getActivity().getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(getActivity()),
                null); //
    }*/



    private void OpenPDF()
    {


        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Reciept.pdf";
        File f=new File(path);
        Intent intent=new Intent(Intent.ACTION_VIEW);
        String mimetype= MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
        intent.setDataAndType(Uri.fromFile(f),mimetype);
        Intent i1=Intent.createChooser(intent,"Open With");
        startActivity(i1);

    }
    private void showSuborderItemsDialog()
    {
        final android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(ActivitySummary.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_items_detail_layout, null);

        final ListView lv_suborder_items_detail = (ListView) mView.findViewById(R.id.lv_suborder_items_detail);

        set_suborders_items_in_custom_listview(lv_suborder_items_detail);

        mBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        mBuilder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        dialog.setMessage("Sub Order Detail");
        dialog.show();
    }

    private void getUpdatedSummary_usingVolley(final String action, final String table_id)
    {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        //volley code

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROCESSING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            //Log.v("response", response);

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                            String response_status = jsonObject_data.getString("response_status");
                            String response_msg = jsonObject_data.getString("response_msg");
                            JSONArray jsonArray_response_data = jsonObject_data.getJSONArray("response_data");

                            //Log.v("response", String.valueOf(jsonObject1.getString("table_name")));

                            if(response_status.equals("success"))
                            {
                                JSONObject jsonObject_table = jsonArray_response_data.getJSONObject(0);

                                String tbl_auto_id = jsonObject_table.optString("table_auto_id");
                                String table_order_id = jsonObject_table.optString("order_auto_id");

                                table_auto_id = tbl_auto_id;
                                table_order_auto_id = table_order_id;

                                tv_summary_starts_at.setText(jsonObject_table.optString("order_start_at").split(" ")[1]);
                                //tv_table_name_t1.setText(jsonObject_table.optString("table_name"));
                                tv_summary_sub_recepts_count_qty.setText(jsonObject_table.optString("sub_order_count") + " (" + jsonObject_table.optString("sub_order_items_count") + "/" + jsonObject_table.optString("sub_order_item_qty") +")");
                                tv_summary_status.setText(jsonObject_table.optString("table_status"));
                                if(jsonObject_table.optString("table_status").equals("Busy")) {
                                    tv_summary_status.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_summary_status.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_summary_curr_amt.setText(jsonObject_table.optString("order_amt"));

                                getsuborders_usingVolley("get_sub_orders_detail", table_id, table_order_id);
                            }
                            else
                            {
                                show_dialog_message("Invalid Credentials", "Login Failed, Email OR password incorrect");
                                //Log.v("response", "Invalid Credentials");
                            }
                        }
                        catch(Exception ex)
                        {
                            show_dialog_message("Network Error", "Check network connection");
                            Log.v("catch err", ex.getMessage());
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        show_dialog_message("Network Error", "Check network connection");
                        //Log.v("Network Error", error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("table_auto_id", table_auto_id);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CreatePdf(View view){
        Context context=ActivitySummary.this;
        PrintManager printManager=(PrintManager)ActivitySummary.this.getSystemService(context.PRINT_SERVICE);
        PrintDocumentAdapter adapter=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
        }
        String JobName=getString(R.string.app_name) +"Document";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            PrintJob printJob=printManager.print(JobName,adapter,new PrintAttributes.Builder().build());
        }


    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void clicked(View view)
    { //Button To start print
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name) + " Document";
        printManager.print(jobName, pda, null);
    }
    PrintDocumentAdapter pda = new PrintDocumentAdapter()
    {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback)
        {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(new File("/storage/emulated/0/Reciept.pdf"));
                output = new FileOutputStream(destination.getFileDescriptor());
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            }
            catch (Exception e) {

            } finally {
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras)
        {
            if (cancellationSignal.isCanceled())
            {
                callback.onLayoutCancelled();
                return;
            }

            //int pages = computePageCount(newAttributes);

            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("file_name.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
            callback.onLayoutFinished(pdi, true);
        }
    };
}
