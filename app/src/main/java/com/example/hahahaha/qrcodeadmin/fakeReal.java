package com.example.hahahaha.qrcodeadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fakeReal extends AppCompatActivity {
    private TextView dailytext;
    private TextView weeklyText;
    private TextView monthlyText;
    private TextView allTimeText;
    private final String TAG="STATISTICS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_real);
        dailytext=findViewById(R.id.RealText);
        weeklyText=findViewById(R.id.FakeText);

        allTimeText=findViewById(R.id.TotalText);
        sendDataToServer.getData(getApplicationContext(), constant.FakeRealStats, new getDataCallBack() {
            @Override
            public void OnSuccess(JSONArray data) {
                try {
                    Log.e(TAG, "OnSuccess: "+data );
                    dailytext.setText(data.getJSONObject(0).getString("fake"));
                    weeklyText.setText(data.getJSONObject(0).getString("real"));

                    allTimeText.setText(data.getJSONObject(0).getString("total"));
                    Log.e(TAG, "OnSuccess: "+data.getJSONObject(0) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnError(String Error) {
                Toast.makeText(fakeReal.this, "Error connecting to server", Toast.LENGTH_SHORT).show();

            }
        });

    }
}

