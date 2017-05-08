package com.csja.smlocked;

import com.csja.smlocked.log.MLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class DateUtil {
    private static String TAG = "DateUtil";

    public static boolean isLockedTime(long startTime, long endTime) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(startTime);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(endTime);

        long currentTime = System.currentTimeMillis();
        MLog.i(TAG, "startTime:" + startTime + " endTime:" + endTime + " currentTime:" + currentTime);
        if (calendarStart.getTimeInMillis() < currentTime && currentTime < calendarEnd.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    public static String formatDate(String format, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date=new Date(time);
        return simpleDateFormat.format(date);
    }

    public static List<String> getDate() {
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            dateList.add(i + ":00");
            dateList.add(i + ":15");
            dateList.add(i + ":30");
            dateList.add(i + ":45");
        }
        return dateList;
    }
}
