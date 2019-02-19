package com.ut.base.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.ut.base.BaseApplication;
import com.ut.base.R;

import java.util.HashMap;
import java.util.Map;

/**
 * author : zhouyubin
 * time   : 2019/02/19
 * desc   :
 * version: 1.0
 */
public class AudioPlayUtil {

    private static AudioPlayUtil INSTANCE = null;

    private SoundPool mSoundPool = null;

    private Context mContext = null;

    private int mStreamID = 0;

    private boolean mRingerMode = true;

    private boolean mVibrateMode = true;

    private Vibrator mVibrator = null;

    private Map<Integer, Integer> mSoundMap;

    private AudioPlayUtil(Context context) {
        mContext = context.getApplicationContext();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mSoundMap = new HashMap<>();
        mSoundMap.put(0, mSoundPool.load(mContext, R.raw.dingdong, 0));
    }

    public static AudioPlayUtil get(Context context) {
        if (INSTANCE == null) {
            synchronized (AudioPlayUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioPlayUtil(context);
                }
            }
        }
        return INSTANCE;
    }

    public void play(int id, boolean needVibrate) {
        stop();
        getAlarmParams();
        if (BaseApplication.getUser().isEnableSound() == 0) return;
        if (mRingerMode) {
            mStreamID = mSoundPool.play(mSoundMap.get(id), 1, 1, 0, 0, 1);
        }
        if (mVibrateMode && needVibrate) {
            if (mVibrator.hasVibrator())
                mVibrator.vibrate(500);
        }
    }


    public void stop() {
        if (mStreamID != -1) {
            mSoundPool.stop(mStreamID);
            mStreamID = -1;
        }
    }

    /**
     * 设置震动和声音
     *
     * @see
     * @since V2.0
     */
    private void getAlarmParams() {
        // AudioManager provides access to volume and ringer mode control.
        AudioManager volMgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        switch (volMgr.getRingerMode()) { // 获取系统设置的铃声模式
            case AudioManager.RINGER_MODE_SILENT:// 静音模式，值为0，这时候不震动，不响铃
                mVibrateMode = false;
                mRingerMode = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:// 震动模式，值为1，这时候震动，不响铃
                mRingerMode = false;
                mVibrateMode = true;
                break;
            case AudioManager.RINGER_MODE_NORMAL:// 常规模式，值为2，分两种情况：1_响铃但不震动，2_响铃+震动
                mRingerMode = true;
                mVibrateMode = true;
                break;
            default:
                break;
        }
    }
}
