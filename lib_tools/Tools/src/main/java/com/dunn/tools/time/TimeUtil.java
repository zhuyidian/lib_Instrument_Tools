package com.dunn.tools.time;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class TimeUtil {
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
    public static long localToGtm(long timeMillisLocal) {
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
    public static long gtmToLocal(long timeMillisGtm) {
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
     * 说明：将date转化为时间字符串
     * @param date
     * @return
     */
    public static String date2string(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 说明：将时间字符串转化为date
     * 输入：timeStr 时间字符串 (yyyy-MM-dd HH:mm:ss)
     * 返回：Date
     */
    public static Date string2date(String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
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
     * 说明：根据天数获取天的所有小时段
     * 输入：N_days 天数
     * 返回：ArrayList 天数对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getN_day_Pamams(final Context context, int N_days, final String UID) {
        ArrayList<String> dayList = TimeUtil.getDayHoursList(N_days);
        ArrayList<String> params = new ArrayList<>();

        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;
        for (int i = 0; i < dayList.size(); i++) {
            for (int n = 0; n < 24; n++) {
                //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
                long time = TimeUtil.localToGtm(TimeUtil.string2long(dayList.get(i) + " " + TimeUtil.timeAdd0(n) + ":00:00"));
//                if (time > insertTime) {
                String s_time = TimeUtil.long2string(time);
                String arr_item[] = s_time.split(" ");
                params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
            }
        }

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定天数得到需要获取数据的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }

    /**
     * 说明：根据时间戳获取前后hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     * hourNum 前后多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBAPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeAdd, timeDesc;
        timeAdd = timeDesc = TimeUtil.setIntegerHour(timeMillis);
        temp.add(timeAdd);
        for (int i = 0; i < hourNum; i++) {
            timeAdd = TimeUtil.addHour(timeAdd);
            temp.add(timeAdd);
            timeDesc = TimeUtil.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for (int n = 0; n < temp.size(); n++) {
            long time = TimeUtil.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeUtil.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeUtil.sortA(params);

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定时间戳得到前后几小时的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }

    /**
     * 说明：根据时间戳获取timeMillis前hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     * hourNum 前多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeDesc;
        timeDesc = TimeUtil.setIntegerHour(timeMillis);
        temp.add(timeDesc);
        for (int i = 0; i < hourNum; i++) {
            timeDesc = TimeUtil.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for (int n = 0; n < temp.size(); n++) {
            long time = TimeUtil.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeUtil.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeUtil.sortA(params);

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定时间戳得到前几小时的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }
}
