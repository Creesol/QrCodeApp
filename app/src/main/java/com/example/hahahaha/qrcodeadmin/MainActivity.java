package com.example.hahahaha.qrcodeadmin;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void CallRead(View view) {

        Intent intent=new Intent(MainActivity.this,ReadData.class);
        startActivity(intent);
    }


    public void Statistics(View view) {
        Intent intent=new Intent(MainActivity.this,StatisticsMain.class);
        startActivity(intent);
    }
}