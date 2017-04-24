package com.csja.smlocked.daemon;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.push.example.Utils;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;

/**
 * Implementation 1<br/>
 * override one method is ok.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class MyApplication1 extends DaemonApplication {
    public static RequestQueue mRequestQueue;


    private class CustomException implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            MLog.i("CustomException", "CustomException->" + e.getMessage());
        }
    }

    /**
     * you can override this method instead of {@link android.app.Application attachBaseContext}
     *
     * @param base
     */
    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
    }


    /**
     * give the configuration to lib in this callback
     *
     * @return
     */
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "androidproject.com.demo",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "androidproject.com.demo:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 启动百度push
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(getApplicationContext(), "api_key"));

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        MLog.init("");

        Thread.setDefaultUncaughtExceptionHandler(new CustomException());
    }
}
