package com.example.hahahaha.qrcodeadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class timesChecked extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_times_checked);
        recyclerView=findViewById(R.id.checked);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        sendDataToServer.getData(getApplicationContext(), constant.checkedMoreThenOnce, new getDataCallBack() {
            @Override
            public void OnSuccess(JSONArray data) {
                List<XYValue> list = new ArrayList<>();
                try {
                for(int i=0;i<data.length();i++){

                        String times=data.getJSONObject(i).getString("macAddr");
                        String qr_code=data.getJSONObject(i).getString("_product_qr_code");
                        list.add(new XYValue(qr_code,times));
                    }
                    adapter=new orderAdapter1(getApplicationContext(),list);
                    recyclerView.setAdapter(adapter);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnError(String Error) {

            }
        });
    }
}
