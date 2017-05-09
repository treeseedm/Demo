package com.csja.smlocked;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.csja.smlocked.daemon.Service1;

import java.util.ArrayList;

public class MainActivity extends Activity {
    WindowManager.LayoutParams lp;
    private View view;
    private TextView mStartTime, mEndTime;
    private ArrayList<ConfigEntity> allConfig;
    private TimeAdapter timeAdapter;
    private PopupWindow mChoiceEndTimePop;

    private WheelView mStartTimeWheelView;
    private WheelView mEndtimeWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, Service1.class));

        if (ConfigUtil.config == null) {
            ConfigUtil.init(getApplicationContext(), ConfigUtil.CONFIGNAME);
        }
        allConfig = new ArrayList<>(ConfigUtil.config);
        Intent intent = new Intent(this, StudentInfoActivity.class);
        intent.setAction("main");
        startActivity(intent);
        finish();
    }

    private void add() {
        String startTime = mStartTime.getText().toString();
        if (startTime.length() > 0) {
//            ConfigUtil.setDate(this, startTime.split("-")[0], startTime.split("-")[1], ConfigUtil.CONFIGNAME);
//            allConfig.add(new ConfigEntity(startTime.split("-")[0], startTime.split("-")[1]));
            timeAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "请填写时间", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectTime() {
        if (mChoiceEndTimePop == null) {
            View view = View.inflate(this, R.layout.room_layout_time_pop, null);
            mStartTimeWheelView = (WheelView) view.findViewById(R.id.wv_lefttime);
            mStartTimeWheelView.setData(DateUtil.getDate(), 0);

            mEndtimeWheelView = (WheelView) view.findViewById(R.id.wv_righttime);
            mEndtimeWheelView.setData(DateUtil.getDate(), 0);

            mChoiceEndTimePop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mChoiceEndTimePop.setBackgroundDrawable(new BitmapDrawable());
            mChoiceEndTimePop.setFocusable(true);
            view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoiceEndTimePop.dismiss();
                }
            });
            view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStartTime.setText(mStartTimeWheelView.getSelectedText() + "-" + mEndtimeWheelView.getSelectedText());
                    mChoiceEndTimePop.dismiss();
                }
            });

        }

        mChoiceEndTimePop.showAtLocation(findViewById(R.id.ll_container), Gravity.BOTTOM, 0, 0);
    }


    private class TimeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allConfig.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TimeHolder timeHolder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_time_layout, null);
                timeHolder = new TimeHolder();
                timeHolder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(timeHolder);
            }
            timeHolder = (TimeHolder) convertView.getTag();
            ConfigEntity configEntity = allConfig.get(position);
//            timeHolder.time.setText(configEntity.startDate + "-" + configEntity.endDate);
            return convertView;
        }

        private class TimeHolder {
            TextView time;
        }
    }


}
