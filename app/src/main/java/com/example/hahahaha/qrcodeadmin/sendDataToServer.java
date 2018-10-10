package com.example.hahahaha.qrcodeadmin;

import android.app.VoiceInteractor;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;
import static com.android.volley.VolleyLog.e;

public class sendDataToServer {
    public sendDataToServer() {
    }

    public static void sendsToServerFromExcel(Context context, String uri, JSONArray js, final serverDataCallback callback) {
        Log.e(TAG, "sendsToServerFromExcel: Called");
        JsonArrayRequest request3 = new JsonArrayRequest(Request.Method.POST, uri, js, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, "onResponse: send Successfully");

                callback.OnSuccess("Your Data has been send Successfully");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //callback.onError(error.getMessage());
                Log.d("faileder", "onResponsefail: " + error);
                callback.OnError("There is connection Error");
            }

        });
        request3.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        requestQueue.add(request3);
    }

    public static void getData(Context context, String uri, final getDataCallBack callBack) {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,uri,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callBack.OnSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.OnError("There is an Error");

            }

        });
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        requestQueue.add(jsonObjectRequest);


    }
}
