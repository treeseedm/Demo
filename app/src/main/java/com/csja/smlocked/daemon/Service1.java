package com.csja.smlocked.daemon;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.csja.smlocked.ConfigEntity;
import com.csja.smlocked.ConfigUtil;
import com.csja.smlocked.Constant;
import com.csja.smlocked.DateUtil;
import com.csja.smlocked.LockedWindow;
import com.csja.smlocked.SreenBroadCaseReceiver;
import com.csja.smlocked.log.MLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service {
    private final static int GRAY_SERVICE_ID = 1001;


    private long lockTimeIntervel = 2 * 60 * 1000;
    private long keepAliveIntevel = 5 * 60 * 1000;
    private static String TAG = "Service1";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Set<ConfigEntity> allConfig = ConfigUtil.config;
                Iterator<ConfigEntity> iterator = allConfig.iterator();
                boolean isNeedLocked = false;
                while (iterator.hasNext()) {
                    ConfigEntity configEntity = iterator.next();
                    if (DateUtil.isLockedTime(configEntity.startTime, configEntity.endTime)) {
                        isNeedLocked = true;
                        break;
                    }
                }
                if (LockedWindow.mLock != isNeedLocked) {
                    LockedWindow.show(getApplicationContext(), isNeedLocked);
                }
                handler.sendEmptyMessageDelayed(0, lockTimeIntervel);
                MLog.i(TAG, "isNeedLocked->" + isNeedLocked);
            } else {
                String studentInfo = Constant.getStudentInfo(getApplicationContext());
                MLog.i(TAG, "keepAppAlive->" + studentInfo);
                if (!TextUtils.isEmpty(studentInfo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(studentInfo);
                        String studentId = jsonObject.optString("studentId");
                        if (!TextUtils.isEmpty(studentId)) {
                            String path = "heart/keepAppAlive?studentId=" + URLEncoder.encode(studentId);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.HOST + path, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    MLog.i(TAG, response);
                                    handler.sendEmptyMessageDelayed(1, keepAliveIntevel);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    MLog.i(TAG, error.getMessage());
                                    handler.sendEmptyMessageDelayed(1, keepAliveIntevel);
                                }
                            });
                            MyApplication1.mRequestQueue.add(stringRequest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..

        Toast.makeText(getApplicationContext(), "server start", Toast.LENGTH_SHORT).show();
        MLog.i(getClass().getName(), "service onCreate");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new SreenBroadCaseReceiver(), filter);

        ConfigUtil.init(getApplicationContext(), ConfigUtil.CONFIGNAME);

        handler.sendEmptyMessageDelayed(0, 1000);


        handler.sendEmptyMessageDelayed(1, keepAliveIntevel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        return super.onStartCommand(intent, flags, startId);
    }

}
