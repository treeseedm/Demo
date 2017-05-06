package com.csja.smlocked;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.csja.smlocked.log.MLog;

import java.lang.ref.WeakReference;

/**
 * Created by mahaifeng on 17/3/28.
 */

public class SreenBroadCaseReceiver extends BroadcastReceiver {
    private static String TAG = "SreenBroadCaseReceiver";
    private View view;
    private WindowManager.LayoutParams lp;
    public static WeakReference<Activity> mActivityWref;


    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        MLog.i(getClass().getName(), "action:" + action);
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            if (mActivityWref != null) {
                Activity activity = mActivityWref.get();
                if (activity != null) {
                    activity.finish();
                    mActivityWref=null;
                }
            }
//            LockedWindow.show(context,LockedWindow.mLock);
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            LiveActivity.actionToLiveActivity(context);


//            LockedWindow.show(context, LockedWindow.mLock);
        }
    }

//    private WindowManager.LayoutParams generateLayoutParams(Context context) {
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
//        lp.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
//        lp.x = 0;
//        lp.y = 0;
//        lp.format = PixelFormat.TRANSLUCENT;
//        return lp;
//    }
}
