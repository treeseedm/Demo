package com.csja.smlocked.daemon;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.csja.smlocked.log.MLog;

/**
 * Created by mahaifeng on 17/4/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private String TAG = "MyJobService";

    @Override
    public void onCreate() {
        super.onCreate();
        startJobSheduler();
    }


    public void startJobSheduler() {
        try {
            int id = 1;
            JobInfo.Builder builder = new JobInfo.Builder(id,
                    new ComponentName(getPackageName(), MyJobService.class.getName()));
            builder.setPeriodic(500);  //间隔500毫秒调用onStartJob函数， 500只是为了验证
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int ret = jobScheduler.schedule(builder.build());
            // Android24版本才有scheduleAsPackage方法， 期待中
//Class clz = Class.forName("android.app.job.JobScheduler");
//Method[] methods = clz.getMethods();
//Method method = clz.getMethod("scheduleAsPackage", JobInfo.class , String.class, Integer.class, String.class);
//Object obj = method.invoke(jobScheduler, builder.build(), "com.brycegao.autostart", "brycegao", "test");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        MLog.d(TAG, "onStartJob alive");
        startService(new Intent(getApplicationContext(), Service1.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        MLog.d(TAG, "onStopJob alive");
        return false;
    }
}

