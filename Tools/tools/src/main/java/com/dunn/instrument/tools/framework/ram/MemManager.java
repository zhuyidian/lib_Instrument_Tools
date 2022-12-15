package com.dunn.instrument.tools.framework.ram;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

import com.dunn.instrument.tools.framework.ams.AmsUtil;
import com.dunn.instrument.tools.framework.cpu.ProcCpuStat;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @ClassName: MemManager
 * @Author: ZhuYiDian
 * @CreateDate: 2022/12/15 16:53
 * @Description:
 */
public class MemManager {
    private ActivityManager activityManager;
    private ProcCpuStat procCpuStat;

    private static class MemManagerHolder {
        private static volatile MemManager mInstance = new MemManager();
    }

    private MemManager() {
    }

    public static MemManager getInstance() {
        return MemManagerHolder.mInstance;
    }

    public void init(Context context) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public ActivityManager.MemoryInfo getMemoryInfo(){
        return MemTools.getMemoryInfo(activityManager);
    }

    public ProcMemInfo getProcMemInfo(String pkgName) {
        ProcMemInfo mProcMemInfo = new ProcMemInfo();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcInfoList;
        runningAppProcInfoList = AmsUtil.getPidForPackageName(activityManager, pkgName);
        int[] pidArray = new int[runningAppProcInfoList.size()];
        for (int i = 0; i < runningAppProcInfoList.size(); i++) {
            pidArray[i] = runningAppProcInfoList.get(i).pid;
        }

        //获取内存信息，最耗时
        Debug.MemoryInfo[] memoryInfos;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            memoryInfos = MemTools.getProcessMemoryInfo(activityManager, pidArray);
        } else {
            memoryInfos = MemTools.getMemoryInfos(pidArray);
        }
        int memoryUnit = 1024;

        int totalProcMemPss = 0;
        int totalDalvikPss = 0;
        int totalNativePss = 0;
        int totalOtherPss = 0;
        StringBuilder text = new StringBuilder();
        int procNum = runningAppProcInfoList.size();
        for (int i = 0; i < procNum; i++) {
            text.append(" ").append(runningAppProcInfoList.get(i).processName).append(":").append(memoryInfos[i].getTotalPss() / memoryUnit).append("MB");

            totalProcMemPss += memoryInfos[i].getTotalPss();
            totalDalvikPss += memoryInfos[i].dalvikPss;
            totalNativePss += memoryInfos[i].nativePss;
            totalOtherPss += memoryInfos[i].otherPss;
            if (i == procNum - 1) {
                mProcMemInfo.totalPss = String.valueOf(totalProcMemPss / memoryUnit);
                mProcMemInfo.dalvikPss = String.valueOf(totalDalvikPss / memoryUnit);
                mProcMemInfo.nativePss = String.valueOf(totalNativePss / memoryUnit);
                mProcMemInfo.otherPss = String.valueOf(totalOtherPss / memoryUnit);
            }
        }

        return mProcMemInfo;
    }

    public static class ProcMemInfo {
        // pss
        public String totalPss;

        public String dalvikPss;

        public String nativePss;

        public String otherPss;
    }
}
