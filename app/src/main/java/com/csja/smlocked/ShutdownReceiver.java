package com.csja.smlocked;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csja.smlocked.daemon.MyApplication1;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.volley.JsonObjectReqeustWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by mahaifeng on 17/4/29.
 */

public class ShutdownReceiver extends BroadcastReceiver {
    private static final String TAG = "ShutdownReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String info = Constant.getStudentInfo(context);
            JSONObject jsonObject;
            String studentId = "";
            try {
                jsonObject = new JSONObject(info);
                studentId = jsonObject.optString("studentId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MLog.i(TAG, "启动关闭中...");
            String path = "/heart/saveAppException?studentId=" + URLEncoder.encode("" + studentId) + "&time=" + URLEncoder.encode("" + System.currentTimeMillis()) + "&type=" + URLEncoder.encode("关机");
            JsonObjectReqeustWrapper jsonObjectRequest = new JsonObjectReqeustWrapper(JsonObjectRequest.Method.GET, path, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            MLog.i(TAG, "关机异常:" + response == null ? "" : response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MLog.e(TAG, error.getMessage(), error);
                }
            });
            MyApplication1.mRequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {

        }
    }
}
