package com.coocaa.remoteplatform.baseability.common;

import android.content.Context;
import android.content.SharedPreferences;

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
}
