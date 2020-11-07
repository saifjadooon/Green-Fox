package com.example.aptech.greenfox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class ActivitySplash extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        FirebaseApp.initializeApp(ActivitySplash.this);

        //initializing progress bar and value of it
        progressBar = (ProgressBar) findViewById(R.id.progressBar_splash);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        //function for incresing progress bar value using loop
        final Thread thread = new Thread(){
            @Override
            public void run() {
                try
                {
                    for (int i = 0; i < 100; i++)
                    {
                        progressBar.setProgress(i);
                        sleep(30);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    //opening main activity after completing progress bar value
                    Intent intent = new Intent(ActivitySplash.this,Portal.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();

    }
}
