package com.dunn.tools.time;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.bean.TimeBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

public class TimeAnalysis {

    /**
     * 解析content
     * @param content
     * @return null:解析失败  TimeBean:解析成功
     */
    public static TimeBean parseContent(String content){
        if(content==null) return null;

        final Gson gsonBuilder = new GsonBuilder().create();
        java.lang.reflect.Type type = new TypeToken<TimeBean>() {}.getType();
        try {
            //TimeBean plan = gson.fromJson(content, TimeBean.class);
            final TimeBean timeBean = gsonBuilder.fromJson(content, type);

            if(timeBean==null) return null;

            return timeBean;
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("time", "content=" + content+", e="+e);
        }

        return null;
    }

    /**
     * 获取delay timetask的时间戳，确定的固定时间戳
     * @param delay
     * @return 0:失败&没有未来时间戳  其它:未来的时间戳
     */
    public static long getDelayTime(String delay){
        if(delay==null) return 0l;

        try {
            long timeLong = TimeUtil.string2long(delay);
            return timeLong;
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("time", "delay=" + delay+", e="+e);
        }

        return 0l;
    }

    /**
     * 获取day timetask的时间戳，当天的未来时间戳
     * @param time
     * @param currentTime
     * @return 0:失败&没有当天的未来时间戳  其它:当天的未来时间戳
     */
    public static long getDayTime(List<String> time, long currentTime){
        if(time==null) return 0l;

        try {
            List<Long> timeLong = new ArrayList<>();
            for (int i = 0; i < time.size(); i++) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                mCalendar.add(Calendar.DAY_OF_MONTH, 0);
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                long value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + time.get(i));
                timeLong.add(value);

                mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                year = mCalendar.get(Calendar.YEAR);
                month = mCalendar.get(Calendar.MONTH) + 1;
                day = mCalendar.get(Calendar.DAY_OF_MONTH);
                value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + time.get(i));
                timeLong.add(value);
            }

            Collections.sort(timeLong, new Comparator<Long>() {
                @Override
                public int compare(Long t1, Long t2) {
                    return t2 > t1 ? -1 : 1;
                }
            });

            if (timeLong == null || timeLong.isEmpty()) return 0l;
            for (int i = 0; i < timeLong.size(); i++) {
                if (timeLong.get(i) > currentTime) {
                    return timeLong.get(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("time", "time=" + time+", e="+e);
        }

        return 0l;
    }

    /**
     * 获取week timetask的时间戳，本周的未来时间戳
     * @param time
     * @param currentTime
     * @return 0:失败&没有本周的未来时间戳  其它:本周的未来时间戳
     */
    public static long getWeekTime(List<TimeBean.DayBean> time, long currentTime){
        if(time==null) return 0l;

        try {
            List<Long> timeLong = new ArrayList<>();
            for (int i = 0; i < time.size(); i++) {
                int week = time.get(i).getNumber();
                List<String> timeValue = time.get(i).getTimeX();

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                mCalendar.add(Calendar.DAY_OF_MONTH, 0);
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SUNDAY) {
                    dayOfWeek = 7;
                } else {
                    dayOfWeek = dayOfWeek - 1;
                }
                mCalendar.add(Calendar.DAY_OF_MONTH, week - dayOfWeek);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                if (timeValue == null) continue;
                for (int j = 0; j < timeValue.size(); j++) {
                    long value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + timeValue.get(j));
                    timeLong.add(value);
                }

                mCalendar.add(Calendar.DAY_OF_MONTH, 7);
                year = mCalendar.get(Calendar.YEAR);
                month = mCalendar.get(Calendar.MONTH) + 1;
                day = mCalendar.get(Calendar.DAY_OF_MONTH);
                for (int j = 0; j < timeValue.size(); j++) {
                    long value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + timeValue.get(j));
                    timeLong.add(value);
                }
            }

            Collections.sort(timeLong, new Comparator<Long>() {
                @Override
                public int compare(Long t1, Long t2) {
                    return t2 > t1 ? -1 : 1;
                }
            });

            if (timeLong == null || timeLong.isEmpty()) return 0l;
            for (int i = 0; i < timeLong.size(); i++) {
                if (timeLong.get(i) > currentTime) {
                    return timeLong.get(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("time", "time=" + time+", e="+e);
        }

        return 0l;
    }

    /**
     * 获取month timetask的时间戳，本月的未来时间戳
     * @param time
     * @param currentTime
     * @return 0:失败&没有本周的未来时间戳  其它:本月的未来时间戳
     */
    public static long getMonthTime(List<TimeBean.DayBean> time, long currentTime){
        if(time==null) return 0l;

        try {
            List<Long> timeLong = new ArrayList<>();
            for (int i = 0; i < time.size(); i++) {
                int monthDay = time.get(i).getNumber();
                List<String> timeValue = time.get(i).getTimeX();

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                int maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if(monthDay>maxDay){
                    continue;
                }
                mCalendar.set(Calendar.DAY_OF_MONTH, monthDay);
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                if (timeValue == null) continue;
                for (int j = 0; j < timeValue.size(); j++) {
                    long value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + timeValue.get(j));
                    timeLong.add(value);
                }

                mCalendar.add(Calendar.MONTH, 1);
                maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if(monthDay>maxDay){
                    continue;
                }
                mCalendar.set(Calendar.DAY_OF_MONTH, monthDay);
                year = mCalendar.get(Calendar.YEAR);
                month = mCalendar.get(Calendar.MONTH) + 1;
                day = mCalendar.get(Calendar.DAY_OF_MONTH);
                for (int j = 0; j < timeValue.size(); j++) {
                    long value = TimeUtil.string2long(year + "-" + month + "-" + day + " " + timeValue.get(j));
                    timeLong.add(value);
                }
            }

            Collections.sort(timeLong, new Comparator<Long>() {
                @Override
                public int compare(Long t1, Long t2) {
                    return t2 > t1 ? -1 : 1;
                }
            });

            if (timeLong == null || timeLong.isEmpty()) return 0l;
            for (int i = 0; i < timeLong.size(); i++) {
                if (timeLong.get(i) > currentTime) {
                    return timeLong.get(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("time", "time=" + time+", e="+e);
        }

        return 0l;
    }
}
