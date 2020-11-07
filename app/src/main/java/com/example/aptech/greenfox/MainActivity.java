package com.example.aptech.greenfox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView cardView_table1;
    private CardView cardView_table2;
    private CardView cardView_table3;
    private CardView cardView_table4;
    private CardView cardView_table5;
    private CardView cardView_table6;
    private TextView tv_acc_name;
    private TextView tv_acc_type;
    private TextView tv_table_name_t1, tv_table_name_t2, tv_table_name_t3, tv_table_name_t4, tv_table_name_t5, tv_table_name_t6;
    private TextView tv_table_qty_t1, tv_table_qty_t2, tv_table_qty_t3, tv_table_qty_t4, tv_table_qty_t5, tv_table_qty_t6;
    private TextView tv_table_status_t1, tv_table_status_t2, tv_table_status_t3, tv_table_status_t4, tv_table_status_t5, tv_table_status_t6;
    private TextView tv_table_amt_t1, tv_table_amt_t2, tv_table_amt_t3, tv_table_amt_t4, tv_table_amt_t5, tv_table_amt_t6;
    private String acc_auto_id = "";

    private String table1_auto_id = "", table2_auto_id = "", table3_auto_id = "", table4_auto_id = "", table5_auto_id = "", table6_auto_id = "";
    private String table1_order_auto_id = "", table2_order_auto_id = "", table3_order_auto_id = "", table4_order_auto_id = "", table5_order_auto_id = "", table6_order_auto_id = "";
    private String table1_order_start_at = "", table2_order_start_at = "", table3_order_start_at = "", table4_order_start_at = "", table5_order_start_at = "", table6_order_start_at = "";
    private String table1_item_name="",table2_item_name="",table3_item_name="",table4_item_name="",table5_item_name="",table6_item_name="";
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout main_activity_refresh_layout;

    static Urls urls = new Urls();
    private static final String URL_PROCESSING = urls.url_processing();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("GreenFox - Select Table");
        cardView_table1 = (CardView) findViewById(R.id.card_view_table1);
        cardView_table2 = (CardView) findViewById(R.id.card_view_table2);
        cardView_table3 = (CardView) findViewById(R.id.card_view_table3);
        cardView_table4 = (CardView) findViewById(R.id.card_view_table4);
        cardView_table5 = (CardView) findViewById(R.id.card_view_table5);
        cardView_table6 = (CardView) findViewById(R.id.card_view_table6);
        tv_acc_name = (TextView) findViewById(R.id.tv_acc_name);
        tv_acc_type = (TextView) findViewById(R.id.tv_acc_type);

        tv_table_name_t1 = (TextView) findViewById(R.id.tv_table_name_t1);
        tv_table_name_t2 = (TextView) findViewById(R.id.tv_table_name_t2);
        tv_table_name_t3 = (TextView) findViewById(R.id.tv_table_name_t3);
        tv_table_name_t4 = (TextView) findViewById(R.id.tv_table_name_t4);
        tv_table_name_t5 = (TextView) findViewById(R.id.tv_table_name_t5);
        tv_table_name_t6 = (TextView) findViewById(R.id.tv_table_name_t6);
        tv_table_qty_t1 = (TextView) findViewById(R.id.tv_table_qty_t1);
        tv_table_qty_t2 = (TextView) findViewById(R.id.tv_table_qty_t2);
        tv_table_qty_t3 = (TextView) findViewById(R.id.tv_table_qty_t3);
        tv_table_qty_t4 = (TextView) findViewById(R.id.tv_table_qty_t4);
        tv_table_qty_t5 = (TextView) findViewById(R.id.tv_table_qty_t5);
        tv_table_qty_t6 = (TextView) findViewById(R.id.tv_table_qty_t6);
        tv_table_status_t1 = (TextView) findViewById(R.id.tv_table_status_t1);
        tv_table_status_t2 = (TextView) findViewById(R.id.tv_table_status_t2);
        tv_table_status_t3 = (TextView) findViewById(R.id.tv_table_status_t3);
        tv_table_status_t4 = (TextView) findViewById(R.id.tv_table_status_t4);
        tv_table_status_t5 = (TextView) findViewById(R.id.tv_table_status_t5);
        tv_table_status_t6 = (TextView) findViewById(R.id.tv_table_status_t6);
        tv_table_amt_t1 = (TextView) findViewById(R.id.tv_table_amt_t1);
        tv_table_amt_t2 = (TextView) findViewById(R.id.tv_table_amt_t2);
        tv_table_amt_t3 = (TextView) findViewById(R.id.tv_table_amt_t3);
        tv_table_amt_t4 = (TextView) findViewById(R.id.tv_table_amt_t4);
        tv_table_amt_t5 = (TextView) findViewById(R.id.tv_table_amt_t5);
        tv_table_amt_t6 = (TextView) findViewById(R.id.tv_table_amt_t6);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        main_activity_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.main_activity_refresh_layout);

        Intent intent = getIntent();
        acc_auto_id = intent.getStringExtra("acc_auto_id");
        tv_acc_name.setText(intent.getStringExtra("acc_name").toUpperCase());
        tv_acc_type.setText(intent.getStringExtra("acc_type").toUpperCase());

        cardView_table1.setOnClickListener(this);
        cardView_table2.setOnClickListener(this);
        cardView_table3.setOnClickListener(this);
        cardView_table4.setOnClickListener(this);
        cardView_table5.setOnClickListener(this);
        cardView_table6.setOnClickListener(this);

        getTablesInfo_usingVolley("get_tables_details");

        main_activity_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                main_activity_refresh_layout.setRefreshing(false);
                getTablesInfo_usingVolley("get_tables_details");
                //Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTablesInfo_usingVolley("get_tables_details");
    }

    @Override
    public void onClick(View view) {
        if(view == cardView_table1)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 1");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table1_auto_id);
            intent.putExtra("table_order_auto_id", table1_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t1.getText());
            intent.putExtra("status", tv_table_status_t1.getText());
            intent.putExtra("start_at", table1_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t1.getText());
            startActivity(intent);


        }
        else if(view == cardView_table2)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 2");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table2_auto_id);
            intent.putExtra("table_order_auto_id", table2_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t2.getText());
            intent.putExtra("status", tv_table_status_t2.getText());
            intent.putExtra("start_at", table2_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t2.getText());
            startActivity(intent);


        }
        else if(view == cardView_table3)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 3");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table3_auto_id);
            intent.putExtra("table_order_auto_id", table3_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t3.getText());
            intent.putExtra("status", tv_table_status_t3.getText());
            intent.putExtra("start_at", table3_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t3.getText());
            startActivity(intent);


        }
        else if(view == cardView_table4)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 4");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table4_auto_id);
            intent.putExtra("table_order_auto_id", table4_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t4.getText());
            intent.putExtra("status", tv_table_status_t4.getText());
            intent.putExtra("start_at", table4_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t4.getText());
            startActivity(intent);

        }
        else if(view == cardView_table5)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 5");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table5_auto_id);
            intent.putExtra("table_order_auto_id", table5_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t5.getText());
            intent.putExtra("status", tv_table_status_t5.getText());
            intent.putExtra("start_at", table5_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t5.getText());
            startActivity(intent);
        }
        else if(view == cardView_table6)
        {
            Intent intent = new Intent(MainActivity.this, ActivitySummary.class);
            intent.putExtra("table_no", "Table 6");
            intent.putExtra("acc_auto_id", acc_auto_id);
            intent.putExtra("table_auto_id", table6_auto_id);
            intent.putExtra("table_order_auto_id", table6_order_auto_id);
            intent.putExtra("sub_recepts_count_qty", tv_table_qty_t6.getText());
            intent.putExtra("status", tv_table_status_t6.getText());
            intent.putExtra("start_at", table6_order_start_at);
            intent.putExtra("curr_amt", tv_table_amt_t6.getText());
            startActivity(intent);
        }
    }

    private void getTablesInfo_usingVolley(final String action)
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
                                JSONObject jsonObject_table1 = jsonArray_response_data.getJSONObject(0);
                                JSONObject jsonObject_table2 = jsonArray_response_data.getJSONObject(1);
                                JSONObject jsonObject_table3 = jsonArray_response_data.getJSONObject(2);
                                JSONObject jsonObject_table4 = jsonArray_response_data.getJSONObject(3);
                                JSONObject jsonObject_table5 = jsonArray_response_data.getJSONObject(4);
                                JSONObject jsonObject_table6 = jsonArray_response_data.getJSONObject(5);

                                table1_auto_id = jsonObject_table1.optString("table_auto_id");
                                table2_auto_id = jsonObject_table2.optString("table_auto_id");
                                table3_auto_id = jsonObject_table3.optString("table_auto_id");
                                table4_auto_id = jsonObject_table4.optString("table_auto_id");
                                table5_auto_id = jsonObject_table5.optString("table_auto_id");
                                table6_auto_id = jsonObject_table6.optString("table_auto_id");

                                table1_order_auto_id = jsonObject_table1.optString("order_auto_id");
                                table2_order_auto_id = jsonObject_table2.optString("order_auto_id");
                                table3_order_auto_id = jsonObject_table3.optString("order_auto_id");
                                table4_order_auto_id = jsonObject_table4.optString("order_auto_id");
                                table5_order_auto_id = jsonObject_table5.optString("order_auto_id");
                                table6_order_auto_id = jsonObject_table6.optString("order_auto_id");

                                table1_order_start_at = jsonObject_table1.optString("order_start_at");
                                table2_order_start_at = jsonObject_table2.optString("order_start_at");
                                table3_order_start_at = jsonObject_table3.optString("order_start_at");
                                table4_order_start_at = jsonObject_table4.optString("order_start_at");
                                table5_order_start_at = jsonObject_table5.optString("order_start_at");
                                table6_order_start_at = jsonObject_table6.optString("order_start_at");

                                // table 1
                                tv_table_name_t1.setText(jsonObject_table1.optString("table_name"));
                                tv_table_qty_t1.setText(jsonObject_table1.optString("sub_order_count") + " (" + jsonObject_table1.optString("sub_order_items_count") + "/" + jsonObject_table1.optString("sub_order_item_qty") +")");
                                tv_table_status_t1.setText(jsonObject_table1.optString("table_status"));
                                if(jsonObject_table1.optString("table_status").equals("Busy")) {
                                    tv_table_status_t1.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t1.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t1.setText(jsonObject_table1.optString("order_amt"));

                                // table 2
                                tv_table_name_t2.setText(jsonObject_table2.optString("table_name"));
                                tv_table_qty_t2.setText(jsonObject_table2.optString("sub_order_count") + " (" + jsonObject_table2.optString("sub_order_items_count") + "/" + jsonObject_table2.optString("sub_order_item_qty") +")");
                                tv_table_status_t2.setText(jsonObject_table2.optString("table_status"));
                                if(jsonObject_table2.optString("table_status").equals("Busy")) {
                                    tv_table_status_t2.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t2.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t2.setText(jsonObject_table2.optString("order_amt"));

                                // table 3
                                tv_table_name_t3.setText(jsonObject_table3.optString("table_name"));
                                tv_table_qty_t3.setText(jsonObject_table3.optString("sub_order_count") + " (" + jsonObject_table3.optString("sub_order_items_count") + "/" + jsonObject_table3.optString("sub_order_item_qty") +")");
                                tv_table_status_t3.setText(jsonObject_table3.optString("table_status"));
                                if(jsonObject_table3.optString("table_status").equals("Busy")) {
                                    tv_table_status_t3.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t3.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t3.setText(jsonObject_table3.optString("order_amt"));

                                // table 4
                                tv_table_name_t4.setText(jsonObject_table4.optString("table_name"));
                                tv_table_qty_t4.setText(jsonObject_table4.optString("sub_order_count") + " (" + jsonObject_table4.optString("sub_order_items_count") + "/" + jsonObject_table4.optString("sub_order_item_qty") +")");
                                tv_table_status_t4.setText(jsonObject_table4.optString("table_status"));
                                if(jsonObject_table4.optString("table_status").equals("Busy")) {
                                    tv_table_status_t4.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t4.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t4.setText(jsonObject_table4.optString("order_amt"));

                                // table 5
                                tv_table_name_t5.setText(jsonObject_table5.optString("table_name"));
                                tv_table_qty_t5.setText(jsonObject_table5.optString("sub_order_count") + " (" + jsonObject_table5.optString("sub_order_items_count") + "/" + jsonObject_table5.optString("sub_order_item_qty") +")");
                                tv_table_status_t5.setText(jsonObject_table5.optString("table_status"));
                                if(jsonObject_table5.optString("table_status").equals("Busy")) {
                                    tv_table_status_t5.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t5.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t5.setText(jsonObject_table5.optString("order_amt"));

                                // table 6
                                tv_table_name_t6.setText(jsonObject_table6.optString("table_name"));
                                tv_table_qty_t6.setText(jsonObject_table6.optString("sub_order_count") + " (" + jsonObject_table6.optString("sub_order_items_count") + "/" + jsonObject_table6.optString("sub_order_item_qty") +")");
                                tv_table_status_t6.setText(jsonObject_table6.optString("table_status"));
                                if(jsonObject_table6.optString("table_status").equals("Busy")) {
                                    tv_table_status_t6.setTextColor(Color.parseColor("#800000"));
                                }
                                else{
                                    tv_table_status_t6.setTextColor(Color.parseColor("#006400"));
                                }
                                tv_table_amt_t6.setText(jsonObject_table6.optString("order_amt"));

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

                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void show_dialog_message(String title, String msg)
    {
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure want to close app? You will be logout")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.exit(0);
                    }})
                .setNegativeButton("NO", null).show();
    }
}
