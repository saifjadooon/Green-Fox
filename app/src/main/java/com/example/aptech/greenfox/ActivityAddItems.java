package com.example.aptech.greenfox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddItems extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private Context context;
    private CardView cardview;
    private CardView.LayoutParams layoutparams;
    private TextView textview;
    private LinearLayout ll_select_category, ll_select_item;
    private ListView lv_order_review;
    private String[][] array_selected_items;
    private ArrayList arrayList_item_id;
    private ArrayList arrayList_item_name;
    private ArrayList arrayList_item_price;
    private ArrayList arrayList_item_qty;
    private ArrayList<String> arrayList_selected_items;
    private FloatingActionButton fab_add_items_done;
    private String table_no = "", acc_auto_id = "", table_auto_id = "", table_order_auto_id = "", f_item="", f_quantity="";

    ProgressDialog progressDialog;

    static Urls urls = new Urls();
    private static final String URL_PROCESSING = urls.url_processing();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        context = getApplicationContext();
        ll_select_category = (LinearLayout)findViewById(R.id.ll_select_category);
        ll_select_item = (LinearLayout)findViewById(R.id.ll_select_item);

        lv_order_review = (ListView) findViewById(R.id.lv_order_review);
        fab_add_items_done = (FloatingActionButton) findViewById(R.id.fab_add_items_done);
        Intent intent = getIntent();
        acc_auto_id = intent.getStringExtra("acc_auto_id");
        table_auto_id = intent.getStringExtra("table_auto_id");
        table_order_auto_id = intent.getStringExtra("table_order_auto_id");
       // f_item=intent.getStringExtra("f_item");
        //f_quantity=intent.getStringExtra("f_quantity");
        arrayList_item_id = new ArrayList();
        arrayList_item_name = new ArrayList();
        arrayList_item_price = new ArrayList();
        arrayList_item_qty = new ArrayList();
        arrayList_selected_items = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);


       /* Intent intent1 = new Intent(ActivityAddItems.this, ActivitySummary.class);
        intent1.putExtra("Item_name",f_item);
        intent1.putExtra("Item_quantity",f_quantity);
        startActivity(intent1);

*/

        getcategories_usingVolley("get_enabled_categories");

        //CreateCardViewsForCategory("1", "Breakfast", "https://i.imgur.com/AhvTjoX.png");
        //CreateCardViewsForItem("1", "HOUSE GRANOLA", "€", "6.00", "https://i.imgur.com/wuxvGX9.jpg");

        fab_add_items_done.setOnClickListener(this);

        lv_order_review.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {

                /*ArrayList<String> arrayList_custom_options = new ArrayList<>();
                arrayList_custom_options.add("Remove");
                //arrayList_custom_options.add("Edit Qty");
                showCustomOptionsDialog(arrayList_custom_options, i);*/
                new AlertDialog.Builder(ActivityAddItems.this)
                        .setTitle("Options")
                        .setMessage("What do you want?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Edit Qty", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String item_id = arrayList_selected_items.get(pos).split("#")[0];
                                String item_name = arrayList_selected_items.get(pos).split("#")[1];
                                f_item=item_name;
                                String item_price = arrayList_selected_items.get(pos).split("#")[2];
                                String item_exist_qty = arrayList_selected_items.get(pos).split("#")[3];
                                f_quantity=item_exist_qty;
                                showQtyDialogForEdit(item_id, item_name, item_price, item_exist_qty);
                                //Toast.makeText(context, item_id, Toast.LENGTH_SHORT).show();
                                //Log.v("selected items", String.valueOf(arrayList_selected_items));
                            }})
                        .setNegativeButton("Remove Item", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                arrayList_selected_items.remove(pos);
                                getItemsForOrderReview();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == fab_add_items_done)
        {
            if(arrayList_selected_items.size() > 0)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure want to generate sub-order?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                String selected_items_json_array = new Gson().toJson(arrayList_selected_items);
                                new_order_or_suborder_usingVolley("generate_order_suborder_logic", table_auto_id, table_order_auto_id, acc_auto_id, selected_items_json_array);

                            }})
                        .setNegativeButton("NO", null).show();
            }
            else
            {
                Toast.makeText(context, "No Item(s) Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getcategories_usingVolley(final String action)
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

                            //Log.v("response", String.valueOf(jsonArray_response_data));

                            if(response_status.equals("success"))
                            {
                                for(int i = 0; i < jsonArray_response_data.length(); i++)
                                {
                                    JSONObject jsonObject_suborder = jsonArray_response_data.getJSONObject(i);
                                    String category_auto_id = jsonObject_suborder.getString("category_auto_id");
                                    String category_name = jsonObject_suborder.getString("category_name");
                                    String category_image = jsonObject_suborder.getString("category_image");
                                    CreateCardViewsForCategory(category_auto_id, category_name, category_image);
                                }
                                //Log.v("jsonObject_table1", String.valueOf(arrayList_sub_orders));
                            }
                            else
                            {
                                show_dialog_message("No Categories", "No Enabled Categories");
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

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void getitems_usingVolley(final String action, final String category_id)
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

                            //Log.v("response", String.valueOf(jsonArray_response_data));

                            if(response_status.equals("success"))
                            {
                                for(int i = 0; i < jsonArray_response_data.length(); i++)
                                {
                                    JSONObject jsonObject_suborder = jsonArray_response_data.getJSONObject(i);
                                    String item_auto_id = jsonObject_suborder.optString("item_auto_id");
                                    String item_name = jsonObject_suborder.optString("item_name");
                                    String item_image = jsonObject_suborder.optString("item_image");
                                    String item_price = jsonObject_suborder.optString("item_price");
                                    CreateCardViewsForItem(item_auto_id, item_name, "€", item_price, item_image);
                                }
                                //Log.v("jsonObject_table1", String.valueOf(arrayList_sub_orders));
                            }
                            else
                            {
                                show_dialog_message("No Items", "No Enabled Items");
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
                params.put("category_id", category_id);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void CreateCardViewsForCategory(final String cat_id, String cat_name, String cat_img_url){

        cardview = new CardView(context);

        CardView.LayoutParams cardlayoutparams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                300
        );

        cardview.setLayoutParams(cardlayoutparams);
        cardview.setForegroundGravity(Gravity.CENTER);
        cardview.setRadius(50);
        cardview.setMinimumWidth(300);
        cardview.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        cardview.setContentPadding(25, 25, 25, 25);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cardview.getLayoutParams();
        layoutParams.setMargins(20, 0, 20, 0);
        cardview.requestLayout();
        cardview.setMaxCardElevation(15);
        cardview.setClickable(true);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, cat_id, Toast.LENGTH_SHORT).show();
                ll_select_item.removeAllViews();
                getitems_usingVolley("get_enabled_items", cat_id);
            }
        });

        ImageView iv_category_image = new ImageView(context);

        iv_category_image.setImageResource(R.drawable.coldrinkicon);
        iv_category_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180));
        //RelativeLayout.LayoutParams ivparams = (RelativeLayout.LayoutParams)iv_category_image.getLayoutParams();
        //iv_category_image.requestLayout();
        Glide.with(context).load(cat_img_url).into(iv_category_image);
        cardview.addView(iv_category_image);

        TextView tv_category_name = new TextView(context);

        tv_category_name.setText(cat_name);
        tv_category_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        //tv_category_name.setTextColor(Color.GRAY);
        tv_category_name.setTypeface(tv_category_name.getTypeface(), Typeface.BOLD);
        tv_category_name.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_category_name.setPadding(0,180,0,0);
        cardview.addView(tv_category_name);

        ll_select_category.addView(cardview);

    }

    private void CreateCardViewsForItem(final String item_id, final String item_name, final String item_currency, final String item_price, String cat_img_url){

        final CardView cardview = new CardView(context);

        CardView.LayoutParams cardlayoutparams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300
        );

        cardview.setLayoutParams(cardlayoutparams);
        cardview.setForegroundGravity(Gravity.CENTER);
        cardview.setRadius(50);
        cardview.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        //cardview.setContentPadding(25, 25, 25, 25);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cardview.getLayoutParams();
        layoutParams.setMargins(0, 20, 0, 20);
        cardview.requestLayout();
        cardview.setMaxCardElevation(15);
        cardview.setClickable(true);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, item_id, Toast.LENGTH_SHORT).show();
                showQtyDialog(item_id, item_name, item_price);
            }
        });

        ImageView iv_category_image = new ImageView(context);

        //iv_category_image.setImageResource(R.drawable.coldrinkicon);
        iv_category_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180));
        //RelativeLayout.LayoutParams ivparams = (RelativeLayout.LayoutParams)iv_category_image.getLayoutParams();
        //iv_category_image.requestLayout();
        //Glide.with(context).load(cat_img_url).into(iv_category_image);
        Glide.with(this).load(cat_img_url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cardview.setBackground(drawable);
                }
            }
        });
        cardview.addView(iv_category_image);

        TextView tv_item_name = new TextView(context);

        tv_item_name.setText(item_name);
        tv_item_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv_item_name.setTextColor(Color.WHITE);
        tv_item_name.setTypeface(tv_item_name.getTypeface(), Typeface.BOLD);
        //tv_category_name.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_item_name.setPadding(15,10,0,0);
        tv_item_name.setShadowLayer(2.5f, -2, 2, Color.BLACK);

        cardview.addView(tv_item_name);

        TextView tv_item_price = new TextView(context);

        tv_item_price.setText(item_currency + " " + item_price);
        tv_item_price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv_item_price.setTextColor(Color.WHITE);
        tv_item_price.setTypeface(tv_item_price.getTypeface(), Typeface.BOLD);
        //tv_category_name.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_item_price.setPadding(15,0,0,15);
        tv_item_price.setGravity(Gravity.BOTTOM);
        tv_item_price.setShadowLayer(2.5f, -2, 2, Color.BLACK);

        cardview.addView(tv_item_price);

        ll_select_item.addView(cardview);

    }

    private void showQtyDialog(final String item_id, final String item_name, final String item_price)
    {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivityAddItems.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_qty_layout, null);

        final EditText et_qty = (EditText) mView.findViewById(R.id.et_qty);
        final Button btnDec = (Button) mView.findViewById(R.id.btn_dec_qty);
        final Button btnInc = (Button) mView.findViewById(R.id.btn_inc_qty);
        final Button btnAdd = (Button) mView.findViewById(R.id.btn_add_qty);
        btnAdd.setVisibility(View.INVISIBLE);

        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());
                if(qty > 1)
                {
                    qty--;
                    et_qty.setText(String.valueOf(qty));
                }
            }
        });

        btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());
                qty++;
                et_qty.setText(String.valueOf(qty));
            }
        });

        mBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());

                boolean item_exist = false;
                for(int count = 0; count < arrayList_selected_items.size(); count++)
                {
                    String id = arrayList_selected_items.get(count).split("#")[0];
                    String name = arrayList_selected_items.get(count).split("#")[1];
                    String price = arrayList_selected_items.get(count).split("#")[2];
                    String iqty = arrayList_selected_items.get(count).split("#")[3];
                    if(id.equals(item_id))
                    {
                        item_exist = true;
                        int new_qty = Integer.parseInt(iqty) + qty;
                        double total_price = new_qty * Double.parseDouble(price);
                        arrayList_selected_items.set(count, id + "#" + name + "#" + price + "#" + new_qty + "#" + total_price);
                        break;
                    }
                }
                if(item_exist == false)
                {
                    double total_price = qty * Double.parseDouble(item_price);
                    arrayList_selected_items.add(item_id + "#" + item_name + "#" + item_price + "#" + qty + "#" + total_price);
                }
                //array_selected_items = new String[][] {}

                //Log.v("array selected items", String.valueOf(arrayList_selected_items));
                getItemsForOrderReview();
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setMessage(item_name);
        dialog.show();
    }

    private void showQtyDialogForEdit(final String item_id, final String item_name, final String item_price, final String qty_exist)
    {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivityAddItems.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_qty_layout, null);

        final EditText et_qty = (EditText) mView.findViewById(R.id.et_qty);
        final Button btnDec = (Button) mView.findViewById(R.id.btn_dec_qty);
        final Button btnInc = (Button) mView.findViewById(R.id.btn_inc_qty);
        final Button btnAdd = (Button) mView.findViewById(R.id.btn_add_qty);
        btnAdd.setVisibility(View.INVISIBLE);

        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());
                if(qty > 1)
                {
                    qty--;
                    et_qty.setText(String.valueOf(qty));
                }
            }
        });

        btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());
                qty++;
                et_qty.setText(String.valueOf(qty));
            }
        });

        et_qty.setText(qty_exist);

        mBuilder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int qty = Integer.parseInt(et_qty.getText().toString().trim());

                boolean item_exist = false;
                for(int count = 0; count < arrayList_selected_items.size(); count++)
                {
                    String id = arrayList_selected_items.get(count).split("#")[0];
                    String name = arrayList_selected_items.get(count).split("#")[1];
                    String price = arrayList_selected_items.get(count).split("#")[2];
                    String iqty = arrayList_selected_items.get(count).split("#")[3];
                    if(id.equals(item_id))
                    {
                        item_exist = true;
                        int new_qty = qty;
                        double total_price = new_qty * Double.parseDouble(price);
                        arrayList_selected_items.set(count, id + "#" + name + "#" + price + "#" + new_qty + "#" + total_price);
                        break;
                    }
                }
                if(item_exist == false)
                {
                    double total_price = qty * Double.parseDouble(item_price);
                    arrayList_selected_items.add(item_id + "#" + item_name + "#" + item_price + "#" + qty + "#" + total_price);
                }
                //array_selected_items = new String[][] {}

                //Log.v("array selected items", String.valueOf(arrayList_selected_items));
                getItemsForOrderReview();
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setMessage(item_name);
        dialog.show();
    }

    private void getItemsForOrderReview()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList_selected_items)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //textView.setBackgroundColor(Color.GREEN);

                String item_name = textView.getText().toString().split("#")[1];
                String item_price = textView.getText().toString().split("#")[2];
                String item_qty = textView.getText().toString().split("#")[3];
                String item_total_price = textView.getText().toString().split("#")[4];
                textView.setText("\n" + item_name + "\n" + "Price: " + item_price + "\n" + "Qty: " + item_qty + "\n" + "Total: " + item_total_price + "\n");
                return view;
            }
        };

        lv_order_review.setAdapter(arrayAdapter);
    }

    private void show_dialog_message(String title, String msg)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void new_order_or_suborder_usingVolley(final String action, final String table_id, final String order_auto_id, final String acc_id, final String selected_items_json_array)
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

                            Log.v("response", String.valueOf(jsonArray_response_data));

                            if(response_status.equals("success"))
                            {

                                Toast.makeText(context, response_msg, Toast.LENGTH_SHORT).show();
                                //Log.v("jsonObject_table1", String.valueOf(arrayList_sub_orders));
                            }
                            else
                            {
                                show_dialog_message("No Categories", "No Enabled Categories");
                                //Log.v("response", "Invalid Credentials");
                            }
                            finish();
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
                        //Log.v("Network Error", error.networkResponse.data.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("table_auto_id", table_id);
                params.put("order_auto_id", order_auto_id);
                params.put("acc_auto_id", acc_id);
                params.put("f_item",f_item);
                params.put("f_quantity",f_quantity);
                params.put("selected_items_json_array", selected_items_json_array);

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void showCustomOptionsDialog(ArrayList arrayList_options, final int item_position)
    {
        final android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(ActivityAddItems.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_list_options_layout, null);

        final ListView lv_custom_options = (ListView) mView.findViewById(R.id.lv_custom_options);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList_options)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //textView.setBackgroundColor(Color.GREEN);

                return view;
            }
        };

        lv_custom_options.setAdapter(arrayAdapter);

        lv_custom_options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(context, String.valueOf(i), Toast.LENGTH_SHORT).show();
                arrayList_selected_items.remove(item_position);
                getItemsForOrderReview();
            }
        });

        /*mBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });*/
        mBuilder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        //dialog.setMessage("Options");
        dialog.show();
    }
}
