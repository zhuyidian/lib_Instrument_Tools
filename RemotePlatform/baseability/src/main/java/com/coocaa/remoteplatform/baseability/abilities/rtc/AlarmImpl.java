package com.coocaa.remoteplatform.baseability.abilities.rtc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: Alarm
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 5:13 PM
 * @Description:
 */
public class AlarmImpl extends AbsAbility {
    private static final String TAG = "AlarmImpl";

    private static int second, hour, minute, year, month, day, dayOfWeek;

    @Override
    public void handleMessage(RemoteCommand command) {
        Gson gson = new Gson();
        Alarm alarm = gson.fromJson(command.content, Alarm.class);
        if (alarm == null) {
            command.replyError(mContext).reply();
            return;
        }
        SharedPreferences prefs = mContext.getSharedPreferences("switch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("switchJson", command.content);
        editor.commit();

        if (setAlarm(alarm, mContext)) {
            command.replyFinish(mContext).reply();
            return;
        }
        command.replyError(mContext).reply();
    }

    @Override
    public String getName() {
        return null;
    }

    public static boolean setAlarm(Alarm alarm, Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginTime = new Date();
        Date endTime = new Date();
        List<String> timePattern;
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        boolean isFindBeginTime = false;
        boolean isFindEndTime = false;
        switch (alarm.getPlanType()) {
            case 0:
                shutdown();
                break;
            case 1:
                break;
            case 2:
                List<String> timeList = alarm.getTime();
                String time = "";
                for (String t : timeList) {
                    time = t;
                }
                Log.d(TAG, "handleMessage: time: " + time);
                getDataByOffset(mCalendar, 0);
                //找出离目前最近的开关机时间
                while (!isFindBeginTime || !isFindEndTime) {
                    timePattern = timePattern(time);
                    if (timePattern.size() < 2) {
                        return false;
                    }
                    Date tempBeginTime;
                    Date tempEndTime;
                    try {
                        tempBeginTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(0));
                        tempEndTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }

                    if (!isFindBeginTime && (tempBeginTime.getTime() > System.currentTimeMillis())) {
                        beginTime = tempBeginTime;
                        isFindBeginTime = true;
                    }
                    if (!isFindEndTime && (tempEndTime.getTime() > System.currentTimeMillis())) {
                        endTime = tempEndTime;
                        isFindEndTime = true;
                    }
                    getDataByOffset(mCalendar, 1);
                }
                return setAlarm(beginTime, endTime, context);
            case 3:
                break;
            case 4:
                break;
            case 5:
                List<Alarm.Day> week = alarm.getWeek();
                if (week.size() <= 0) {
                    return false;
                }
                //返回的json数据是有序的
                getDataByOffset(mCalendar, 0);
                while (!isFindBeginTime || !isFindEndTime) {
                    for (Alarm.Day weekDay : week) {
                        if (weekDay.getNumber() >= dayOfWeek) {
                            getDataByOffset(mCalendar, weekDay.getNumber() - dayOfWeek);
                            timePattern = timePattern(weekDay.getTimeX());
                            if (timePattern.size() < 2) {
                                return false;
                            }
                            Date tempBeginTime;
                            Date tempEndTime;
                            try {
                                tempBeginTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(0));
                                tempEndTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(1));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return false;
                            }

                            if (!isFindBeginTime && (tempBeginTime.getTime() > System.currentTimeMillis())) {
                                beginTime = tempBeginTime;
                                isFindBeginTime = true;
                            }
                            if (!isFindEndTime && (tempEndTime.getTime() > System.currentTimeMillis())) {
                                endTime = tempEndTime;
                                isFindEndTime = true;
                            }
                            if (isFindBeginTime && isFindEndTime) {
                                break;
                            }
                        }
                    }
                    mCalendar = getNextMonday();
                    getDataByOffset(mCalendar, 0);
                }
                return setAlarm(beginTime, endTime, context);
        }
        return true;
    }

    //time: 00:00:00-23:59:59
    public static List<String> timePattern(String text) {
        //String dateStr = text.replaceAll("r?n", " ");
        List<String> matches = new ArrayList<>();
        try {
            Pattern p = Pattern.compile("(\\d{1,2}:\\d{1,2}:\\d{1,2})");
            Matcher matcher = p.matcher(text);
            while (matcher.find() && matcher.groupCount() >= 1) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String temp = matcher.group(i);
                    Log.d(TAG, "timePattern: " + temp);
                    matches.add(temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    public static void shutdown() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            try {
                Class<?> serviceManager = Class.forName("android.os.ServiceManager");
                Method getService = serviceManager.getMethod("getService", String.class);
                Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
                Method asInterface = stub.getMethod("asInterface", IBinder.class);
                Object powerManager = asInterface.invoke(null, remoteService);
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                shutdown.invoke(powerManager, false, "", true);
            } catch (Exception e) {
                e.printStackTrace();
                //nothing to do
            }
        }
    }

    private static boolean setAlarm(Date beginTime, Date endTime, Context context) {
        boolean shutdown = true;
        long temp = beginTime.getTime() - endTime.getTime();
        //关机和开机时间相隔小于五分钟，当做不关机处理
        //开机和关机时间相隔小于五分钟，正常处理。一般只发生在第一次设置开关机时间的时候，即当前时间小于开关机时间。开机和关机时间相隔太短，开机时间过长的话，有可能错过关机时间。
        if (temp <= 1000 * 60 * 5 && temp > 0) {
            Log.d(TAG, "setAlarm fail: 关机和开机时间相隔小于五分钟，当做不关机处理");
            shutdown = false;
        }
        Log.d(TAG, "setAlarm: beginTime: " + beginTime + " endTime:" + endTime);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("shutdown", shutdown);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //这里是模拟开机广播，当开机闹钟到达，但目前机器实际并不是从关机到开机的状态。即设定的开机时间小于关机时间的情况
        Intent bootIntent = new Intent(context, AlarmReceiver.class);
        bootIntent.putExtra("boot", true);
        PendingIntent bootPi = PendingIntent.getBroadcast(context, 1, bootIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //If the time occurs in the past, the alarm will be triggered immediately.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, beginTime.getTime(), bootPi);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime.getTime(), pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, beginTime.getTime(), bootPi);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, endTime.getTime(), pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, beginTime.getTime(), bootPi);
            alarmManager.set(AlarmManager.RTC_WAKEUP, endTime.getTime(), pi);
        }
        //设置开机时间
        return JNI.rtcSetTime(beginTime.getTime()) >= 0;
    }

    private static void getDataByOffset(Calendar mCalendar, int dayOffset) {
        mCalendar.add(Calendar.DAY_OF_MONTH, dayOffset);

        year = mCalendar.get(Calendar.YEAR);
        month = mCalendar.get(Calendar.MONTH) + 1;
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY) {
            dayOfWeek = 7;
        } else {
            dayOfWeek = dayOfWeek - 1;
        }
        hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        minute = mCalendar.get(Calendar.MINUTE);
        second = mCalendar.get(Calendar.SECOND);
        Log.d(TAG, "getDataByOffset: " + mCalendar);
    }

    private static Calendar getNextMonday() {
        Calendar cd = Calendar.getInstance();
        cd.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        // 获得入参日期是一周的第几天
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        // 获得入参日期相对于下周一的偏移量（在国外，星期一是一周的第二天，所以下周一是这周的第九天）
        // 若入参日期是周日，它的下周一偏移量是1
        int nextMondayOffset = dayOfWeek == 1 ? 1 : 9 - dayOfWeek;

        // 增加到入参日期的下周一
        cd.add(Calendar.DAY_OF_MONTH, nextMondayOffset);
        return cd;
    }

    private static class MyDate {
        public int year;
        public int month;
        public int day;

        public MyDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
}
