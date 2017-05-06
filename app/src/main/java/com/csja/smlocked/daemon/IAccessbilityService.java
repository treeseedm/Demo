package com.csja.smlocked.daemon;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.csja.smlocked.log.MLog;

/**
 * Created by mahaifeng on 17/5/2.
 */

public class IAccessbilityService extends AccessibilityService {
    private static String TAG = "IAccessbilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        MLog.i(TAG, "" + event.getAction());
        Toast.makeText(getApplicationContext(),""+ event.getAction(), Toast.LENGTH_SHORT).show();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                startActivity(new Intent(getApplicationContext(), Service1.class));
//            }
//        }, 5000);
    }

    @Override
    public void onInterrupt() {
//        Toast.makeText(getApplicationContext(), "onInterrupt", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
//        Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }
}
