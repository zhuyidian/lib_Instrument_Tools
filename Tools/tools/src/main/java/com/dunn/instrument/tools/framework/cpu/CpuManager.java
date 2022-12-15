package com.dunn.instrument.tools.framework.cpu;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.dunn.instrument.tools.framework.ams.AmsUtil;
import com.dunn.instrument.tools.framework.system.SystemUtil;
import com.dunn.instrument.tools.log.LogUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @ClassName: CpuManager
 * @Author: ZhuYiDian
 * @CreateDate: 2022/12/15 16:53
 * @Description:
 */
public class CpuManager {
    private ActivityManager activityManager;
    private ProcCpuStat procCpuStat;

    private static class CpuToolsHolder {
        private static volatile CpuManager mInstance = new CpuManager();
    }

    private CpuManager() {
    }

    public static CpuManager getInstance() {
        return CpuToolsHolder.mInstance;
    }

    public void init(Context context) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        procCpuStat = new ProcCpuStat();
    }

    public CpuInfo getCpuInfo(String pkgName) {
        CpuInfo mCpuInfo = new CpuInfo();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        StringBuilder text = new StringBuilder();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcInfoList;
        runningAppProcInfoList = AmsUtil.getPidForPackageName(activityManager, pkgName);
        int[] pidArray = new int[runningAppProcInfoList.size()];
        for (int i = 0; i < runningAppProcInfoList.size(); i++) {
            pidArray[i] = runningAppProcInfoList.get(i).pid;
        }
        List<ProcCpuStat.ProcStats> result = procCpuStat.getCpuUsage(runningAppProcInfoList);
        String cpuRate = decimalFormat.format(result.get(0).getCpuUsage() * 100) + "%";
        mCpuInfo.cpuRate = cpuRate;
        mCpuInfo.pkgName = pkgName;

        double totalProcCpuUsage = 0f;
        long totalProcThreadNum = 0;
        int procNum = runningAppProcInfoList.size();
        for (int i = 0; i < procNum; i++) {
            text.append(" ").append(runningAppProcInfoList.get(i).processName).append(":").append(decimalFormat.format(result.get(i).getProcCpuUsage() * 100)).append("%");
            totalProcCpuUsage += result.get(i).getProcCpuUsage();
            totalProcThreadNum += result.get(i).getThreadNum();
            if (i == procNum - 1) {
                String procCpuRate = decimalFormat.format(totalProcCpuUsage * 100) + "%";

                mCpuInfo.procCpuRate = procCpuRate;
                mCpuInfo.procProcessNum = String.valueOf(procNum);
                mCpuInfo.procThreadNum = String.valueOf(totalProcThreadNum);
            }
        }

        return mCpuInfo;
    }

    public static class CpuInfo {
        // CPU 总的使用
        String cpuRate;

        // 监控应用
        String pkgName;

        // 应用CPU占用
        String procCpuRate;

        // 应用进程数
        String procProcessNum;

        // 应用线程数
        String procThreadNum;
    }
}
