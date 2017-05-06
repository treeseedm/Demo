package com.csja.smlocked;

import android.content.Context;
import android.content.SharedPreferences;

import com.csja.smlocked.entity.TimeConfigResponse;

/**
 * Created by mahaifeng on 17/4/18.
 */

public class Constant {
    private static String CHANNELID = "channelid";
    private static String STUDENTINFO = "studentInfo";
    public static String HOST = "http://wx26.cn:8088/syllabus/api/";
    public static String SUCCESS = "success";
    public static String MOTTO = "motto";
    public static String CONTACT = "contact";

    public static void saveChannelId(Context context, String channelId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELID, context.MODE_PRIVATE);
        sharedPreferences.edit().putString("channelid", channelId).commit();
    }

    public static String getChannelId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELID, context.MODE_PRIVATE);
        return sharedPreferences.getString("channelid", "");
    }

    public static void saveStudentInfo(Context context, String studentInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STUDENTINFO, context.MODE_PRIVATE);
        sharedPreferences.edit().putString("studentInfo", studentInfo).commit();
    }

    public static String getStudentInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STUDENTINFO, context.MODE_PRIVATE);
        return sharedPreferences.getString("studentInfo", "");
    }

    public static void saveContact(Context context, String contact) {
        String temp = getContact(context);
        temp = contact + ";" + temp;

        SharedPreferences sharedPreferences = context.getSharedPreferences(CONTACT, context.MODE_PRIVATE);
        sharedPreferences.edit().putString("contact", temp).commit();
    }

    public static String getContact(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONTACT, context.MODE_PRIVATE);
        return sharedPreferences.getString("contact", "");
    }

    public static void saveMotto(Context context, String creator, String content) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MOTTO, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("creator", creator);
        editor.putString("content", content);
        editor.commit();
    }

    public static TimeConfigResponse.MottoEntity getMotto(Context context) {
        TimeConfigResponse.MottoEntity mottoEntity = new TimeConfigResponse.MottoEntity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MOTTO, context.MODE_PRIVATE);
        mottoEntity.creator = sharedPreferences.getString("creator", "");
        mottoEntity.content = sharedPreferences.getString("content", "");
        return mottoEntity;
    }
}
