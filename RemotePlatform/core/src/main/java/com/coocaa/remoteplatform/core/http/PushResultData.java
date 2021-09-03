package com.coocaa.remoteplatform.core.http;

import java.io.Serializable;

/**
 * @ClassName: PushResultData
 * @Author: XuZeXiao
 * @CreateDate: 2021/5/13 15:32
 * @Description:
 */
public class PushResultData implements Serializable {
    public String msgId;
    public String deviceId;
    public String mac;
    public int status;
    public String result;
    public String data;

    public PushResultData(String msgId, String deviceId, String mac, int status, String result, String data) {
        this.msgId = msgId;
        this.deviceId = deviceId;
        this.mac = mac;
        this.status = status;
        this.result = result;
        this.data = data;
    }
}
