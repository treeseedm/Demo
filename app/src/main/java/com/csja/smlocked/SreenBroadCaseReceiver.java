package com.csja.smlocked;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.WindowManager;

import com.csja.smlocked.daemon.Service1;
import com.csja.smlocked.log.MLog;

import java.io.File;
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
                    mActivityWref = null;
                }
            }
            Intent intentServer = new Intent(context,
                    Service1.class);
            intentServer.putExtra("what", 0);
            context.startService(intentServer);
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            LiveActivity.actionToLiveActivity(context);
        } else if (Constant.UPDATEAPK_ACTION.equals(action)) {
            //文件保存位置
            File saveDir = new File(Constant.DOWNLOAD_PATH);
            for (File file : saveDir.listFiles()) {
                Intent uploadintent = new Intent(Intent.ACTION_VIEW);
//判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uploadintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    uploadintent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    uploadintent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    uploadintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(uploadintent);
                break;
            }
        }
    }

}
