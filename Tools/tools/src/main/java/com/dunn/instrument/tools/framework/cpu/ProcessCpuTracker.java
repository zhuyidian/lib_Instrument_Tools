package com.dunn.instrument.tools.framework.cpu;


import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import android.os.SystemClock;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;

import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.reflect.Reflector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//安卓11版本
public class ProcessCpuTracker {
    public static final int PROC_SPACE_TERM = (int) ' ';
    public static final int PROC_PARENS = 0x200;
    public static final int PROC_OUT_LONG = 0x2000;
    public static final int PROC_OUT_STRING = 0x1000;
    public static final int PROC_COMBINE = 0x100;
    public static final int PROC_OUT_FLOAT = 0x4000;
    static final int PROCESS_STAT_MINOR_FAULTS = 0;
    static final int PROCESS_STAT_MAJOR_FAULTS = 1;
    static final int PROCESS_STAT_UTIME = 2;
    static final int PROCESS_STAT_STIME = 3;
    static final int PROCESS_FULL_STAT_MINOR_FAULTS = 1;
    static final int PROCESS_FULL_STAT_MAJOR_FAULTS = 2;
    static final int PROCESS_FULL_STAT_UTIME = 3;
    static final int PROCESS_FULL_STAT_STIME = 4;
    static final int PROCESS_FULL_STAT_VSIZE = 5;
    private static final String TAG = "ProcessCpuTracker";
    private static final boolean DEBUG = false;
    private static final boolean localLOGV = DEBUG || false;
    private static final int[] PROCESS_STATS_FORMAT = new int[]{
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_PARENS,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 10: minor faults
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 12: major faults
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 14: utime
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 15: stime
    };
    private static final int[] PROCESS_FULL_STATS_FORMAT = new int[]{
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_PARENS | PROC_OUT_STRING,    // 2: name
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 10: minor faults
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 12: major faults
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 14: utime
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 15: stime
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 23: vsize
    };
    private static final int[] SYSTEM_CPU_FORMAT = new int[]{
            PROC_SPACE_TERM | PROC_COMBINE,
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 1: user time
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 2: nice time
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 3: sys time
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 4: idle time
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 5: iowait time
            PROC_SPACE_TERM | PROC_OUT_LONG,                  // 6: irq time
            PROC_SPACE_TERM | PROC_OUT_LONG                   // 7: softirq time
    };
    private static final int[] LOAD_AVERAGE_FORMAT = new int[]{
            PROC_SPACE_TERM | PROC_OUT_FLOAT,                 // 0: 1 min
            PROC_SPACE_TERM | PROC_OUT_FLOAT,                 // 1: 5 mins
            PROC_SPACE_TERM | PROC_OUT_FLOAT                  // 2: 15 mins
    };
    private final static Comparator<Stats> sLoadComparator = new Comparator<Stats>() {
        public final int
        compare(Stats sta, Stats stb) {
            int ta = sta.rel_utime + sta.rel_stime;
            int tb = stb.rel_utime + stb.rel_stime;
            if (ta != tb) {
                return ta > tb ? -1 : 1;
            }
            if (sta.added != stb.added) {
                return sta.added ? -1 : 1;
            }
            if (sta.removed != stb.removed) {
                return sta.added ? -1 : 1;
            }
            return 0;
        }
    };
    private static final int READ_SIZE = 1024;
    /**
     * Stores user time and system time in jiffies.
     */
    private final long[] mProcessStatsData = new long[4];
    /**
     * Stores user time and system time in jiffies.  Used for
     * public API to retrieve CPU use for a process.  Must lock while in use.
     */
    private final long[] mSinglePidStatsData = new long[4];
    private final String[] mProcessFullStatsStringData = new String[6];
    private final long[] mProcessFullStatsData = new long[6];
    private final long[] mSystemCpuData = new long[7];
    private final float[] mLoadAverageData = new float[3];
    private final boolean mIncludeThreads;
    // How long a CPU jiffy is in milliseconds.
    private final long mJiffyMillis;
    private final ArrayList<Stats> mProcStats = new ArrayList<Stats>();
    private final ArrayList<Stats> mWorkingProcs = new ArrayList<Stats>();
    private float mLoad1 = 0;
    private float mLoad5 = 0;
    private float mLoad15 = 0;
    // All times are in milliseconds. They are converted from jiffies to milliseconds
    // when extracted from the kernel.
    private long mCurrentSampleTime;
    private long mLastSampleTime;
    private long mCurrentSampleRealTime;
    private long mLastSampleRealTime;
    private long mCurrentSampleWallTime;
    private long mLastSampleWallTime;
    private long mBaseUserTime;
    private long mBaseSystemTime;
    private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    private long mBaseSoftIrqTime;
    private long mBaseIdleTime;
    private int mRelUserTime;
    private int mRelSystemTime;
    private int mRelIoWaitTime;
    private int mRelIrqTime;
    private int mRelSoftIrqTime;
    private int mRelIdleTime;
    private boolean mRelStatsAreGood;
    private int[] mCurPids;
    private int[] mCurThreadPids;
    private boolean mWorkingProcsSorted;
    private boolean mFirst = true;

    public ProcessCpuTracker(boolean includeThreads) {
        mIncludeThreads = includeThreads;
        long jiffyHz = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jiffyHz = Os.sysconf(OsConstants._SC_CLK_TCK);
        } else {
            // jiffyHz =  Libcore.os.sysconf( libcore.io.OsConstants._SC_CLK_TCK);;
            jiffyHz = 1000;
        }
        mJiffyMillis = 1000 / jiffyHz;
    }

    public static boolean readProcFile(String file, int[] format,
                                       String[] outStrings, long[] outLongs, float[] outFloats) {
        try {
            return Reflector.on(Process.class).method("readProcFile", String.class, int[].class, String[].class, long[].class,
                    float[].class).call(file, format, outStrings, outLongs, outFloats);
        } catch (Reflector.ReflectedException e) {
            e.printStackTrace();
            LogUtil.i("CpuTracker","readProcFile: e="+e);
        }
        return false;
    }

    public static String readTerminatedProcFile(String path, byte terminator) {
        // Permit disk reads here, as /proc isn't really "on disk" and should be fast.
        // TODO: make BlockGuard ignore /proc/ and /sys/ files perhaps?
        final StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try (FileInputStream is = new FileInputStream(path)) {
            ByteArrayOutputStream byteStream = null;
            final byte[] buffer = new byte[READ_SIZE];
            while (true) {
                // Read file into buffer
                final int len = is.read(buffer);
                if (len <= 0) {
                    // If we've read nothing, we're done
                    break;
                }

                // Find the terminating character
                int terminatingIndex = -1;
                for (int i = 0; i < len; i++) {
                    if (buffer[i] == terminator) {
                        terminatingIndex = i;
                        break;
                    }
                }
                final boolean foundTerminator = terminatingIndex != -1;

                // If we have found it and the byte stream isn't initialized, we don't need to
                // initialize it and can return the string here
                if (foundTerminator && byteStream == null) {
                    return new String(buffer, 0, terminatingIndex);
                }

                // Initialize the byte stream
                if (byteStream == null) {
                    byteStream = new ByteArrayOutputStream(READ_SIZE);
                }

                // Write the whole buffer if terminator not found, or up to the terminator if found
                byteStream.write(buffer, 0, foundTerminator ? terminatingIndex : len);

                // If we've found the terminator, we can finish
                if (foundTerminator) {
                    break;
                }
            }

            // If the byte stream is null at the end, this means that we have read an empty file
            if (byteStream == null) {
                return "";
            }
            return byteStream.toString();
        } catch (IOException e) {
            if (DEBUG) {
                //Slog.d(TAG, "Failed to open proc file", e);
            }
            return null;
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    public void onLoadChanged(float load1, float load5, float load15) {
    }

    public int onMeasureProcessName(String name) {
        return 0;
    }

    public void init() {
        //if (DEBUG) Slog.v(TAG, "Init: " + this);
        mFirst = true;
        update();
    }

    public void update() {
        //if (DEBUG) Slog.v(TAG, "Update: " + this);

        final long nowUptime = SystemClock.uptimeMillis();
        final long nowRealtime = SystemClock.elapsedRealtime();
        final long nowWallTime = System.currentTimeMillis();

        final long[] sysCpu = mSystemCpuData;
        if (readProcFile("/proc/stat", SYSTEM_CPU_FORMAT,
                null, sysCpu, null)) {
            // Total user time is user + nice time.
            final long usertime = (sysCpu[0] + sysCpu[1]) * mJiffyMillis;
            // Total system time is simply system time.
            final long systemtime = sysCpu[2] * mJiffyMillis;
            // Total idle time is simply idle time.
            final long idletime = sysCpu[3] * mJiffyMillis;
            // Total irq time is iowait + irq + softirq time.
            final long iowaittime = sysCpu[4] * mJiffyMillis;
            final long irqtime = sysCpu[5] * mJiffyMillis;
            final long softirqtime = sysCpu[6] * mJiffyMillis;

            // This code is trying to avoid issues with idle time going backwards,
            // but currently it gets into situations where it triggers most of the time. :(
            if (true || (usertime >= mBaseUserTime && systemtime >= mBaseSystemTime
                    && iowaittime >= mBaseIoWaitTime && irqtime >= mBaseIrqTime
                    && softirqtime >= mBaseSoftIrqTime && idletime >= mBaseIdleTime)) {
                mRelUserTime = (int) (usertime - mBaseUserTime);
                mRelSystemTime = (int) (systemtime - mBaseSystemTime);
                mRelIoWaitTime = (int) (iowaittime - mBaseIoWaitTime);
                mRelIrqTime = (int) (irqtime - mBaseIrqTime);
                mRelSoftIrqTime = (int) (softirqtime - mBaseSoftIrqTime);
                mRelIdleTime = (int) (idletime - mBaseIdleTime);
                mRelStatsAreGood = true;

                if (DEBUG) {
//                    Slog.i("Load", "Total U:" + (sysCpu[0] * mJiffyMillis)
//                            + " N:" + (sysCpu[1] * mJiffyMillis)
//                            + " S:" + (sysCpu[2] * mJiffyMillis) + " I:" + (sysCpu[3] * mJiffyMillis)
//                            + " W:" + (sysCpu[4] * mJiffyMillis) + " Q:" + (sysCpu[5] * mJiffyMillis)
//                            + " O:" + (sysCpu[6] * mJiffyMillis));
//                    Slog.i("Load", "Rel U:" + mRelUserTime + " S:" + mRelSystemTime
//                            + " I:" + mRelIdleTime + " Q:" + mRelIrqTime);
                }

                mBaseUserTime = usertime;
                mBaseSystemTime = systemtime;
                mBaseIoWaitTime = iowaittime;
                mBaseIrqTime = irqtime;
                mBaseSoftIrqTime = softirqtime;
                mBaseIdleTime = idletime;

            } else {
                mRelUserTime = 0;
                mRelSystemTime = 0;
                mRelIoWaitTime = 0;
                mRelIrqTime = 0;
                mRelSoftIrqTime = 0;
                mRelIdleTime = 0;
                mRelStatsAreGood = false;
//                Slog.w(TAG, "/proc/stats has gone backwards; skipping CPU update");
                return;
            }
        }

        mLastSampleTime = mCurrentSampleTime;
        mCurrentSampleTime = nowUptime;
        mLastSampleRealTime = mCurrentSampleRealTime;
        mCurrentSampleRealTime = nowRealtime;
        mLastSampleWallTime = mCurrentSampleWallTime;
        mCurrentSampleWallTime = nowWallTime;

        final StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            mCurPids = collectStats("/proc", -1, mFirst, mCurPids, mProcStats);
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }

        final float[] loadAverages = mLoadAverageData;
        if (readProcFile("/proc/loadavg", LOAD_AVERAGE_FORMAT,
                null, null, loadAverages)) {
            float load1 = loadAverages[0];
            float load5 = loadAverages[1];
            float load15 = loadAverages[2];
            if (load1 != mLoad1 || load5 != mLoad5 || load15 != mLoad15) {
                mLoad1 = load1;
                mLoad5 = load5;
                mLoad15 = load15;
                onLoadChanged(load1, load5, load15);
            }
        }

//        if (DEBUG) Slog.i(TAG, "*** TIME TO COLLECT STATS: "
//                + (SystemClock.uptimeMillis() - mCurrentSampleTime));

        mWorkingProcsSorted = false;
        mFirst = false;
    }

    private int[] collectStats(String statsFile, int parentPid, boolean first,
                               int[] curPids, ArrayList<Stats> allProcs) {

        int[] pids = new int[0];//= Process.getPids(statsFile, curPids);
        try {
            pids = Reflector.on(Process.class).method("getPids", String.class, int[].class).call(statsFile, curPids);
        } catch (Reflector.ReflectedException e) {
            e.printStackTrace();
        }
        int NP = (pids == null) ? 0 : pids.length;
        int NS = allProcs.size();
        int curStatsIndex = 0;
        for (int i = 0; i < NP; i++) {
            int pid = pids[i];
            if (pid < 0) {
                NP = pid;
                break;
            }
            Stats st = curStatsIndex < NS ? allProcs.get(curStatsIndex) : null;

            if (st != null && st.pid == pid) {
                // Update an existing process...
                st.added = false;
                st.working = false;
                curStatsIndex++;
//                if (DEBUG) Slog.v(TAG, "Existing "
//                        + (parentPid < 0 ? "process" : "thread")
//                        + " pid " + pid + ": " + st);

                if (st.interesting) {
                    final long uptime = SystemClock.uptimeMillis();

                    final long[] procStats = mProcessStatsData;
                    if (readProcFile(st.statFile,
                            PROCESS_STATS_FORMAT, null, procStats, null)) {
                        continue;
                    }

                    final long minfaults = procStats[PROCESS_STAT_MINOR_FAULTS];
                    final long majfaults = procStats[PROCESS_STAT_MAJOR_FAULTS];
                    final long utime = procStats[PROCESS_STAT_UTIME] * mJiffyMillis;
                    final long stime = procStats[PROCESS_STAT_STIME] * mJiffyMillis;

                    if (utime == st.base_utime && stime == st.base_stime) {
                        st.rel_utime = 0;
                        st.rel_stime = 0;
                        st.rel_minfaults = 0;
                        st.rel_majfaults = 0;
                        if (st.active) {
                            st.active = false;
                        }
                        continue;
                    }

                    if (!st.active) {
                        st.active = true;
                    }

                    if (parentPid < 0) {
                        getName(st, st.cmdlineFile);
                        if (st.threadStats != null) {
                            mCurThreadPids = collectStats(st.threadsDir, pid, false,
                                    mCurThreadPids, st.threadStats);
                        }
                    }

//                    if (DEBUG) Slog.v("Load", "Stats changed " + st.name + " pid=" + st.pid
//                            + " utime=" + utime + "-" + st.base_utime
//                            + " stime=" + stime + "-" + st.base_stime
//                            + " minfaults=" + minfaults + "-" + st.base_minfaults
//                            + " majfaults=" + majfaults + "-" + st.base_majfaults);

                    st.rel_uptime = uptime - st.base_uptime;
                    st.base_uptime = uptime;
                    st.rel_utime = (int) (utime - st.base_utime);
                    st.rel_stime = (int) (stime - st.base_stime);
                    st.base_utime = utime;
                    st.base_stime = stime;
                    st.rel_minfaults = (int) (minfaults - st.base_minfaults);
                    st.rel_majfaults = (int) (majfaults - st.base_majfaults);
                    st.base_minfaults = minfaults;
                    st.base_majfaults = majfaults;
                    st.working = true;
                }

                continue;
            }

            if (st == null || st.pid > pid) {
                // We have a new process!
                st = new Stats(pid, parentPid, mIncludeThreads);
                allProcs.add(curStatsIndex, st);
                curStatsIndex++;
                NS++;
//                if (DEBUG) Slog.v(TAG, "New "
//                        + (parentPid < 0 ? "process" : "thread")
//                        + " pid " + pid + ": " + st);

                final String[] procStatsString = mProcessFullStatsStringData;
                final long[] procStats = mProcessFullStatsData;
                st.base_uptime = SystemClock.uptimeMillis();
                String path = st.statFile;
                //Slog.d(TAG, "Reading proc file: " + path);
                if (readProcFile(path, PROCESS_FULL_STATS_FORMAT, procStatsString,
                        procStats, null)) {
                    // This is a possible way to filter out processes that
                    // are actually kernel threads...  do we want to?  Some
                    // of them do use CPU, but there can be a *lot* that are
                    // not doing anything.
                    st.vsize = procStats[PROCESS_FULL_STAT_VSIZE];
                    if (true || procStats[PROCESS_FULL_STAT_VSIZE] != 0) {
                        st.interesting = true;
                        st.baseName = procStatsString[0];
                        st.base_minfaults = procStats[PROCESS_FULL_STAT_MINOR_FAULTS];
                        st.base_majfaults = procStats[PROCESS_FULL_STAT_MAJOR_FAULTS];
                        st.base_utime = procStats[PROCESS_FULL_STAT_UTIME] * mJiffyMillis;
                        st.base_stime = procStats[PROCESS_FULL_STAT_STIME] * mJiffyMillis;
                    } else {
//                        Slog.i(TAG, "Skipping kernel process pid " + pid
//                                + " name " + procStatsString[0]);
                        st.baseName = procStatsString[0];
                    }
                } else {
//                    Slog.w(TAG, "Skipping unknown process pid " + pid);
                    st.baseName = "<unknown>";
                    st.base_utime = st.base_stime = 0;
                    st.base_minfaults = st.base_majfaults = 0;
                }

                if (parentPid < 0) {
                    getName(st, st.cmdlineFile);
                    if (st.threadStats != null) {
                        mCurThreadPids = collectStats(st.threadsDir, pid, true,
                                mCurThreadPids, st.threadStats);
                    }
                } else if (st.interesting) {
                    st.name = st.baseName;
                    st.nameWidth = onMeasureProcessName(st.name);
                }

//                if (DEBUG) Slog.v("Load", "Stats added " + st.name + " pid=" + st.pid
//                        + " utime=" + st.base_utime + " stime=" + st.base_stime
//                        + " minfaults=" + st.base_minfaults + " majfaults=" + st.base_majfaults);

                st.rel_utime = 0;
                st.rel_stime = 0;
                st.rel_minfaults = 0;
                st.rel_majfaults = 0;
                st.added = true;
                if (!first && st.interesting) {
                    st.working = true;
                }
                continue;
            }

            // This process has gone away!
            st.rel_utime = 0;
            st.rel_stime = 0;
            st.rel_minfaults = 0;
            st.rel_majfaults = 0;
            st.removed = true;
            st.working = true;
            allProcs.remove(curStatsIndex);
            NS--;
//            if (DEBUG) Slog.v(TAG, "Removed "
//                    + (parentPid < 0 ? "process" : "thread")
//                    + " pid " + pid + ": " + st);
            // Decrement the loop counter so that we process the current pid
            // again the next time through the loop.
            i--;
            continue;
        }

        while (curStatsIndex < NS) {
            // This process has gone away!
            final Stats st = allProcs.get(curStatsIndex);
            st.rel_utime = 0;
            st.rel_stime = 0;
            st.rel_minfaults = 0;
            st.rel_majfaults = 0;
            st.removed = true;
            st.working = true;
            allProcs.remove(curStatsIndex);
            NS--;
//            if (localLOGV) Slog.v(TAG, "Removed pid " + st.pid + ": " + st);
        }

        return pids;
    }

    /**
     * Returns the total time (in milliseconds) spent executing in
     * both user and system code.  Safe to call without lock held.
     */
    public long getCpuTimeForPid(int pid) {
        synchronized (mSinglePidStatsData) {
            final String statFile = "/proc/" + pid + "/stat";
            final long[] statsData = mSinglePidStatsData;
            boolean result = readProcFile(statFile, PROCESS_STATS_FORMAT,
                    null, statsData, null);
            Log.e(TAG, "getCpuTimeForPid: " + result);
            if (result) {
                long time = statsData[PROCESS_STAT_UTIME]
                        + statsData[PROCESS_STAT_STIME];
                return time * mJiffyMillis;
            }
            return 0;
        }
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastUserTime() {
        return mRelUserTime;
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastSystemTime() {
        return mRelSystemTime;
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastIoWaitTime() {
        return mRelIoWaitTime;
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastIrqTime() {
        return mRelIrqTime;
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastSoftIrqTime() {
        return mRelSoftIrqTime;
    }

    /**
     * @return time in milliseconds.
     */
    final public int getLastIdleTime() {
        return mRelIdleTime;
    }

    final public boolean hasGoodLastStats() {
        return mRelStatsAreGood;
    }

    final public float getTotalCpuPercent() {
        int denom = mRelUserTime + mRelSystemTime + mRelIrqTime + mRelIdleTime;
        if (denom <= 0) {
            return 0;
        }
        return ((float) (mRelUserTime + mRelSystemTime + mRelIrqTime) * 100) / denom;
    }

    final void buildWorkingProcs() {
        if (!mWorkingProcsSorted) {
            mWorkingProcs.clear();
            final int N = mProcStats.size();
            for (int i = 0; i < N; i++) {
                Stats stats = mProcStats.get(i);
                if (stats.working) {
                    mWorkingProcs.add(stats);
                    if (stats.threadStats != null && stats.threadStats.size() > 1) {
                        stats.workingThreads.clear();
                        final int M = stats.threadStats.size();
                        for (int j = 0; j < M; j++) {
                            Stats tstats = stats.threadStats.get(j);
                            if (tstats.working) {
                                stats.workingThreads.add(tstats);
                            }
                        }
                        Collections.sort(stats.workingThreads, sLoadComparator);
                    }
                }
            }
            Collections.sort(mWorkingProcs, sLoadComparator);
            mWorkingProcsSorted = true;
        }
    }

    final public int countStats() {
        return mProcStats.size();
    }

    final public Stats getStats(int index) {
        return mProcStats.get(index);
    }

    final public List<Stats> getStats(FilterStats filter) {
        final ArrayList<Stats> statses = new ArrayList<>(mProcStats.size());
        final int N = mProcStats.size();
        for (int p = 0; p < N; p++) {
            Stats stats = mProcStats.get(p);
            if (filter.needed(stats)) {
                statses.add(stats);
            }
        }
        return statses;
    }

    final public int countWorkingStats() {
        buildWorkingProcs();
        return mWorkingProcs.size();
    }

    final public Stats getWorkingStats(int index) {
        return mWorkingProcs.get(index);
    }

//    final public String printCurrentLoad() {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new FastPrintWriter(sw, false, 128);
//        pw.print("Load: ");
//        pw.print(mLoad1);
//        pw.print(" / ");
//        pw.print(mLoad5);
//        pw.print(" / ");
//        pw.println(mLoad15);
//        pw.flush();
//        return sw.toString();
//    }

//    final public String printCurrentState(long now) {
//        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//        buildWorkingProcs();
//
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new FastPrintWriter(sw, false, 1024);
//
//        pw.print("CPU usage from ");
//        if (now > mLastSampleTime) {
//            pw.print(now - mLastSampleTime);
//            pw.print("ms to ");
//            pw.print(now - mCurrentSampleTime);
//            pw.print("ms ago");
//        } else {
//            pw.print(mLastSampleTime - now);
//            pw.print("ms to ");
//            pw.print(mCurrentSampleTime - now);
//            pw.print("ms later");
//        }
//        pw.print(" (");
//        pw.print(sdf.format(new Date(mLastSampleWallTime)));
//        pw.print(" to ");
//        pw.print(sdf.format(new Date(mCurrentSampleWallTime)));
//        pw.print(")");
//
//        long sampleTime = mCurrentSampleTime - mLastSampleTime;
//        long sampleRealTime = mCurrentSampleRealTime - mLastSampleRealTime;
//        long percAwake = sampleRealTime > 0 ? ((sampleTime * 100) / sampleRealTime) : 0;
//        if (percAwake != 100) {
//            pw.print(" with ");
//            pw.print(percAwake);
//            pw.print("% awake");
//        }
//        pw.println(":");
//
//        final int totalTime = mRelUserTime + mRelSystemTime + mRelIoWaitTime
//                + mRelIrqTime + mRelSoftIrqTime + mRelIdleTime;
//
//        if (DEBUG) Slog.i(TAG, "totalTime " + totalTime + " over sample time "
//                + (mCurrentSampleTime - mLastSampleTime));
//
//        int N = mWorkingProcs.size();
//        for (int i = 0; i < N; i++) {
//            Stats st = mWorkingProcs.get(i);
//            printProcessCPU(pw, st.added ? " +" : (st.removed ? " -" : "  "),
//                    st.pid, st.name, (int) st.rel_uptime,
//                    st.rel_utime, st.rel_stime, 0, 0, 0, st.rel_minfaults, st.rel_majfaults);
//            if (!st.removed && st.workingThreads != null) {
//                int M = st.workingThreads.size();
//                for (int j = 0; j < M; j++) {
//                    Stats tst = st.workingThreads.get(j);
//                    printProcessCPU(pw,
//                            tst.added ? "   +" : (tst.removed ? "   -" : "    "),
//                            tst.pid, tst.name, (int) st.rel_uptime,
//                            tst.rel_utime, tst.rel_stime, 0, 0, 0, 0, 0);
//                }
//            }
//        }
//
//        printProcessCPU(pw, "", -1, "TOTAL", totalTime, mRelUserTime, mRelSystemTime,
//                mRelIoWaitTime, mRelIrqTime, mRelSoftIrqTime, 0, 0);
//
//        pw.flush();
//        return sw.toString();
//    }

    private void printRatio(PrintWriter pw, long numerator, long denominator) {
        long thousands = (numerator * 1000) / denominator;
        long hundreds = thousands / 10;
        pw.print(hundreds);
        if (hundreds < 10) {
            long remainder = thousands - (hundreds * 10);
            if (remainder != 0) {
                pw.print('.');
                pw.print(remainder);
            }
        }
    }

    private void printProcessCPU(PrintWriter pw, String prefix, int pid, String label,
                                 int totalTime, int user, int system, int iowait, int irq, int softIrq,
                                 int minFaults, int majFaults) {
        pw.print(prefix);
        if (totalTime == 0) totalTime = 1;
        printRatio(pw, user + system + iowait + irq + softIrq, totalTime);
        pw.print("% ");
        if (pid >= 0) {
            pw.print(pid);
            pw.print("/");
        }
        pw.print(label);
        pw.print(": ");
        printRatio(pw, user, totalTime);
        pw.print("% user + ");
        printRatio(pw, system, totalTime);
        pw.print("% kernel");
        if (iowait > 0) {
            pw.print(" + ");
            printRatio(pw, iowait, totalTime);
            pw.print("% iowait");
        }
        if (irq > 0) {
            pw.print(" + ");
            printRatio(pw, irq, totalTime);
            pw.print("% irq");
        }
        if (softIrq > 0) {
            pw.print(" + ");
            printRatio(pw, softIrq, totalTime);
            pw.print("% softirq");
        }
        if (minFaults > 0 || majFaults > 0) {
            pw.print(" / faults:");
            if (minFaults > 0) {
                pw.print(" ");
                pw.print(minFaults);
                pw.print(" minor");
            }
            if (majFaults > 0) {
                pw.print(" ");
                pw.print(majFaults);
                pw.print(" major");
            }
        }
        pw.println();
    }

    private void getName(Stats st, String cmdlineFile) {
        String newName = st.name;
        if (st.name == null || st.name.equals("app_process")
                || st.name.equals("<pre-initialized>")) {
            String cmdName = readTerminatedProcFile(cmdlineFile, (byte) '\0');
            if (cmdName != null && cmdName.length() > 1) {
                newName = cmdName;
                int i = newName.lastIndexOf("/");
                if (i > 0 && i < newName.length() - 1) {
                    newName = newName.substring(i + 1);
                }
            }
            if (newName == null) {
                newName = st.baseName;
            }
        }
        if (st.name == null || !newName.equals(st.name)) {
            st.name = newName;
            st.nameWidth = onMeasureProcessName(st.name);
        }
    }

    public interface FilterStats {
        /**
         * Which stats to pick when filtering
         */
        boolean needed(Stats stats);
    }

    public static class Stats {
        public final int pid;
        public final int uid;
        final String statFile;
        final String cmdlineFile;
        final String threadsDir;
        final ArrayList<Stats> threadStats;
        final ArrayList<Stats> workingThreads;

//        public BatteryStatsImpl.Uid.Proc batteryStats;

        public boolean interesting;

        public String baseName;
        public String name;
        public int nameWidth;

        // vsize capture when process first detected; can be used to
        // filter out kernel processes.
        public long vsize;

        /**
         * Time in milliseconds.
         */
        public long base_uptime;

        /**
         * Time in milliseconds.
         */
        public long rel_uptime;

        /**
         * Time in milliseconds.
         */
        public long base_utime;

        /**
         * Time in milliseconds.
         */
        public long base_stime;

        /**
         * Time in milliseconds.
         */
        public int rel_utime;

        /**
         * Time in milliseconds.
         */
        public int rel_stime;

        public long base_minfaults;
        public long base_majfaults;
        public int rel_minfaults;
        public int rel_majfaults;

        public boolean active;
        public boolean working;
        public boolean added;
        public boolean removed;

        Stats(int _pid, int parentPid, boolean includeThreads) {
            pid = _pid;
            if (parentPid < 0) {
                final File procDir = new File("/proc", Integer.toString(pid));
                uid = getUid(procDir.toString());
                statFile = new File(procDir, "stat").toString();
                cmdlineFile = new File(procDir, "cmdline").toString();
                threadsDir = (new File(procDir, "task")).toString();
                if (includeThreads) {
                    threadStats = new ArrayList<Stats>();
                    workingThreads = new ArrayList<Stats>();
                } else {
                    threadStats = null;
                    workingThreads = null;
                }
            } else {
                final File procDir = new File("/proc", Integer.toString(
                        parentPid));
                final File taskDir = new File(
                        new File(procDir, "task"), Integer.toString(pid));
                uid = getUid(taskDir.toString());
                statFile = new File(taskDir, "stat").toString();
                cmdlineFile = null;
                threadsDir = null;
                threadStats = null;
                workingThreads = null;
            }
        }

        private static int getUid(String path) {
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    return Os.stat(path).st_uid;
//                } else {
//                    return Libcore.os.stat(path).st_uid;
//                }
//            } catch (ErrnoException e) {
////                Slog.w(TAG, "Failed to stat(" + path + "): " + e);
//                return -1;
//            }
            return -1;
        }
    }
}
