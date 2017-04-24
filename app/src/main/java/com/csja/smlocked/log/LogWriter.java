package com.csja.smlocked.log;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * author: wangxianbing
 * email: wangxianbing@meituan.com
 * time: 15/12/14 00:00
 */

class LogWriter {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss:SSS");
    private static final SimpleDateFormat BAK_FILE_PART = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

    private static final int MAX_FILE_SIZE = 1;
    private static final int BUFF_SIZE = 32 * 1024;
    private static final long FLUSH_INTERVAL = 5000;
    private static final int BAK_FILE_COUNT = 5;

    private long mLastMillis = 0;
    private BufferedWriter mWriter;
    private String mLogDir;
    private File mLogFile;

    private BaseScheduler mScheduler = ThreadPoolScheduler.getInstance();

    public LogWriter() {
    }

    public void setLogPath(String logDir) {
        if (TextUtils.isEmpty(logDir)) {
            return;
        }

        mLogDir = logDir;
        File file = new File(mLogDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        String logFileName = getCurLogFileName();
        Log.d("MLog", "setLogPath logFileName：" + logFileName);
        if (TextUtils.isEmpty(logFileName)) {
            long time = System.currentTimeMillis();
            logFileName = getNewLogFileName(time);
        }
        String LogFilePath = getLogFilePath(mLogDir, logFileName);

        createWriter(LogFilePath);
    }

    public String getLogPath() {
        return mLogDir;
    }

    public void writeLogToFile(final String tag, final String msg) {
        final long timeMilis = System.currentTimeMillis();
        mScheduler.runOnQueueThread(BaseScheduler.SCHEDULER_LOG, new Runnable() {
            @Override
            public void run() {
                try {
                    writeLog(tag, msg, timeMilis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private String getNewLogFileName(long timeMillis) {
        String fileExt = BAK_FILE_PART.format(timeMillis);
        StringBuilder sb = new StringBuilder(fileExt).append(".txt");
        return sb.toString();
    }

    private String getLogFilePath(String logDir, String logFileName) {
        return logDir.endsWith(File.separator) ? (logDir + logFileName) : (logDir + File.separator + logFileName);
    }

    private String getCurLogFileName() {
        File dirFile = new File(mLogDir);
        if (!dirFile.exists()) {
            return null;
        }

        final File files[] = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File tmp = null;
        for (File f : files) {
            String name = f.getName();
            if (!TextUtils.isEmpty(name)) {
                if (Character.isDigit(name.charAt(0))) {
                    if (tmp == null) {
                        tmp = f;
                    } else if (f.lastModified() > tmp.lastModified()) {
                        tmp = f;
                    }
                }
            }
        }

        if (tmp != null) {
            return tmp.getName();
        }

        return null;
    }

    private void writeLog(String tag, String msg, long timeMillis) throws IOException {
        String strLog = DATE_TIME_FORMAT.format(timeMillis);

        StringBuffer sb = new StringBuffer(strLog);
        sb.append(' ');
        sb.append(tag);
        sb.append(' ');
        sb.append(msg);
        sb.append('\n');
        strLog = sb.toString();

        BufferedWriter bufWriter = mWriter;

        if (bufWriter != null) {
            bufWriter.write(strLog);

            long curMillis = SystemClock.elapsedRealtime();
//            if (curMillis - mLastMillis >= FLUSH_INTERVAL) {
                bufWriter.flush();
                mLastMillis = curMillis;

                long fileSize = (mLogFile.length() >>> 20);
                if (fileSize >= MAX_FILE_SIZE) {
                    logToNewFile(timeMillis);

                    deleteOldBakFiles();
                }
//            }
        }
    }

    private void logToNewFile(long timeMillis) {
        Log.d("MLog", "logToNewFile");
        close();

        String logFileName = getNewLogFileName(timeMillis);
        String LogFilePath = getLogFilePath(mLogDir, logFileName);
        createWriter(LogFilePath);
    }

    private void createWriter(String logFilePath) {
        Log.d("MLog", "createWriter logFilePath：" + logFilePath);
        mLogFile = new File(logFilePath);

        if (!mLogFile.exists()) {
            try {
                mLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(mLogFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileWriter != null) {
            mWriter = new BufferedWriter(fileWriter, BUFF_SIZE);
        }
    }

    private void deleteOldBakFiles() {
        File dirFile = new File(mLogDir);
        if (!dirFile.exists()) {
            return;
        }

        final File files[] = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        List<File> bakFileList = new ArrayList<>();
        for (File f : files) {
            if (f.getName().endsWith(".txt")) {
                bakFileList.add(f);
            }
        }

        if (bakFileList.size() <= BAK_FILE_COUNT) {
            return;
        }

        File[] fileToBeDelete = new File[bakFileList.size()];
        for (int i = 0; i < fileToBeDelete.length; i++) {
            fileToBeDelete[i] = bakFileList.get(i);
            Log.d("MLog", "deleteOldBakFiles 删除前file：" + fileToBeDelete[i].getName());
        }

        deleteOldFiles(fileToBeDelete, BAK_FILE_COUNT);
    }

    private void deleteOldFiles(File[] files, int count) {
        Comparator<? super File> comparator = new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return rhs.getName().compareTo(lhs.getName());
            }
        };

        Arrays.sort(files, comparator);

        for (int i = count; i < files.length; ++i) {
            File file = files[i];
            Log.d("MLog", "deleteOldFiles 准备删除file：" + file.getName());
            if (!file.delete()) {
                Log.e("LogWriter", "delete log file fail");
            }
        }
    }

    public void flush() {
        BufferedWriter writer = mWriter;
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close() {
        BufferedWriter writer = mWriter;
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
