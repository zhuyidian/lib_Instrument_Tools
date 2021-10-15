package com.coocaa.remoteplatform.baseability.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;

import java.io.File;

public class DeviceInfoUtils {
    public static String getAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("switch", Context.MODE_PRIVATE);
        return sharedPreferences.getString("switchJson", "");
    }

    public static boolean isSupportAlarm() {
        File file = new File("/dev/rtc0");
        File file2 = new File("/sys/class/rtc/rtc0/wakealarm");
        return file.exists() && file2.exists();
    }

    public static int getMaxVolume(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getMinVolume(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return manager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        } else {
            return 0;
        }
    }

    public static int convertSystemVolume(int currentVolume, int maxValue) {
        float convertVom = (float) currentVolume / maxValue * 100;
        //int target = (int) convertVom;
        int target = Math.round(convertVom);
        if (target > 100) target = 100;
        if (target < 0) target = 0;
        return target;
    }
}
