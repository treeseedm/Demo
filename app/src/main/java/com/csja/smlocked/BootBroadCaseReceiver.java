package com.csja.smlocked;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csja.smlocked.daemon.MyApplication1;
import com.csja.smlocked.daemon.Service1;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.volley.JsonObjectReqeustWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by mahaifeng on 17/3/31.
 */

public class BootBroadCaseReceiver extends BroadcastReceiver {
    private String TAG = "BootBroadCaseReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "action=" + intent.getAction(), Toast.LENGTH_LONG).show();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(this.getClass().getName(), "开机自启动");


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
                MLog.i(TAG, "开机自启动...");
                String path = "user/switchPhone?studentId=" + URLEncoder.encode("" + studentId) + "&status=" + URLEncoder.encode("" + 0);
                JsonObjectReqeustWrapper jsonObjectRequest = new JsonObjectReqeustWrapper(JsonObjectRequest.Method.GET, path, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                MLog.i(TAG, "开机自启动:" + response == null ? "" : response.toString());
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
            context.startService(new Intent(context, Service1.class));
        }
    }
}
