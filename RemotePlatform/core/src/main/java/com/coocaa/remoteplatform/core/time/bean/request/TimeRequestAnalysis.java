package com.coocaa.remoteplatform.core.time.bean.request;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TimeRequestAnalysis {

    public static int convertSwtich(int value) {
        int convertValue = 0;

        if (value == 0) convertValue = 0;
        if (value == 1) convertValue = 2;
        if (value == 2) convertValue = 3;

        return convertValue;
    }

    public static String parseContent(TimeRequestInfo info) {
        if (info == null) return null;

        TimeRequestBean content = null;
        switch (info.cycleMode) {
            case 0:   //一次性定时
                content = parseDelayContent(info);
                break;
            case 1:   //每天定时
                content = parseDayContent(info);
                break;
            case 2:   //每周定时
                content = parseWeekContent(info);
                break;
            case 3:   //每月定时
                content = parseMonthContent(info);
                break;
        }

        final Gson gsonBuilder = new GsonBuilder().create();
        java.lang.reflect.Type type = new TypeToken<TimeRequestBean>() {
        }.getType();
        try {
            String json = gsonBuilder.toJson(content, type);

            if (json == null) return null;

            return json;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("time-api", "request parse content e=" + e);
        }

        return null;
    }

    //command=1, volume=0, cycleMode=0, executeTime='11:02:37', runDate='2021-09-03', isOpen=1, deviceId='6574334c7527645511eed9b13d698f2c'
    private static TimeRequestBean parseDelayContent(TimeRequestInfo info) {
        TimeRequestBean content = new TimeRequestBean();
        content.setPlanType(1);
        content.setSwitchX(convertSwtich(info.command));
        content.setVolume(info.volume);
        content.setDelay(info.runDate + " " + info.executeTime);  //必须写
        List<String> time = new ArrayList<>();
        time.add(info.executeTime);
        content.setTime(time);

        return content;
    }

    //command=0, volume=0, cycleMode=1, executeTime='11:02:30', runDate='null', isOpen=1, deviceId='6574334c7527645511eed9b13d698f2c'
    private static TimeRequestBean parseDayContent(TimeRequestInfo info) {
        TimeRequestBean content = new TimeRequestBean();
        content.setPlanType(2);
        content.setSwitchX(convertSwtich(info.command));
        content.setVolume(info.volume);
        List<String> time = new ArrayList<>();
        time.add(info.executeTime);
        content.setTime(time);  //必须写

        return content;
    }

    //command=1, volume=0, cycleMode=2, executeTime='11:02:37', runDate='2,3', isOpen=1, deviceId='6574334c7527645511eed9b13d698f2c'
    private static TimeRequestBean parseWeekContent(TimeRequestInfo info) {
        TimeRequestBean content = new TimeRequestBean();
        content.setPlanType(5);
        content.setSwitchX(convertSwtich(info.command));
        content.setVolume(info.volume);
        List<String> time = new ArrayList<>();
        time.add(info.executeTime);
        content.setTime(time);

        String[] weekDay = info.runDate.split(",");
        if (weekDay != null) {
            List<TimeRequestBean.DayBean> weekList = new ArrayList<>();
            for (String week : weekDay) {
                try {
                    TimeRequestBean.DayBean weekBean = new TimeRequestBean.DayBean();
                    weekBean.setNumber(Integer.valueOf(week).intValue());
                    weekBean.setTimeX(time);
                    weekList.add(weekBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            content.setWeek(weekList);   //必须写
        }

        return content;
    }

    //command=2, volume=50, cycleMode=3, executeTime='16:31:39', runDate='10,11,12,13,14,15', isOpen=1, deviceId='6574334c7527645511eed9b13d698f2c'
    private static TimeRequestBean parseMonthContent(TimeRequestInfo info) {
        TimeRequestBean content = new TimeRequestBean();
        content.setPlanType(6);
        content.setSwitchX(convertSwtich(info.command));
        content.setVolume(info.volume);
        List<String> time = new ArrayList<>();
        time.add(info.executeTime);
        content.setTime(time);

        String[] monthDay = info.runDate.split(",");
        if (monthDay != null) {
            List<TimeRequestBean.DayBean> monthList = new ArrayList<>();
            for (String month : monthDay) {
                try {
                    TimeRequestBean.DayBean monthBean = new TimeRequestBean.DayBean();
                    monthBean.setNumber(Integer.valueOf(month).intValue());
                    monthBean.setTimeX(time);
                    monthList.add(monthBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            content.setMonth(monthList);   //必须写
        }

        return content;
    }
}
