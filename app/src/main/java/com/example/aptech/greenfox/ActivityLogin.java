package com.example.aptech.greenfox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    EditText et_email, et_pass;
    Button btn_login;
    ProgressDialog progressDialog;

    static Urls urls = new Urls();
    private static final String URL_PROCESSING = urls.url_processing();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        et_email = (EditText) findViewById(R.id.et_email);
        et_pass = (EditText) findViewById(R.id.et_pass);
        btn_login = (Button) findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //et_email.setText("abc@gmail.com");
        //et_pass.setText("admin");
    }

    private void login()
    {
        String email = et_email.getText().toString().trim();
        String pass = et_pass.getText().toString().trim();

        if(email.equals(""))
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pass.equals(""))
        {
            Toast.makeText(this, "Please enter pass", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!email.equals("") && !pass.equals(""))
        {
            login_usingVolley("login", email, pass);
            //Log.v("MD5", generateMD5(pass));
        }
    }

    private void login_usingVolley(final String action, final String email, final String pass)
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
                            Log.v("response", response);

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                            String response_status = jsonObject_data.getString("response_status");
                            String response_msg = jsonObject_data.getString("response_msg");
                            JSONObject jsonObject_response_data = jsonObject_data.getJSONObject("response_data");

                            Log.v("response", jsonObject_response_data.optString("acc_name"));

                            if(response_status.equals("success"))
                            {
                                String acc_auto_id = jsonObject_response_data.optString("acc_auto_id");
                                String acc_name = jsonObject_response_data.optString("acc_name");
                                String acc_email = jsonObject_response_data.optString("acc_email");
                                String acc_type = jsonObject_response_data.optString("acc_type");
                                String acc_status = jsonObject_response_data.optString("acc_status");
                                String created_at = jsonObject_response_data.optString("created_at");

                                if(acc_status.equals("enabled"))
                                {
                                    Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                                    intent.putExtra("acc_auto_id", acc_auto_id);
                                    intent.putExtra("acc_name", acc_name);
                                    intent.putExtra("acc_type", acc_type);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    show_dialog_message("Account Disabled", "Dear user, your account has been disabled. Please contact your administrator");
                                }
                            }
                            else
                            {
                                show_dialog_message("Invalid Credentials", "Login Failed, Email OR password incorrect");
                                //Log.v("response", "Invalid Credentials");
                            }
                        }
                        catch(Exception ex)
                        {
                            show_dialog_message("Network Error ", "Check network connection");
                            //Log.v("catch err", ex.getMessage());
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        show_dialog_message("Network Error", "Check network connection");
                        Log.v("Network Error", error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("email", email);
                params.put("pass", pass);

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

    private String generateMD5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
