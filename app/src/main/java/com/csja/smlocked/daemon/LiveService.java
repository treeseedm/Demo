package com.csja.smlocked.daemon;

import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import com.csja.smlocked.log.MLog;

/**
 * Created by mahaifeng on 17/4/26.
 */


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class LiveService extends NotificationListenerService {
    private String TAG = "LiveService";

    public LiveService() {

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        MLog.i(TAG, "onNotificationPosted");
        startService(new Intent(getApplicationContext(), Service1.class));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        MLog.i(TAG, "onNotificationRemoved");
        startService(new Intent(getApplicationContext(), Service1.class));
    }

}
