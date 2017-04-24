package com.csja.smlocked;

import android.content.Context;

import com.csja.smlocked.log.MLog;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class ConfigUtil {
    private static String TAG = "ConfigUtil";
    public static final String CONFIGNAME = "config";
    public static Set<ConfigEntity> config = new HashSet<>();

    public static void init(Context context, String name) {
        config = SerializeUtils.deSerializeObject(context, name);
        if (config == null) {
            config = new HashSet<>();
        }
        MLog.i(TAG, "init config " + config.size());
        int i=0;
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
}
