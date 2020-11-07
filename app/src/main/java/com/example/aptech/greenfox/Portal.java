package com.example.aptech.greenfox;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Portal extends AppCompatActivity {

    Button btn_admin,btn_waiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        btn_admin=(Button) findViewById(R.id.btn_login_as_admin1);
        btn_waiter=(Button) findViewById(R.id.btn_login_as_waiter);

        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Admin_portal.class);
                startActivity(i);
            }
        });

        btn_waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ActivityLogin.class);
                startActivity(i);

            }
        });
    }
}
