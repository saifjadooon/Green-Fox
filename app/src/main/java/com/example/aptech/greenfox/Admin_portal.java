package com.example.aptech.greenfox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Admin_portal extends AppCompatActivity {

    EditText pass_admin,mail_Admin;
    Button btn_admin_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);
        mail_Admin=(EditText) findViewById(R.id.et_email_admin);
        pass_admin=(EditText) findViewById(R.id.et_pass_admin);

        btn_admin_login=(Button) findViewById(R.id.btn_login_admin);

        btn_admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail=mail_Admin.getText().toString();
                String password =pass_admin.getText().toString();
                if(mail.equals(""))
                {
                    Toast.makeText(Admin_portal.this, "Email and Password is required", Toast.LENGTH_SHORT).show();
                }
                else if(mail.equals("admin") && password.equals("admin"))
                {
                        Intent n=new Intent(getApplicationContext(),Insert_menu_items.class);
                        startActivity(n);
                }

                else
                    {
                        Toast.makeText(Admin_portal.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }
}
