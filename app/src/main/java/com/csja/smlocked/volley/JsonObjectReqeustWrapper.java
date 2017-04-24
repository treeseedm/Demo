package com.csja.smlocked.volley;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csja.smlocked.Constant;
import com.csja.smlocked.log.MLog;

import org.json.JSONObject;

/**
 * Created by mahaifeng on 17/4/18.
 */

public class JsonObjectReqeustWrapper extends JsonObjectRequest {

    public JsonObjectReqeustWrapper(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, Constant.HOST + url, jsonRequest, listener, errorListener);
        MLog.i(getClass().getSimpleName(), "url>" + url);
    }
}
