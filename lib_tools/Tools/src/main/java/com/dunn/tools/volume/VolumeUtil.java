package com.dunn.tools.volume;

import android.content.Context;
import android.media.AudioManager;

import com.dunn.tools.common.CommonUtil;
import com.dunn.tools.log.LogUtil;

public class VolumeUtil {

    /**
     * volumeType:
     * AudioManager.STREAM_SYSTEM  系统音量
     * AudioManager.STREAM_MUSIC  音乐音量
     * AudioManager.STREAM_ALARM  提示声音音量
     * AudioManager.STREAM_VOICE_CALL  通话音量
     * AudioManager.STREAM_RING  铃声音量
     * AudioManager.STREAM_NOTIFICATION  窗口顶部状态栏通知声
     * AudioManager.STREAM_ACCESSIBILITY
     * AudioManager.STREAM_DTMF  双音多频,不是很明白什么东西
     * flag:
     * flags(可选标志位):
     * flags One（单个参数） or more flags.(参数1|参数2|参数3..)
     * 如下flag：
     * 0 表示什么额外行为也没有，比如想自己绘制音量条时，可以使用这个值防止显示系统默认的音量条
     * AudioManager.FLAG_SHOW_UI 调整时显示音量条，跟按按键调节音量时出现的音量条一致
     * AudioManager.FLAG_ALLOW_RINGER_MODES
     * AudioManager.FLAG_PLAY_SOUND  调整音量时播放声音
     * AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
     * AudioManager.FLAG_VIBRATE
     *
     * @param context
     * @param volume
     */
    public static void setVolume(Context context, int volume) {
        try {
            LogUtil.i("volume", "set volume volume=" + volume);
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = getMaxVolume(context, AudioManager.STREAM_SYSTEM);
            int currentVolume = getCurrentVolume(context, AudioManager.STREAM_SYSTEM);
            volume = CommonUtil.convertVolume(volume, maxVolume);
            LogUtil.i("volume", "set volume maxVolume=" + maxVolume + ", currentVolume=" + currentVolume + ", convert volume=" + volume);
            setVolume(context, AudioManager.STREAM_SYSTEM, volume,/*AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE*/AudioManager.FLAG_PLAY_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "set volume e=" + e);
        }
    }

    /**
     * direction,是音量调节的方向，可以取下面的几个值:
     * ADJUST_LOWER 降低音量
     * ADJUST_RAISE 升高音量
     * ADJUST_SAME 保持不变，使用这个值UI上会向用户展示当前的音量
     *
     * @param context
     * @param volumeType
     * @param flag
     */
    private static void setAdjustVolume(Context context, int volumeType, int direction, int flag) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            manager.adjustStreamVolume(volumeType, direction, flag);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }
    }

    private static void setVolume(Context context, int volumeType, int volume, int flag) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamVolume(volumeType, volume, flag);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }
    }

    private static int getMaxVolume(Context context, int volumeType) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            return manager.getStreamMaxVolume(volumeType);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }

        return 100;
    }

    private static int getCurrentVolume(Context context, int volumeType) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            return manager.getStreamVolume(volumeType);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }

        return 100;
    }

    /**
     * @param context
     * @return MODE_INVALID: 发生异常的时候返回
     * MODE_NORMAL: 普通模式
     * MODE_RINGTONE：铃声模式
     * MODE_IN_CALL: 通话模式
     * MODE_IN_COMMUNICATION:通话模式
     */
    private static int getMode(Context context) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //获取/设置铃声的模式
            int mode = manager.getMode();
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
            return -1;
        }
    }

    private static void setMode(Context context, int mode) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            manager.setMode(mode);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }
    }

    /**
     * @param context
     * @return RINGER_MODE_NORMAL（铃声模式）
     * RINGER_MODE_SILENT(静音模式)
     * RINGER_MODE_VIBRATE（静音但是震动）
     */
    private static int getRingMode(Context context) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //获取/设置铃声的模式
            int ringMode = manager.getRingerMode();
            return ringMode;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
            return -1;
        }
    }

    private static void setRingMode(Context context) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //普通模式
            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            //静音模式
            manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }
    }

    private static void setStreamMute(Context context) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //设置声音流静音/不静音
            //音乐静音
            manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            //铃声不静音
            manager.setStreamMute(AudioManager.STREAM_RING, false);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("volume", "e=" + e);
        }
    }
}
