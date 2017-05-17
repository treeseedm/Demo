package com.csja.smlocked;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csja.smlocked.View.CustomListView;
import com.csja.smlocked.log.MLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class LockedWindow {
    private static String TAG = "LockedWindow";
    private static int THEMEIMAGE_INTEVAL = 5 * 60 * 1000;
    private static View view;
    private static WindowManager.LayoutParams lp;
    public static boolean mLock = false;
    private static String phoneList;
    private static RelativeLayout mContainer;

    public synchronized static void show(final Context context, boolean lock) {
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

    private static Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (view == null) {
                return;
            }
            if (mContainer.getTag() == null) {
                mContainer.setTag(1);
                mContainer.setBackground(view.getContext().getResources().getDrawable(R.mipmap.locked1));
            } else {
                int tag = (int) mContainer.getTag();
                switch (tag) {
                    case 1:
                        setBackground(2, R.mipmap.lock2, mContainer, view.getContext());
                        break;
                    case 2:
                        setBackground(3, R.mipmap.lock3, mContainer, view.getContext());
                        break;
                    case 3:
                        setBackground(4, R.mipmap.lock4, mContainer, view.getContext());
                        break;
                    case 4:
                        setBackground(5, R.mipmap.lock5, mContainer, view.getContext());
                        break;
                    case 5:
                        setBackground(6, R.mipmap.lock6, mContainer, view.getContext());
                        break;
                    case 6:
                        setBackground(1, R.mipmap.locked1, mContainer, view.getContext());
                        break;
                }
            }

            sendEmptyMessageDelayed(0, THEMEIMAGE_INTEVAL);
        }
    };

    public static void initView(final Context context) {
        if (view != null) {
            Button tvPhone = (Button) view.findViewById(R.id.phone);
//        ImageView close = (ImageView) view.findViewById(R.id.close);
            Button unlock = (Button) view.findViewById(R.id.unlock);
            Button update = (Button) view.findViewById(R.id.update);
            TextView lockTipC = (TextView) view.findViewById(R.id.locked_tip2);
            TextView lockTipTop = (TextView) view.findViewById(R.id.tv_lockedtip);

            TextView responsiblepersion = (TextView) view.findViewById(R.id.responsiblepersion);
            TextView locktime = (TextView) view.findViewById(R.id.locktime);
//        TextView schoolMoto = (TextView) view.findViewById(R.id.tv_school_motto);
            mContainer = (RelativeLayout) view.findViewById(R.id.ll_container);
            tvPhone.setOnClickListener(new View.OnClickListener() {
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
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
////                intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(intent);
//            }
//        });
            unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
            String tipTop = "课后时间，允许解锁";
            String tipCen = "";
            AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
            if (!mLock) {
                unlock.setVisibility(View.VISIBLE);
                tipCen = Constant.getMotto(context).content;
                lockTipTop.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.shape_circle_green), null);
                mContainer.setBackground(context.getResources().getDrawable(R.mipmap.unlock));
                myHandler.removeCallbacksAndMessages(null);
                //声音模式
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                update.setVisibility(View.VISIBLE);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject jsonObject = null;
                        try {
                            String info = Constant.getStudentInfo(context);
                            if (!TextUtils.isEmpty(info)) {
                                jsonObject = new JSONObject(info);
                                if (jsonObject.has("studentId")) {
                                    StudentInfoActivity.query(context, jsonObject.optString("studentId"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                responsiblepersion.setVisibility(View.VISIBLE);
                locktime.setVisibility(View.VISIBLE);
                responsiblepersion.setText("倡导人:" + Constant.getMotto(context).creator);
                if (TextUtils.isEmpty(getLastLockTime())) {
                    locktime.setVisibility(View.GONE);
                } else {
                    locktime.setVisibility(View.VISIBLE);
                    locktime.setText("系统将于" + getLastLockTime() + "锁定");
                }

            } else {
                tipTop = "教学时间，禁止解锁";
                unlock.setVisibility(View.GONE);
                lockTipTop.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.shape_circle_red), null);

                myHandler.sendEmptyMessage(0);

                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                update.setVisibility(View.GONE);
                responsiblepersion.setVisibility(View.GONE);
                locktime.setVisibility(View.GONE);
            }

            lockTipTop.setText(tipTop);
            lockTipC.setText(tipCen);
        }

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
        MLog.i(TAG, android.os.Build.MODEL);
        if (Build.MODEL.contains("vivo")) {
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

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

    private static String getLastLockTime() {

        ArrayList<ConfigEntity> allConfig = ConfigUtil.config;
        Collections.sort(allConfig);
        Iterator<ConfigEntity> iterator = allConfig.iterator();
        while (iterator.hasNext()) {
            ConfigEntity configEntity = iterator.next();
            if (System.currentTimeMillis() < configEntity.startTime) {
                return DateUtil.formatDate("HH点mm分", configEntity.startTime);
            }
        }
        return "";
    }
}
