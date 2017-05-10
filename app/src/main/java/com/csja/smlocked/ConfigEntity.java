package com.csja.smlocked;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by mahaifeng on 17/4/1.
 */

public class ConfigEntity implements Serializable, Comparable {
    /**
     * startTime : 1492642800000
     * endTime : 1492646400000
     */

    public long startTime;
    public long endTime;

    public ConfigEntity(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ConfigEntity{" +
                "startDate=" + startTime +
                ", endDate=" + endTime +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ConfigEntity configEntity = (ConfigEntity) o;
        if (configEntity.startTime > this.startTime) {
            return 1;
        } else {
            return -1;
        }
    }
}
