package com.csja.smlocked;

import android.content.Context;

import com.csja.smlocked.log.MLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class ConfigUtil {
    private static String TAG = "ConfigUtil";
    public static final String CONFIGNAME = "config";
    public static ArrayList<ConfigEntity> config = new ArrayList<>();

    public static void init(Context context, String name) {
        config = SerializeUtils.deSerializeObject(context, name);
        if (config == null) {
            config = new ArrayList<>();
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
}
