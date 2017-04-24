package com.csja.smlocked;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by fangjian on 15/8/19.
 */
final class BeepManager {

    private static final String TAG = BeepManager.class.getSimpleName();

    private final Activity activity;
    private SoundPool soundPool;

    BeepManager(Activity activity) {
        this.activity = activity;
        initBeepSound();
    }

    void initBeepSound() {
        soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        AssetFileDescriptor file = activity.getResources().openRawResourceFd(
               R.raw.qrcode_completed);
        soundPool.load(file, 1);
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否能够在识别成功时发出声音，如果用户选择了静音模式或者震动模式，则返回false表示不发出声音
     */
    private boolean shouldBeep() {
        boolean shouldPlayBeep = true;
        // See if sound settings overrides this
        AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            shouldPlayBeep = false;
        }
        return shouldPlayBeep;
    }

    public void playBeepSound() {
        if (shouldBeep()) {
            soundPool.play(1, 1, 1, 0, 0, 1);
        }
    }

    public void release() {
        soundPool.release();
    }

}

