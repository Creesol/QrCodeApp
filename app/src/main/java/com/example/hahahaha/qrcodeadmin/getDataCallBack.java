package com.example.hahahaha.qrcodeadmin;

import org.json.JSONArray;
import org.json.JSONObject;

public interface getDataCallBack {
    void OnSuccess(JSONArray data);
    void OnError(String Error);
}
