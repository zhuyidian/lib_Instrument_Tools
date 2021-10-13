package com.coocaa.remoteplatform.core.time.bean.request;

public class TimeRequestInfo {
    public int command;
    public int volume;
    public int cycleMode;
    public String executeTime;
    public String runDate;
    public int isOpen;
    public String deviceId;

    @Override
    public String toString() {
        return "TimeInfo{" +
                "command=" + command +
                ", volume=" + volume +
                ", cycleMode=" + cycleMode +
                ", executeTime='" + executeTime + '\'' +
                ", runDate='" + runDate + '\'' +
                ", isOpen=" + isOpen +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
