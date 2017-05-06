package com.csja.smlocked;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.csja.smlocked.daemon.MyApplication1;
import com.csja.smlocked.daemon.Service1;
import com.csja.smlocked.entity.TimeConfigResponse;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.volley.JsonObjectReqeustWrapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import static android.app.AlarmManager.RTC_WAKEUP;

/**
 * Created by mahaifeng on 17/4/21.
 */

public class NotifyMessageController {
    private static String TAG = "NotifyMessageController";
    public static long keepAliveIntevel = 5 * 60 * 1000;
    public static long lockTimeIntervel = 30 * 1000;
    public static long UPDATETIMELOCKED = 12*60*60 * 1000;

    public static void bindParent(final Context context, String t,
                                  String description, String customContentString) throws JSONException {
        JSONObject jsonObject = new JSONObject(customContentString);
        final long studentParentId = jsonObject.optLong("studentParentId");


        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        final View view = View.inflate(context, R.layout.layout_parent_bind, null);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        lp.gravity = Gravity.CENTER;
        lp.height = DeviceUtil.dip2px(context, 200);
        lp.width = context.getResources().getDisplayMetrics().widthPixels / 4 * 3;

        windowManager.addView(view, lp);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button ok = (Button) view.findViewById(R.id.ok);

        title.setText(t);
        content.setText(description);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindRes(context, 1, studentParentId);
                view.setVisibility(View.GONE);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindRes(context, 0, studentParentId);
                view.setVisibility(View.GONE);
            }
        });

    }

    private static void bindRes(final Context context, int res, long studentParentId) {
        String path = "user/bindParent" + "?studentParentId=" + URLEncoder.encode("" + studentParentId) + "&result=" + URLEncoder.encode("" + res);
        JsonObjectReqeustWrapper jsonObjectReqeustWrapper = new JsonObjectReqeustWrapper(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    MLog.i(TAG, response.toString());
                    Toast.makeText(context, response.optString("text"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication1.mRequestQueue.add(jsonObjectReqeustWrapper);
    }

    public static void bindTimeConfig(Context context, String message) {
        Gson gson = new Gson();
        TimeConfigResponse timeConfigResponse = gson.fromJson(message, TimeConfigResponse.class);
        MLog.i(TAG, message);
        if (Constant.SUCCESS.equals(timeConfigResponse.code)) {
            ConfigUtil.clearTime(context);
            List<ConfigEntity> configEntityList = timeConfigResponse.lockTime;
            for (ConfigEntity configEntity : configEntityList) {
                ConfigUtil.addTime(context, configEntity.startTime, configEntity.endTime);
            }
            if (timeConfigResponse.motto != null) {
                Constant.saveMotto(context, timeConfigResponse.motto.creator, timeConfigResponse.motto.content);
            }
        }

    }

    public static void startTimeAlarm(Context context, int what, long intevel) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
        Intent intent = new Intent(context,
                Service1.class);
        intent.putExtra("what", what);
        final PendingIntent pi = PendingIntent.getService(
                context, what, intent, 0);
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.setRepeating(RTC_WAKEUP, 0, intevel, pi);
        } else {
            alarmManager.setWindow(RTC_WAKEUP, System.currentTimeMillis() + intevel, 0, pi);
        }
    }

}
