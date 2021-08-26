package com.dunn.tools.time;

import com.dunn.tools.log.LogUtil;
import com.google.gson.Gson;

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
 * Author:Administrator
 * Date:2021/8/25 11:10
 * Description:TimeDemo
 */
public class TimeDemo {
    private static int second, hour, minute, year, month, day, dayOfWeek;
    private static String content2 = "{\"planType\":2,\"time\":[\"02:01:08-23:59:59\"]}";
    private static String content5 = "{" +
            "\"planType\":5," +
            "\"switch\":1," +
            "\"delay\":\"00:00:00-23:59:59\"," +
            "\"week\":[{\"number\":3,\"time\":\"00:00:00-23:59:59\"},{\"number\":4,\"time\":\"00:00:00-23:59:59\"},{\"number\":5,\"time\":\"00:00:00-23:59:59\"},{\"number\":6,\"time\":\"00:00:00-23:59:59\"},{\"number\":7,\"time\":\"00:00:00-23:59:59\"}]" +
            "}";

    public static void init(){
        Gson gson = new Gson();


        TimeBean alarm = gson.fromJson(content5, TimeBean.class);
        LogUtil.i("timedemo","alarm="+alarm);
        if (alarm == null) {
            return;
        }

//        setAlarm(alarm);
    }

//    public static boolean setAlarm(TimeBean alarm) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date beginTime = new Date();
//        Date endTime = new Date();
//        List<String> timePattern;
//        Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        boolean isFindBeginTime = false;
//        boolean isFindEndTime = false;
//        LogUtil.i("timedemo","PlanType="+alarm.getPlanType());
//        LogUtil.i("timedemo","switch="+alarm.getSwitchX());
//        LogUtil.i("timedemo","delay="+alarm.getDelay());
//        LogUtil.i("timedemo","weak="+alarm.getWeek());
//        switch (alarm.getPlanType()) {
//            case 0:
//
//                break;
//            case 1:
//                break;
//            case 2:
//                List<String> timeList = alarm.getTime();
//                String time = "";
//                for (String t : timeList) {
//                    time = t;
//                }
//                LogUtil.i("timedemo","time="+time);
//                getDataByOffset(mCalendar, 0);
//                LogUtil.i("timedemo","------start------mCalendar="+TimeUtil.date2string(mCalendar.getTime()));
//                //找出离目前最近的开关机时间
//                while (!isFindBeginTime || !isFindEndTime) {
//                    timePattern = timePattern(time);
//                    LogUtil.i("timedemo","timePattern="+timePattern);
//                    if (timePattern.size() < 2) {
//                        return false;
//                    }
//                    Date tempBeginTime;
//                    Date tempEndTime;
//                    try {
//                        tempBeginTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(0));
//                        tempEndTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(1));
//                        LogUtil.i("timedemo","tempBeginTime="+TimeUtil.date2string(tempBeginTime)+
//                                ", tempEndTime="+TimeUtil.date2string(tempEndTime));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//
//                    LogUtil.i("timedemo","compare tempBeginTime&currentTime="+(tempBeginTime.getTime()-System.currentTimeMillis()));
//                    if (!isFindBeginTime && (tempBeginTime.getTime() > System.currentTimeMillis())) {
//                        beginTime = tempBeginTime;
//                        isFindBeginTime = true;
//                    }
//                    LogUtil.i("timedemo","compare tempEndTime&currentTime="+(tempEndTime.getTime()-System.currentTimeMillis()));
//                    if (!isFindEndTime && (tempEndTime.getTime() > System.currentTimeMillis())) {
//                        endTime = tempEndTime;
//                        isFindEndTime = true;
//                    }
//                    getDataByOffset(mCalendar, 1);
//                    LogUtil.i("timedemo","end mCalendar="+TimeUtil.date2string(mCalendar.getTime()));
//                }
//                LogUtil.i("timedemo","------end------beginTime="+TimeUtil.date2string(beginTime)+
//                        ", endTime="+TimeUtil.date2string(endTime));
//            case 3:
//                break;
//            case 4:
//                break;
//            case 5:
//                List<TimeBean.Day> week = alarm.getWeek();
//                LogUtil.i("timedemo","------start------week size="+(week!=null?week.size():"null"));
//                if (week.size() <= 0) {
//                    return false;
//                }
//                //返回的json数据是有序的
//                getDataByOffset(mCalendar, 0);
//                LogUtil.i("timedemo","mCalendar="+TimeUtil.date2string(mCalendar.getTime()));
//                while (!isFindBeginTime || !isFindEndTime) {
//                    for (TimeBean.Day weekDay : week) {
//                        LogUtil.i("timedemo","for week day="+weekDay.getNumber()+", dayOfWeek="+dayOfWeek);
//                        if (weekDay.getNumber() >= dayOfWeek) {
//                            getDataByOffset(mCalendar, weekDay.getNumber() - dayOfWeek);
//                            timePattern = timePattern(/*weekDay.getTimeX()*/null);
//                            if (timePattern.size() < 2) {
//                                return false;
//                            }
//                            Date tempBeginTime;
//                            Date tempEndTime;
//                            try {
//                                tempBeginTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(0));
//                                tempEndTime = simpleDateFormat.parse(year + "-" + month + "-" + day + " " + timePattern.get(1));
//                                LogUtil.i("timedemo","tempBeginTime="+TimeUtil.date2string(tempBeginTime)+
//                                        ", tempEndTime="+TimeUtil.date2string(tempEndTime));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                                return false;
//                            }
//
//                            LogUtil.i("timedemo","compare tempBeginTime&currentTime="+(tempBeginTime.getTime()-System.currentTimeMillis()));
//                            if (!isFindBeginTime && (tempBeginTime.getTime() > System.currentTimeMillis())) {
//                                beginTime = tempBeginTime;
//                                isFindBeginTime = true;
//                            }
//                            LogUtil.i("timedemo","compare tempEndTime&currentTime="+(tempEndTime.getTime()-System.currentTimeMillis()));
//                            if (!isFindEndTime && (tempEndTime.getTime() > System.currentTimeMillis())) {
//                                endTime = tempEndTime;
//                                isFindEndTime = true;
//                            }
//                            if (isFindBeginTime && isFindEndTime) {
//                                break;
//                            }
//                        }
//                    }
//                    mCalendar = getNextMonday();
//                    getDataByOffset(mCalendar, 0);
//                }
//                LogUtil.i("timedemo","------end------beginTime="+TimeUtil.date2string(beginTime)+
//                        ", endTime="+TimeUtil.date2string(endTime));
//                //return setAlarm(beginTime, endTime, context);
//        }
//        return true;
//    }

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
        LogUtil.i("timedemo","get current data year="+year+
                ", month="+month+
                ", day="+day+
                ", dayOfWeek="+dayOfWeek+
                ", hour="+hour+
                ", minute="+minute+
                ", second="+second);
    }

    //time: 00:00:00-23:59:59
    private static List<String> timePattern(String text) {
        //String dateStr = text.replaceAll("r?n", " ");
        List<String> matches = new ArrayList<>();
        try {
            Pattern p = Pattern.compile("(\\d{1,2}:\\d{1,2}:\\d{1,2})");
            Matcher matcher = p.matcher(text);
            while (matcher.find() && matcher.groupCount() >= 1) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String temp = matcher.group(i);
                    matches.add(temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
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
}
