package com.coocaa.remoteplatform.baseability.abilities.rtc;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Alarm {
    /**
     * planType : 0:立即实时开/关 1:延时（一次性）2:每日开启 3:法定节假日(中国大陆) 4:法定工作日(中国大陆) 5:每周(自定义)
     * switch : 当planType为0时,此值为必须. 0表示立即关机 1表示立即开机
     * delay : 当planType为1时必填，表示一次性延迟开关机时间点 2021-03-05 23:59:59
     * time : 00:00:00-23:59:59 当planType为1、2、3、4，表示延迟或周期性开关机时间点,当planType为1、2、3、4、数组长度>=1
     * week : []
     */

    @SerializedName("planType")
    private int planType;
    @SerializedName("switch")
    private int switchX;
    @SerializedName("delay")
    private String delay;
    @SerializedName("time")
    private List<String> time;
    @SerializedName("week")
    private List<Day> week;

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

    public List<Day> getWeek() {
        return week;
    }

    public void setWeek(List<Day> week) {
        this.week = week;
    }

    public static class Day {
        /**
         * number : 1-7 表示周1-周7
         * time : 00:00:00-23:59:59
         */

        @SerializedName("number")
        private int number;
        @SerializedName("time")
        private String timeX;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTimeX() {
            return timeX;
        }

        public void setTimeX(String timeX) {
            this.timeX = timeX;
        }
    }
}
