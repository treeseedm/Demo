package com.csja.smlocked;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by mahaifeng on 17/3/28.
 */

public class SreenBroadCaseReceiver extends BroadcastReceiver {
    private View view;
    private WindowManager.LayoutParams lp;

    @Override
    public void onReceive(final Context context, Intent intent) {
//        String action = intent.getAction();
//        Log.d(getClass().getName(), "action:" + action);
//        if (action.equals(Intent.ACTION_SCREEN_ON)) {
//            context.startActivity(new Intent(context, MainActivity.class));
//            Log.d(getClass().getName(), "ACTION_SCREEN_OFF");
//            WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
//            if (lp == null) {
//                lp = generateLayoutParams(context);
//            }
//            if (view == null) {
//                view = View.inflate(context, R.layout.layout_locked, null);
//                view.findViewById(R.id.ll_container).setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        view.setVisibility(View.GONE);
//                        return false;
//                    }
//                });
//                windowManager.addView(view, lp);
//            }
//            if (view.getVisibility() == View.GONE) {
//                view.setVisibility(View.VISIBLE);
//            }
//        }
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
