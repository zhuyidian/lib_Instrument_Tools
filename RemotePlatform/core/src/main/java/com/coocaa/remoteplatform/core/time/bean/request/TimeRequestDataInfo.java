package com.coocaa.remoteplatform.core.time.bean.request;

public class TimeRequestDataInfo {
    public String clientId;
    public int cmdType;
    public String deviceId;
    public int isOpen;
    public long timestamp;
    public String content;

    @Override
    public String toString() {
        return "TimeRequestDataInfo{" +
                "clientId='" + clientId + '\'' +
                ", cmdType=" + cmdType +
                ", deviceId='" + deviceId + '\'' +
                ", isOpen=" + isOpen +
                ", timestamp=" + timestamp +
                ", content='" + content + '\'' +
                '}';
    }
}
