package com.example.hahahaha.qrcodeadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StatisticsMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_main);
    }

    public void DataByDate(View view) {
        Intent intent=new Intent(StatisticsMain.this,statistics.class);
        startActivity(intent);
    }

    public void fakeReal(View view) {
        Intent intent=new Intent(StatisticsMain.this,fakeReal.class);
        startActivity(intent);
    }

    public void timesChecked(View view) {
        Intent intent=new Intent(StatisticsMain.this,timesChecked.class);
        startActivity(intent);
    }

    public void graphs(View view) {
    }
}
