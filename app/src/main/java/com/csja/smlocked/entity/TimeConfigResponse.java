package com.csja.smlocked.entity;

import com.csja.smlocked.ConfigEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahaifeng on 17/4/20.
 */

public class TimeConfigResponse implements Serializable {

    /**
     * code : success
     * lockTime : [{"startTime":"1492642800000","endTime":"1492646400000"},{"startTime":"1492647600000","endTime":"1492650000000"},{"startTime":"1492686000000","endTime":"1492691400000"},{"startTime":"1492691400000","endTime":"1492702200000"},{"startTime":"1492702200000","endTime":"1492639200000"},{"startTime":"1492729200000","endTime":"1492732800000"},{"startTime":"1492734000000","endTime":"1492736400000"},{"startTime":"1492772400000","endTime":"1492777800000"},{"startTime":"1492777800000","endTime":"1492788600000"},{"startTime":"1492788600000","endTime":"1492725600000"},{"startTime":"1492988400000","endTime":"1492992000000"},{"startTime":"1492993200000","endTime":"1492995600000"},{"startTime":"1493031600000","endTime":"1493037000000"},{"startTime":"1493037000000","endTime":"1493047800000"},{"startTime":"1493047800000","endTime":"1492984800000"},{"startTime":"1493074800000","endTime":"1493078400000"},{"startTime":"1493079600000","endTime":"1493082000000"},{"startTime":"1493118000000","endTime":"1493123400000"},{"startTime":"1493123400000","endTime":"1493134200000"},{"startTime":"1493134200000","endTime":"1493071200000"},{"startTime":"1493161200000","endTime":"1493164800000"},{"startTime":"1493166000000","endTime":"1493168400000"},{"startTime":"1493204400000","endTime":"1493209800000"},{"startTime":"1493209800000","endTime":"1493220600000"},{"startTime":"1493220600000","endTime":"1493157600000"}]
     * motto : {"creator":"李校长","content":"好好学习天天向上"}
     * text : 操作成功！
     */

    public String code;
    public MottoEntity motto;
    public String text;
    public ArrayList<ConfigEntity> lockTime;

    public static class MottoEntity {
        /**
         * creator : 李校长
         * content : 好好学习天天向上
         */

        public String creator;
        public String content;
    }
}
