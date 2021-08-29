package com.dunn.tools.time.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeBean {
    /**
     * planType :
     *          -1:取消控制
     *          0:立即实时
     *          1:延时（一次性）
     *          2:每日开启
     *          3:法定节假日(中国大陆)
     *          4:法定工作日(中国大陆)
     *          5:每周(自定义)
     *          6:每月(自定义)
     * switch : 当planType为0时,此值为必须.
     *          0:表示立即关机
     *          1:表示立即开机
     *          2:表示重启
     *          3:表示音量
     * volume:音量绝对值，范围0-100，客户端针对音量范围不在该范围的进行百分比自适应
     * delay : 当planType为1时必填，表示一次性延迟开关机时间点 2021-03-05 23:59:59
     * time : 00:00:00-23:59:59 当planType为1、2、3、4，表示延迟或周期性开关机时间点,当planType为1、2、3、4、数组长度>=1
     * week : 为day[]数组,当planType为5时必填，day的数组，表示每周哪几天什么时候开关机。
     *          day.number  1:星期一 2:星期二 ... ... 6:星期六 7:星期天
     *          day.time  00:00:00-10:00:00, 12:00:00-14:00:00, 16:00:00-24:00
     * month: 为day[]数组, 当planType为6时必填，day的数组，表示每月哪几天什么时候开关机
     *          day.number 1:1号 2:2号...  1-31 表示1号到31号，需结合当前月份具体天数
     *          day.time  00:00:00-10:00:00, 12:00:00-14:00:00, 16:00:00-24:00
     */

    @SerializedName("planType")
    private int planType;
    @SerializedName("switch")
    private int switchX;
    @SerializedName("volume")
    private int volume;
    @SerializedName("delay")
    private String delay;
    @SerializedName("time")
    private List<String> time;
    @SerializedName("week")
    private List<DayBean> week;
    @SerializedName("month")
    private List<DayBean> month;

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

    public int getSwitchX() {
        return switchX;
    }

    public void setSwitchX(int switchX) {
        this.switchX = switchX;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<DayBean> getWeek() {
        return week;
    }

    public void setWeek(List<DayBean> week) {
        this.week = week;
    }

    public List<DayBean> getMonth() {
        return month;
    }

    public void setMonth(List<DayBean> month) {
        this.month = month;
    }

    public static class DayBean {
        /**
         * number : 1-7 表示周1-周7
         * time : 00:00:00-23:59:59
         */

        @SerializedName("number")
        private int number;
        @SerializedName("time")
        private List<String> timeX;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public List<String> getTimeX() {
            return timeX;
        }

        public void setTimeX(List<String> timeX) {
            this.timeX = timeX;
        }

        @Override
        public String toString() {
            return "DayBean{" +
                    "number=" + number +
                    ", timeX=" + timeX +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TimeBean{" +
                "planType=" + planType +
                ", switchX=" + switchX +
                ", volume=" + volume +
                ", delay='" + delay + '\'' +
                ", time=" + time +
                ", week=" + week +
                ", month=" + month +
                '}';
    }
}
