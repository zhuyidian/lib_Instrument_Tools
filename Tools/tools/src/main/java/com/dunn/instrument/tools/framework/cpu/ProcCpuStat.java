package com.dunn.instrument.tools.framework.cpu;

import android.app.ActivityManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProcCpuStat {

    static final int PROCESS_STAT_MINOR_FAULTS = 0;
    static final int PROCESS_STAT_MAJOR_FAULTS = 1;
    static final int PROCESS_STAT_UTIME = 2;
    static final int PROCESS_STAT_STIME = 3;
    static final int PROCESS_STAT_NUM_THREAD = 4;
    private static final String TAG = ProcCpuStat.class.getSimpleName();
    // ProcCpuStatUtil probe
    private static final int[] SYSTEM_CPU_FORMAT = new int[]{
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_COMBINE,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 1: user time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 2: nice time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 3: sys time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 4: idle time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 5: iowait time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG, // 6: irq time
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG  // 7: softirq time
    };
    private static final int[] PROCESS_FULL_STATS_FORMAT = new int[]{
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_PARENS,    // 2: name
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,                  // 10: minor faults
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,                  // 12: major faults
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,                  // 14: utime
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,                  // 15: stime
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,               //20: num_threads: 线程个数
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM,
            ProcessCpuTracker.PROC_SPACE_TERM | ProcessCpuTracker.PROC_OUT_LONG,                  // 23: vsize
    };
    private final long[] mSinglePidStatsData = new long[6];
    private long mBaseUserTime;
    private long mBaseSystemTime;
    private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    private long mBaseSoftIrqTime;
    private long mBaseIdleTime;
    private CpuRateInfo mBaseCpuRateInfo;
    private final HashMap<Integer, ProcStats> mBaseProcCpuStatMap = new HashMap<>();

    public void init() {
        mBaseCpuRateInfo = getCpuRateInfo();
    }

    public String longToSting(long time) {
        long hour = time / (60 * 60 * 1000);
        long minute = (time - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (time - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        return (hour == 0 ? "00" : (hour >= 10 ? hour : ("0" + hour))) + ":" + (minute == 0 ? "00" : (minute >= 10 ? minute : ("0" + minute))) + ":" + (second == 0 ? "00" : (second >= 10 ? second : ("0" + second)));
    }

    public List<String> getCpuTime() {
        List<String> result = new ArrayList<>();
        long[] sysCpu = new long[7];
        if (ProcessCpuTracker.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null)) {
            result.add("User time : " + longToSting(sysCpu[0]));
            result.add("Nice time : " + longToSting(sysCpu[1]));
            result.add("Sys time : " + longToSting(sysCpu[2]));
            result.add("Idle time : " + longToSting(sysCpu[3]));
            result.add("Iowait time : " + longToSting(sysCpu[4]));
            result.add("Irq time : " + longToSting(sysCpu[5]));
            result.add("Softirq time : " + longToSting(sysCpu[6]));

            long totalCpuTime = sysCpu[0] + sysCpu[1] + sysCpu[2] + sysCpu[3] + sysCpu[4] + sysCpu[5] + sysCpu[6];
            result.add("Total CPU time : " + longToSting(totalCpuTime));
        }

        return result;
    }

    public CpuRateInfo getCpuRateInfo() {
        CpuRateInfo mCpuRateInfo = new CpuRateInfo();
        long[] sysCpu = new long[7];
        if (ProcessCpuTracker.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null)) {
            mCpuRateInfo.idleTime = sysCpu[3];
            mCpuRateInfo.totalCpuTime = sysCpu[0] + sysCpu[1] + sysCpu[2] + sysCpu[3] + sysCpu[4] + sysCpu[5] + sysCpu[6];
            mCpuRateInfo.nonIdleTime = mCpuRateInfo.totalCpuTime - mCpuRateInfo.idleTime;
        }

        return mCpuRateInfo;
    }

    /**
     * 获取 CPU 使用率
     * CPU 使用率 =(CPU 非空闲时间2 - CPU 非空闲时间1) / (CPU 运行总时间2 - CPU 运行总时间1) * 100%
     * https://www.jianshu.com/p/6bf564f7cdf0
     */
    public String getCpuUsage() {
        String result = null;
        CpuRateInfo mCpuRateInfo1 = getCpuRateInfo();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CpuRateInfo mCpuRateInfo2 = getCpuRateInfo();

        double usage = ((mCpuRateInfo2.nonIdleTime - mCpuRateInfo1.nonIdleTime) / (mCpuRateInfo2.totalCpuTime - mCpuRateInfo1.totalCpuTime)) * 100;
        result = (int) usage + "%";
        return result;
    }

    public List<ProcStats> getCpuUsage(List<ActivityManager.RunningAppProcessInfo> pids) {
        List<ProcStats> startList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.0000");
        long start = System.nanoTime();
        //fix:其他进程插入导致重新监测时cpu利用率飙升，分母还是隔一秒更新，指定监控的没这个问题
        for (ActivityManager.RunningAppProcessInfo processInfo : pids) {
            startList.add(getCpuTimeForPid(processInfo.pid));
        }
        Log.e(TAG, "getCpuUsage: cost  " + (System.nanoTime() - start) + "ns");
        CpuRateInfo cpuRateInfo = getCpuRateInfo();
        //new ProcessCpuTracker(false).getCpuTimeForPid(pid);

        double cpuUsage;
        if (mBaseCpuRateInfo != null) {
            double usage = (cpuRateInfo.nonIdleTime - mBaseCpuRateInfo.nonIdleTime) / (cpuRateInfo.totalCpuTime - mBaseCpuRateInfo.totalCpuTime);
            //startList.get(0).cpuUsage = Double.parseDouble(df.format(usage));
            cpuUsage = Double.parseDouble(df.format(usage));
        } else {
            //startList.get(0).cpuUsage = -1;
            cpuUsage = -1;
        }
        for (int i = 0; i < startList.size(); i++) {
            ProcStats procStats = mBaseProcCpuStatMap.get(pids.get(i).pid);
            if (procStats != null && (startList.get(i).timeStamp - procStats.timeStamp) <= 1000 * 1 + 1000) {
                double usage2 = (startList.get(i).cpuTime - procStats.cpuTime) / (cpuRateInfo.totalCpuTime - mBaseCpuRateInfo.totalCpuTime);
                startList.get(i).procCpuUsage = Double.parseDouble(df.format(usage2));
            } else {
                startList.get(i).procCpuUsage = -1;
            }
            mBaseProcCpuStatMap.put(pids.get(i).pid, startList.get(i));
        }
        mBaseCpuRateInfo = cpuRateInfo;
        //避免传入的pids数组长度为0，导致数组越界
        if (startList.size() == 0) {
            ProcStats tmp = new ProcStats(0, 0, 0, 0, 0);
            tmp.cpuUsage = cpuUsage;
            startList.add(tmp);
        } else {
            startList.get(0).cpuUsage = cpuUsage;
        }
        return startList;
    }

    public ProcStats getCpuTimeForPid(int pid) {
        Log.e(TAG, "getCpuTimeForPid: pid=" + pid);
        final String statFile = "/proc/" + pid + "/stat";
        final long[] statsData = mSinglePidStatsData;
        boolean result = ProcessCpuTracker.readProcFile(statFile, PROCESS_FULL_STATS_FORMAT,
                null, statsData, null);
        Log.e(TAG, "getCpuTimeForPid: " + result);
        if (result) {
            long time = statsData[PROCESS_STAT_UTIME]
                    + statsData[PROCESS_STAT_STIME];
            return new ProcStats(pid, statsData[PROCESS_STAT_NUM_THREAD], time, 0, System.currentTimeMillis());
        }
        return new ProcStats(pid);
    }

    public static class CpuRateInfo {
        // CPU 运行总时间
        double totalCpuTime;

        // CPU 空闲时间
        double idleTime;

        // CPU 非空闲时间
        double nonIdleTime;
    }

    public static class ProcStats {
        private final int pid;
        private long threadNum;
        private long cpuTime;
        private int memUsage;
        private double procCpuUsage;
        private double cpuUsage;
        private long timeStamp;

        public ProcStats(int pid) {
            this.pid = pid;
        }

        public ProcStats(int pid, long threadNum, long cpuTime, int memUsage, long timeStamp) {
            this.pid = pid;
            this.threadNum = threadNum;
            this.cpuTime = cpuTime;
            this.memUsage = memUsage;
            this.timeStamp = timeStamp;
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public double getProcCpuUsage() {
            return procCpuUsage;
        }

        public void setProcCpuUsage(double procCpuUsage) {
            this.procCpuUsage = procCpuUsage;
        }

        public int getMemUsage() {
            return memUsage;
        }

        public void setMemUsage(int memUsage) {
            this.memUsage = memUsage;
        }

        public long getThreadNum() {
            return threadNum;
        }

        public int getPid() {
            return pid;
        }

        // public final List<Long> mBaseProcCpuStatList = new ArrayList<>();
        //HashMap<Integer, Long> procCpuStatMap = new HashMap<>();
        // public String packageName;
        // String statFile;
        // String cmdlineFile;
        //  String threadsDir;
        //final ArrayList<ProcessCpuTracker.Stats> threadStats;
        //final ArrayList<ProcessCpuTracker.Stats> workingThreads;
    }

}
