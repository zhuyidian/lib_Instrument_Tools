package com.coocaa.remoteplatform.baseability.abilities.deviceinfo;

import java.io.Serializable;

/**
 * @ClassName: ReportInfo
 * @Author: XuZeXiao
 * @CreateDate: 4/8/21 11:04 AM
 * @Description:
 */
public class ReportInfo implements Serializable {
    public String mac;
    public String deviceId;
    public String activeId;
    public int volume;
    public String bootPlain;
    public boolean quickKey;
    public boolean supportOpen;
    public int minVolume;
    public int maxVolume;
    public boolean isBlackScreen;
}
