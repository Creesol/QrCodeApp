package com.example.hahahaha.qrcodeadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class statistics extends AppCompatActivity {
    private TextView dailytext;
    private TextView weeklyText;
    private TextView monthlyText;
    private TextView allTimeText;
    private final String TAG="STATISTICS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dailytext=findViewById(R.id.DailyText);
        weeklyText=findViewById(R.id.WeeklyText);
        monthlyText=findViewById(R.id.MonthlyText);
        allTimeText=findViewById(R.id.AllTimeText);
        sendDataToServer.getData(getApplicationContext(), constant.getData, new getDataCallBack() {
            @Override
            public void OnSuccess(JSONArray data) {
                try {
                    Log.e(TAG, "OnSuccess: "+data );
                    dailytext.setText(data.getJSONObject(0).getString("total"));
                    weeklyText.setText(data.getJSONObject(1).getString("total"));
                    monthlyText.setText(data.getJSONObject(2).getString("total"));
                    allTimeText.setText(data.getJSONObject(3).getString("total"));
                    Log.e(TAG, "OnSuccess: "+data.getString(0) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnError(String Error) {

            }
        });

}
}

