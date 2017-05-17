package com.csja.smlocked;

import android.content.Context;

import com.csja.smlocked.log.MLog;

import java.util.ArrayList;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class ConfigUtil {
    private static String TAG = "ConfigUtil";
    public static final String CONFIGNAME = "config";
    public static final String LOCKEDTIMENAME = "lockedtimename";
    public static ArrayList<ConfigEntity> config = new ArrayList<>();
    public static ArrayList<ConfigEntity> lockedTime = new ArrayList<>();

    public static void init(Context context, String name) {
        config = SerializeUtils.deSerializeObject(context, name);
        lockedTime = SerializeUtils.deSerializeObject(context, LOCKEDTIMENAME);
        if (config == null) {
            config = new ArrayList<>();
        }
        if (lockedTime == null) {
            lockedTime = new ArrayList<>();
        }
        MLog.i(TAG, "init config " + config.size());
    }

    public static void addTime(Context context, long startTime, long endTime) {
        ConfigEntity configEntity = new ConfigEntity(startTime, endTime);
        config.add(configEntity);
        MLog.i(TAG, configEntity.toString());
        SerializeUtils.serializeObject(context, config, CONFIGNAME);
    }

    public static void clearTime(Context context) {
        config.clear();
        SerializeUtils.deSerializeObject(context, CONFIGNAME);
    }

    public static void addLockedTime(Context context, long startTime, long endTime) {
        ConfigEntity configEntity = new ConfigEntity(startTime, endTime);
        lockedTime.add(configEntity);
        MLog.i(TAG, "lockedTime:" + configEntity.toString());
        SerializeUtils.serializeObject(context, lockedTime, LOCKEDTIMENAME);
    }

    public static void removeLockedTime(Context context, ConfigEntity configEntity) {
        lockedTime.remove(configEntity);
        SerializeUtils.serializeObject(context, lockedTime, LOCKEDTIMENAME);
    }
}
