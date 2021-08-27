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
     * @return TimeBean
     */
    public static TimeBean parseContent(String content){
        if(content==null) return null;

        final Gson gsonBuilder = new GsonBuilder().create();
        java.lang.reflect.Type type = new TypeToken<TimeBean>() {}.getType();
        try {
            //TimeBean plan = gson.fromJson(content, TimeBean.class);
            final TimeBean bean = gsonBuilder.fromJson(content, type);

//            LogUtil.i("time", "bean=" + bean);
//            LogUtil.i("time", "planType=" + bean.getPlanType());
//            LogUtil.i("time", "switch=" + bean.getSwitchX());
//            LogUtil.i("time", "volume=" + bean.getVolume());
//            LogUtil.i("time", "delay=" + bean.getDelay());
//            if (bean.getTime() != null) {
//                List<String> listTime = bean.getTime();
//                int size = listTime.size();
//                LogUtil.i("time", "time.size=" + size);
//                for (int i = 0; i < size; i++) {
//                    LogUtil.i("time", "time.str=" + listTime.get(i));
//                }
//            } else {
//                LogUtil.i("time", "time=null");
//            }
//
//            if (bean.getWeek() != null) {
//                List<TimeBean.DayBean> listWeek = bean.getWeek();
//                int size = listWeek.size();
//                LogUtil.i("time", "week.size=" + size);
//                for (int i = 0; i < size; i++) {
//                    LogUtil.i("time", "week.number=" + listWeek.get(i).getNumber() + ", week.time=" + listWeek.get(i).getTimeX());
//                }
//            } else {
//                LogUtil.i("time", "week=null");
//            }
//
//            if (bean.getMonth() != null) {
//                List<TimeBean.DayBean> listMonth = bean.getMonth();
//                int size = listMonth.size();
//                LogUtil.i("time", "month.size=" + size);
//                for (int i = 0; i < size; i++) {
//                    LogUtil.i("time", "month.number=" + listMonth.get(i).getNumber() + ", month.time=" + listMonth.get(i).getTimeX());
//                }
//            } else {
//                LogUtil.i("time", "month=null");
//            }

            if(bean==null) return null;

            return bean;
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.i("time", "e=" + e);
        }

        return null;
    }

    /**
     * 获取delay timetask的时间戳
     * @param delay
     * @return 时间戳
     */
    public static long getDelayTime(String delay){
        if(delay==null) return 0l;

        try {
            long timeLong = TimeUtil.string2long(delay);
            return timeLong;
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.i("time", "e=" + e);
        }

        return 0l;
    }

    /**
     * 获取day timetask的时间戳 注：距离当前时间最近的未来的时间戳
     * @param time
     * @param currentTime
     * @return 时间戳
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
            LogUtil.i("time", "e=" + e);
        }

        return 0l;
    }

    /**
     * 获取week timetask的时间戳 注：距离当前时间最近的未来的时间戳
     * @param time
     * @param currentTime
     * @return 时间戳
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
                int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SUNDAY) {
                    dayOfWeek = 7;
                } else {
                    dayOfWeek = dayOfWeek - 1;
                }
                mCalendar.add(Calendar.DAY_OF_MONTH, week - dayOfWeek);
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                if (timeValue == null) continue;
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
            LogUtil.i("time", "e=" + e);
        }

        return 0l;
    }

    /**
     * 获取month timetask的时间戳 注：距离当前时间最近的未来的时间戳
     * @param time
     * @param currentTime
     * @return 时间戳
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
            LogUtil.i("time", "e=" + e);
        }

        return 0l;
    }
}
