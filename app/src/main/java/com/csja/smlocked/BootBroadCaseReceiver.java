package com.csja.smlocked;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.csja.smlocked.daemon.Service1;

/**
 * Created by mahaifeng on 17/3/31.
 */

public class BootBroadCaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "action=" + intent.getAction(), Toast.LENGTH_LONG).show();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(this.getClass().getName(), "开机自启动");
            context.startService(new Intent(context, Service1.class));
        }
    }
}
