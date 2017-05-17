package com.csja.smlocked;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static long UPDATETIMELOCKED = 12 * 60 * 60 * 1000;


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

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(final Context context, final String urlStr, final String fileName, final String savePath) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置超时间为3秒
                    conn.setConnectTimeout(3 * 1000);
                    //防止屏蔽程序抓取而返回403错误
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                    //得到输入流
                    InputStream inputStream = conn.getInputStream();
                    MLog.i(TAG,"SIZE:"+inputStream.available());
                    //获取自己数组
                    byte[] getData = readInputStream(inputStream);

                    //文件保存位置
                    File saveDir = new File(savePath);
                    if (!saveDir.exists()) {
                        saveDir.mkdir();
                    }
                    for (File file : saveDir.listFiles()) {
                        file.delete();
                    }
                    File file = new File(saveDir + File.separator + fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(getData);
                    if (fos != null) {
                        fos.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    MLog.i(TAG, "info:" + url + " download success");
                    context.sendBroadcast(new Intent(Constant.UPDATEAPK_ACTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void shutdown() {
        try {

//获得ServiceManager类
            Class ServiceManager = Class
                    .forName("android.os.ServiceManager");

//获得ServiceManager的getService方法
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);

//调用getService获取RemoteService
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);

//获得IPowerManager.Stub类
            Class cStub = Class
                    .forName("android.os.IPowerManager$Stub");
//获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
//调用asInterface方法获取IPowerManager对象
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
//获得shutdown()方法
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
//调用shutdown()方法
            shutdown.invoke(oIPowerManager, false, true);

        } catch (Exception e) {
            MLog.e(TAG, e.toString(), e);
        }

    }

    public static void updateVersion(final Context context) {
        String path = "setting/getVersion";
        JsonObjectReqeustWrapper jsonObjectReqeustWrapper = new JsonObjectReqeustWrapper(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    MLog.i(TAG, response.toString());
                    if (!getVersionName(context).equals(response.optString("versionCode"))) {
                        String downLoadUrL = response.optString("downLoadUrl");
                        try {
                            downLoadFromUrl(context, downLoadUrL, response.getString("versionCode") + ".apk", Constant.DOWNLOAD_PATH);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void uploadLockedTime(final Context context) {
        long studentId = Constant.getStudentId(context);
        for (final ConfigEntity configEntity : ConfigUtil.lockedTime) {

            String path = "setting/lockScreen" + "?studentId=" + URLEncoder.encode("" + studentId) + "&create_date=" + URLEncoder.encode("" + DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", configEntity.startTime));
            JsonObjectReqeustWrapper jsonObjectReqeustWrapper = new JsonObjectReqeustWrapper(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        ConfigUtil.removeLockedTime(context, configEntity);
                        MLog.i(TAG, response.toString());
                    } else {
                        MLog.i(TAG, "上传锁屏时间失败");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MLog.i(TAG, "上传锁屏时间失败");
                }
            });
            MyApplication1.mRequestQueue.add(jsonObjectReqeustWrapper);
        }

    }

}
