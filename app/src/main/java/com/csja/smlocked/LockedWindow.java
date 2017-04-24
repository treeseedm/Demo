package com.csja.smlocked;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csja.smlocked.View.CustomListView;
import com.csja.smlocked.log.MLog;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class LockedWindow {
    private static String TAG = "LockedWindow";
    private static View view;
    private static WindowManager.LayoutParams lp;
    public static boolean mLock = false;
    private static String phoneList;

    public static void show(final Context context, boolean lock) {
        mLock = lock;
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        if (lp == null) {
            lp = generateLayoutParams(context);
        }
        if (view == null) {
            view = View.inflate(context, R.layout.layout_locked, null);
            windowManager.addView(view, lp);

        }
        initView(context);
        //lock true
        //if lock false

        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private static void initView(final Context context) {
        ImageView imageView = (ImageView) view.findViewById(R.id.phone);
        ImageView close = (ImageView) view.findViewById(R.id.close);
        Button unlock = (Button) view.findViewById(R.id.unlock);
        TextView lockTipC = (TextView) view.findViewById(R.id.locked_tip2);
        TextView lockTipTop = (TextView) view.findViewById(R.id.tv_lockedtip);
//        TextView schoolMoto = (TextView) view.findViewById(R.id.tv_school_motto);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.ll_container);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View pop = View.inflate(context, R.layout.activity_phone_list, null);

                PopupWindow popupWindow = new PopupWindow(pop, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_rec_white));
                CustomListView listView = (CustomListView) pop.findViewById(R.id.list);
                phoneList = Constant.getContact(context);
//                if (TextUtils.isEmpty(phoneList)) {
//                    phoneList = "18201686357";
//                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, phoneList.split(";"));
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String p = phoneList.split(";")[position];
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + p));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + p));
//                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(context, new String[]{"android.permission.CALL_PHONE"},
//
//                                    100);
//                        }else{
                        callPhone(context, p);
//                        }

                    }
                });
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        String tipTop = "课后时间，允许解锁";
        String tipCen = "";
        if (!mLock) {
            unlock.setVisibility(View.VISIBLE);
            tipCen = Constant.getMotto(context).content;
            lockTipTop.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.shape_circle_green), null);
            container.setBackground(context.getResources().getDrawable(R.mipmap.unlock));
        } else {
            tipTop = "教学时间，禁止解锁";
            unlock.setVisibility(View.GONE);
            lockTipTop.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.shape_circle_red), null);

            if (container.getTag() == null) {
                container.setTag(1);
                container.setBackground(context.getResources().getDrawable(R.mipmap.locked1));
            } else {
                int tag = (int) container.getTag();
                switch (tag) {
                    case 1:
                        setBackground(2, R.mipmap.lock2, container, context);
                        break;
                    case 2:
                        setBackground(3, R.mipmap.lock3, container, context);
                        break;
                    case 3:
                        setBackground(4, R.mipmap.lock4, container, context);
                        break;
                    case 4:
                        setBackground(5, R.mipmap.lock5, container, context);
                        break;
                    case 5:
                        setBackground(6, R.mipmap.lock6, container, context);
                        break;
                    case 6:
                        setBackground(1, R.mipmap.locked1, container, context);
                        break;
                }
            }

        }
        lockTipTop.setText(tipTop);
        lockTipC.setText(tipCen);
    }

    private static void setBackground(int tag, int lock, RelativeLayout container, Context context) {
        container.setTag(tag);
        container.setBackground(context.getResources().getDrawable(lock));
    }

    public static void hide() {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public static WindowManager.LayoutParams generateLayoutParams(Context context) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        lp.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        lp.x = 0;
        lp.y = 0;
        lp.format = PixelFormat.TRANSLUCENT;
        return lp;
    }


    private static void callPhone(Context context, String phone) {
        MLog.i(TAG, "callphone->" + phone);

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + phone.split("|")[1]));

        context.startActivity(intent);

    }


}
