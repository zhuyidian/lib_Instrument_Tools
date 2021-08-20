package com.dunn.tools.TimeUtil;

import android.provider.ContactsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TimeConversion {
    /**
     * 说明：根据天数获取天的具体日期
     * 输入：days 天数
     * 返回：ArrayList 天对应的具体日期(yyyy-MM-dd ,按照降序排列)
     */
    public static ArrayList<String> getDayHoursList(int days) {
        ArrayList<String> list = new ArrayList<>();
        Calendar c = null;
        for (int i = 0; i < days; i++) {
            c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -i);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            list.add(year + "-" + timeAdd0(month) + "-" + timeAdd0(day));
        }

        return sortD(list);
    }
    /**
     * 说明：标准时间戳转格林时间戳
     * 输入：timeMillisLocal 标准时间戳
     * 返回：格林时间戳
     */
    public static long localToGtm(long timeMillisLocal){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillisLocal);
        cal.add(Calendar.HOUR, -8);
        return cal.getTimeInMillis();
    }
    /**
     * 说明：格林时间戳转标准时间戳
     * 输入：timeMillisGtm 格林时间戳
     * 返回：标准时间戳
     */
    public static long gtmToLocal(long timeMillisGtm){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillisGtm);
        cal.add(Calendar.HOUR, +8);
        return cal.getTimeInMillis();
    }
    /**
     * 说明：将时间字符串转化为时间戳
     * 输入：timeStr 时间字符串 (yyyy-MM-dd HH:mm:ss)
     * 返回：时间戳
     */
    public static long string2long(String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }
    /**
     * 说明：将时间戳转化为时间字符串
     * 输入：timeMillis 时间字符串 (yyyy-MM-dd HH:mm:ss)
     * 返回：时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String long2string(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeMillis);
    }
    /**
     * 说明：补全时间
     * 输入：num 具体时间值
     * 返回：补全的时间值
     */
    public static String timeAdd0(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }
    /**
     * 说明：将时间戳对应的时间设置成整点 (分，秒设置成0)
     * 输入：timeMillis 时间戳
     * 返回：整点时间戳
     */
    public static long setIntegerHour(long timeMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH)+1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int miniter = cal.get(Calendar.MINUTE);
//        int second = cal.get(Calendar.SECOND);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }
    /**
     * 说明：将时间戳对应的时间小时add
     * 输入：timeMillis 时间戳
     * 返回：小时增加后的时间戳
     */
    public static long addHour(long timeMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        cal.add(Calendar.HOUR, +1);
        return cal.getTimeInMillis();
    }
    /**
     * 说明：将时间戳对应的时间小时desc
     * 输入：timeMillis 时间戳
     * 返回：小时减小后的时间戳
     */
    public static long descHour(long timeMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        cal.add(Calendar.HOUR, -1);
        return cal.getTimeInMillis();
    }

    public static ArrayList<String> sortD(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String cloudDate, String t1) {
                return (t1).compareTo(cloudDate);
            }
        });

        return list;
    }

    public static ArrayList<String> sortA(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String cloudDate, String t1) {
                return (cloudDate).compareTo(t1);
            }
        });

        return list;
    }
}
