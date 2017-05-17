package com.csja.smlocked.daemon;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
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
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.csja.smlocked.ConfigEntity;
import com.csja.smlocked.ConfigUtil;
import com.csja.smlocked.Constant;
import com.csja.smlocked.DateUtil;
import com.csja.smlocked.LockedWindow;
import com.csja.smlocked.NotifyMessageController;
import com.csja.smlocked.R;
import com.csja.smlocked.SreenBroadCaseReceiver;
import com.csja.smlocked.StudentInfoActivity;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.push.example.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import static android.app.AlarmManager.RTC_WAKEUP;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service {
    private final static int GRAY_SERVICE_ID = 1001;
    private static String TAG = "Service1";

    private static Notification notification;

    private static long LASTLOCKTIME = 0;
    private static long LASTKEEPALIVE = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            processLockTime(msg.what);
            if (LASTLOCKTIME != 0) {
                if (System.currentTimeMillis() - LASTLOCKTIME > NotifyMessageController.lockTimeIntervel) {
                    MLog.i(TAG, "alarmmanager 死亡lockTime");
                    NotifyMessageController.startTimeAlarm(getApplicationContext(), 0, NotifyMessageController.lockTimeIntervel);
                }
            }

            if (LASTKEEPALIVE != 0) {
                if (System.currentTimeMillis() - LASTKEEPALIVE > NotifyMessageController.keepAliveIntevel) {
                    MLog.i(TAG, "alarmmanager 死亡keepAlive");
                    NotifyMessageController.startTimeAlarm(getApplicationContext(), 1, NotifyMessageController.keepAliveIntevel);
                }
            }
            handler.sendEmptyMessageDelayed(msg.what, NotifyMessageController.lockTimeIntervel);
//            if (msg.what == 0) {
//                Set<ConfigEntity> allConfig = ConfigUtil.config;
//                Iterator<ConfigEntity> iterator = allConfig.iterator();
//                boolean isNeedLocked = false;
//                while (iterator.hasNext()) {
//                    ConfigEntity configEntity = iterator.next();
//                    if (DateUtil.isLockedTime(configEntity.startTime, configEntity.endTime)) {
//                        isNeedLocked = true;
//                        break;
//                    }
//                }
//                if (LockedWindow.mLock != isNeedLocked) {
//                    LockedWindow.show(getApplicationContext(), isNeedLocked);
//                }
//                handler.sendEmptyMessageDelayed(0, lockTimeIntervel);
//                MLog.i(TAG, "isNeedLocked->" + isNeedLocked);
//            } else {
//                String studentInfo = Constant.getStudentInfo(getApplicationContext());
//                MLog.i(TAG, "keepAppAlive->" + studentInfo);
//                if (!TextUtils.isEmpty(studentInfo)) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(studentInfo);
//                        String studentId = jsonObject.optString("studentId");
//                        if (!TextUtils.isEmpty(studentId)) {
//                            String path = "heart/keepAppAlive?studentId=" + URLEncoder.encode(studentId);
//                            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.HOST + path, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    MLog.i(TAG, response);
//                                    handler.sendEmptyMessageDelayed(1, keepAliveIntevel);
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    MLog.i(TAG, error.getMessage());
//                                    handler.sendEmptyMessageDelayed(1, keepAliveIntevel);
//                                }
//                            });
//                            MyApplication1.mRequestQueue.add(stringRequest);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..
        // 启动百度push
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(getApplicationContext(), "api_key"));
        MLog.init("");


        Toast.makeText(getApplicationContext(), "server start", Toast.LENGTH_SHORT).show();
        MLog.i(getClass().getName(), "service onCreate");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new SreenBroadCaseReceiver(), filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new SreenBroadCaseReceiver(), filter2);

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(Constant.UPDATEAPK_ACTION);
        registerReceiver(new SreenBroadCaseReceiver(), filter3);

        ConfigUtil.init(getApplicationContext(), ConfigUtil.CONFIGNAME);

        handler.sendEmptyMessageDelayed(0, NotifyMessageController.lockTimeIntervel);
        handler.sendEmptyMessageDelayed(1, NotifyMessageController.keepAliveIntevel);


        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StudentInfoActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        notification = builder.build();


        NotifyMessageController.startTimeAlarm(getApplicationContext(), 0, NotifyMessageController.lockTimeIntervel);
        NotifyMessageController.startTimeAlarm(getApplicationContext(), 1, NotifyMessageController.keepAliveIntevel);


        AlarmManager alarmManager;
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Service.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(),
                Service1.class);
        intent.putExtra("what", 3);
        final PendingIntent pi = PendingIntent.getService(
                getApplicationContext(), 3, intent, 0);
        alarmManager.setRepeating(RTC_WAKEUP, 0, NotifyMessageController.UPDATETIMELOCKED, pi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MyJobService.startJobSheduler(getApplicationContext());
        }


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

    private int count = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.i(TAG, "onStartCommand");

//        if (Build.VERSION.SDK_INT < 18) {
        startForeground(GRAY_SERVICE_ID, notification);//API < 18 ，此方法能有效隐藏Notification上的图标
//        } else {
//            Intent innerIntent = new Intent(this, GrayInnerService.class);
//            startService(innerIntent);
//            startForeground(GRAY_SERVICE_ID, notification);
//        }
        if (intent.hasExtra("what")) {
            int what = intent.getIntExtra("what", 0);
            MLog.i(TAG, "what=" + what);
            if (what == 3) {
                JSONObject jsonObject = null;
                try {
                    String info = Constant.getStudentInfo(getApplicationContext());
                    if (!TextUtils.isEmpty(info)) {
                        jsonObject = new JSONObject(info);
                        if (jsonObject.has("studentId")) {
                            StudentInfoActivity.query(getApplicationContext(), jsonObject.optString("studentId"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (Build.VERSION.SDK_INT >= 19) {
                    NotifyMessageController.startTimeAlarm(getApplicationContext(), what, what == 0 ? NotifyMessageController.lockTimeIntervel : NotifyMessageController.keepAliveIntevel);
                }
                processLockTime(what);
            }

        }

        return Service.START_STICKY;
    }

    private void processLockTime(int what) {
        if (what == 0) {
            LASTLOCKTIME = System.currentTimeMillis();
            ArrayList<ConfigEntity> allConfig = ConfigUtil.config;
            Iterator<ConfigEntity> iterator = allConfig.iterator();
            boolean isNeedLocked = false;
            while (iterator.hasNext()) {
                ConfigEntity configEntity = iterator.next();
                if (DateUtil.isLockedTime(configEntity.startTime, configEntity.endTime)) {
                    isNeedLocked = true;
                    ConfigUtil.addLockedTime(getApplicationContext(), configEntity.startTime, configEntity.endTime);
                    NotifyMessageController.uploadLockedTime(getApplicationContext());
                    break;
                }
            }
            if (LockedWindow.mLock != isNeedLocked) {
                LockedWindow.show(getApplicationContext(), isNeedLocked);
            }
//            handler.sendEmptyMessageDelayed(0, lockTimeIntervel);
            MLog.i(TAG, "isNeedLocked->" + isNeedLocked);


        } else {
            LASTKEEPALIVE = System.currentTimeMillis();
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
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                MLog.i(TAG, error.getMessage());
                            }
                        });
                        MyApplication1.mRequestQueue.add(stringRequest);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            NotifyMessageController.uploadLockedTime(getApplicationContext());
        }


    }
}
