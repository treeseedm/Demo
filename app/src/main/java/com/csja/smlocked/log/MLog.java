package com.csja.smlocked.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * author: wangxianbing
 * email: wangxianbing@meituan.com
 * time: 10/29/15 00:00
 * <p>
 * <p>
 * 使用方法：
 * MLog.w("meituan", "meituan warning logs");
 * MLog.d(this, "%s, %d, %b", "haha", 123, false);
 * MLog.i("Simom", "%s, %d, %b", "hehe", 123, false);
 * 第一个参数tag可以是任何对象，包括String。参数可以可以format，不用+++了
 * <p>
 * MLog.e(this, "exception: %s", new NullPointerException());
 * MLog.e(this, new NullPointerException());
 */

public class MLog {

    public static final class Level {
        public static final int LEVEL_V = 1;
        public static final int LEVEL_D = 2;
        public static final int LEVEL_I = 3;
        public static final int LEVEL_W = 4;
        public static final int LEVEL_E = 5;
    }

    private static LogWriter sLogWriter = new LogWriter();
    private static int sLogLevel = Level.LEVEL_I;

    public static void init(String logDir) {
        String dir = TextUtils.isEmpty(logDir) ? "/sdcard/smlocked/logs/" : logDir;
        sLogWriter.setLogPath(dir);
    }

    public static String getLogPath() {
        if (sLogWriter != null) {
            return sLogWriter.getLogPath();
        }
        return "";
    }

    public static void setLogLevel(int logLevel) {
        if (logLevel < Level.LEVEL_V || logLevel > Level.LEVEL_E) {
            sLogLevel = Level.LEVEL_I;
        } else {
            sLogLevel = logLevel;
        }
    }

    public static void v(Object tag, String format, Object... args) {
        try {
            outputVerbose(tag, format, args);
        } catch (java.util.IllegalFormatException e) {
            e.printStackTrace();
        }
    }

    public static void d(Object tag, String format, Object... args) {
        try {
            outputDebug(tag, format, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void i(Object tag, String format, Object... args) {
        try {
            outputInfo(tag, format, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void w(Object tag, String format, Object... args) {
        try {
            outputWarning(tag, format, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Object tag, String format, Object... args) {
        try {
            outputError(tag, format, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Object tag, Throwable t) {
        try {
            outputError(tag, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void outputVerbose(Object obj, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            String tag = tag(obj);
            Log.v(tag, msg);

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_V) {
                sLogWriter.writeLogToFile(tag, msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void outputDebug(Object obj, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            String tag = tag(obj);
            Log.d(tag, msg);

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_D) {
                sLogWriter.writeLogToFile(tag, msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void outputInfo(Object obj, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            String tag = tag(obj);
            Log.i(tag, msg);

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_I) {
                sLogWriter.writeLogToFile(tag, msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void outputWarning(Object obj, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            String tag = tag(obj);
            Log.w(tag, msg);

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_W) {
                sLogWriter.writeLogToFile(tag, msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void outputError(Object obj, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            String tag = tag(obj);
            Log.e(tag, msg);

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_E) {
                sLogWriter.writeLogToFile(tag, msg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void outputError(Object obj, Throwable t) {
        try {
            String tag = tag(obj);
            Log.e(tag, t.toString());

            if (sLogWriter != null && sLogLevel <= Level.LEVEL_E) {
                sLogWriter.writeLogToFile(tag, t.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static String tag(Object tag) {
        return (tag instanceof String ? (String) tag : tag.getClass().getSimpleName());
    }

    public static void flush() {
        sLogWriter.flush();
    }
}
