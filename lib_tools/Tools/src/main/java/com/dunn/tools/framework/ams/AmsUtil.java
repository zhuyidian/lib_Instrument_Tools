package com.dunn.tools.framework.ams;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:AmsUtil
 */
public class AmsUtil {

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取当前展示 的Activity名称
     * @return
     */
    public static String getCurrentActivityName(Context context){
        ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    /**
     * 获取top包名
     * @param context
     * @return
     */
    public static String getTopPackageName(Context context) {
        String pkgName = null;
        ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
                ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
                if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (topAppProcess.pkgList != null && topAppProcess.pkgList.length > 0) {
                        return topAppProcess.pkgList[0];
                    } else {
                        pkgName = topAppProcess.processName;
                    }
                }
            } else {
                //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
                List localList = manager.getRunningTasks(1);
                ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList.get(0);
                pkgName = localRunningTaskInfo.topActivity.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkgName;
    }
}
