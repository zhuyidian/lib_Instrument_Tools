package com.dunn.tools.usagestats;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.usage.UsageEvents.Event;

/**
 * Author:zhuyidian
 * Date:2021/7/26 16:16
 * Description:UsageStatsUtils
 */
public class UsageStatsUtils {
    private static final String TAG = "UsageStatsUtils$";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    public static void queryAndAggregateUsageStats(final Context mContext){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UsageStatsManager usageStatsManager = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
//                    CLog.d("UsageStats","onCreate: run before");
                    Map<String, UsageStats> map = usageStatsManager.queryAndAggregateUsageStats(0, System.currentTimeMillis());
                    printMap(map);

                    Collection<UsageStats> result = usageStatsManager.queryAndAggregateUsageStats(0, System.currentTimeMillis()).values();
                    List<UsageStats> list = new ArrayList<>(result);
                    Collections.sort(list, new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats o1, UsageStats o2) {
                            return (o1.getLastTimeUsed() - o2.getLastTimeUsed()) > 0 ? -1 : 1;
                        }
                    });
                    printList(list);
                }
            }
        }).start();
    }

    public static void getTopApp(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    UsageStatsManager m = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
                    if (m != null) {
                        long now = System.currentTimeMillis();
                        //获取10分钟之内的应用数据
                        List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 600 * 1000, now);
                        Collections.sort(stats, new Comparator<UsageStats>() {
                            @Override
                            public int compare(UsageStats o1, UsageStats o2) {
                                return (o1.getLastTimeUsed() - o2.getLastTimeUsed()) > 0 ? -1 : 1;
                            }
                        });
                        printList(stats);
                        String topActivity = "";
                        //取得最近运行的一个app，即当前运行的app
                        if ((stats != null) && (!stats.isEmpty())) {
                            int j = 0;
                            for (int i = 0; i < stats.size(); i++) {
                                if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                                    j = i;
                                }
                            }
                            topActivity = stats.get(j).getPackageName();
                        }
//                        CLog.d("UsageStats",TAG+"getTopApp:topActivity=" + topActivity);
                    }
                }
            }
        }).start();
    }

    public static void queryEvents(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    UsageStatsManager m = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
                    if (m != null) {
                        //获取10分钟之内的应用数据
                        long now = System.currentTimeMillis();
                        UsageEvents usageEvents = m.queryEvents(now - 600 * 1000, now);
                        HashMap<String, String> pkgNames = new HashMap<String, String>();
                        HashMap<String, Integer> times = new HashMap<String, Integer>();
                        while (usageEvents.hasNextEvent()) {
                            Event e = new Event();
                            usageEvents.getNextEvent(e);
                            if (e != null && e.getEventType() == 1) {
                                String packageName = e.getPackageName();
                                String className = e.getClassName();

//                                CLog.d("UsageStats",TAG+"queryEvents:packageName="+packageName+", className="+className+
//                                        ", getTimeStamp=" + e.getTimeStamp()+", EventType="+e.getEventType());

                                String clazz = pkgNames.get(packageName);
                                if (clazz == null || clazz.equals("")) {
                                    pkgNames.put(packageName, className);
                                    times.put(packageName, 1);
                                } else if (clazz.equals(className)) {
                                    times.put(packageName, times.get(packageName) + 1);
                                }
                            }
                        }
//                        CLog.d("UsageStats",TAG+"queryEvents:times="+times);
                    }
                }
            }
        }).start();
    }

    public static void printEvents(final Context mContext) throws NoSuchFieldException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.YEAR, -1);
                long startTime = calendar.getTimeInMillis();

//                CLog.d("UsageStats",TAG+"printEvents:Range start:" + dateFormat.format(startTime));
//                CLog.d("UsageStats",TAG+"printEvents:Range end:" + dateFormat.format(endTime));

                UsageStatsManager mUsmManager = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mUsmManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
                    UsageEvents events = mUsmManager.queryEvents(startTime, endTime);
                    HashMap<String, String> pkgNames = new HashMap<String, String>();
                    HashMap<String, Integer> times = new HashMap<String, Integer>();
                    while (events.hasNextEvent()) {
                        Event e = new Event();
                        events.getNextEvent(e);
                        if (e != null && e.getEventType() == 1) {
                            String packageName = e.getPackageName();
                            String className = e.getClassName();
                            String clazz = pkgNames.get(packageName);
                            if (clazz == null || clazz.equals("")) {
                                pkgNames.put(packageName, className);
                                times.put(packageName, 1);
                            } else if (clazz.equals(className)) {
                                times.put(packageName, times.get(packageName) + 1);
                            }
//                            CLog.d("UsageStats",TAG+"printEvents:----lasttime = " + DateUtils.formatSameDayTime(e.getTimeStamp(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM)
//                                    + "---- pkgname = " + packageName
//                                    + "----class = " + e.getClassName()
//                                    + "----type =" + e.getEventType());
                        }
                    }
//                    CLog.d("UsageStats",TAG+"printEvents: times="+times);
                }
            }
        }).start();
    }

    public static void printUsageStats(final Context mContext) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.YEAR, -1);
                long startTime = calendar.getTimeInMillis();
                UsageStatsManager mUsmManager = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mUsmManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
                    List<UsageStats> usageList = mUsmManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
//                    CLog.d("UsageStats",TAG+"printUsageStats:size=" + usageList.size());
                    for (UsageStats u : usageList) {
//                        try {
//                            CLog.d("UsageStats",TAG+"printUsageStats: PackageName=" + u.getPackageName()
//                                    + "----ForegroundTime = " + DateUtils.formatElapsedTime(u.getTotalTimeInForeground() / 1000)
//                                    + "----lasttimeuser = " + DateUtils.formatSameDayTime(u.getLastTimeUsed(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM)
//                                    + "----times = " + u.getClass().getDeclaredField("mLaunchCount").getInt(u));
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        } catch (NoSuchFieldException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        }).start();
    }

    public static void printAggregatedStats(Context mContext) throws NoSuchFieldException, IllegalAccessException {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        UsageStatsManager mUsmManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUsmManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            Map<String, UsageStats> map = mUsmManager.queryAndAggregateUsageStats(startTime, endTime);
            for (Map.Entry<String, UsageStats> entry : map.entrySet()) {
                UsageStats stats = entry.getValue();
//                CLog.d("UsageStats",TAG+"printAggregatedStats: " + entry.getKey() + "Pkg = " + stats.getPackageName()
//                        + "----ForegroundTime = " + DateUtils.formatElapsedTime(stats.getTotalTimeInForeground() / 1000)
//                        + "----lasttimeuser = " + DateUtils.formatSameDayTime(stats.getLastTimeUsed(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM)
//                        + "----times = " + stats.getClass().getDeclaredField("mLaunchCount").getInt(stats));
            }
        }
    }

    private static void printMap(Map<String, UsageStats> map){
//        CLog.d("UsageStats",TAG+"printMap:size=" + map.size());
        Iterator<Map.Entry<String, UsageStats>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsageStats> entry = it.next();
//            CLog.d("UsageStats",TAG+"printMap:key=" + entry.getKey() + ", value=" + entry.getValue());
        }
    }

    private static void printList(List<UsageStats> list) {
//        CLog.d("UsageStats",TAG+"printList:size=" + list.size());
        for (UsageStats usageStats : list) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                CLog.d("UsageStats",TAG+"printList:package=" + usageStats.getPackageName() + ", last time used=" + timestampToString(usageStats.getLastTimeUsed()));
            }
        }
    }

    private static String timestampToString(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(time);
    }
}
