package com.remoteplatform.commom;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;

/**
 * @ClassName: Utils
 * @Author: XuZeXiao
 * @CreateDate: 4/7/21 4:05 PM
 * @Description:
 */
public class Utils {
    public static String getProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo apps : activityManager.getRunningAppProcesses()) {
            if (apps.pid == pid) {
                return apps.processName;
            }
        }
        return null;
    }

    public static <T> T valueFromManifest(Context context, String key) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return (T) applicationInfo.metaData.get(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (T) "";
    }
}
