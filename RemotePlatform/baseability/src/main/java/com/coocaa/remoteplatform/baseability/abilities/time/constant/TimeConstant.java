package com.coocaa.remoteplatform.baseability.abilities.time.constant;

import java.io.Serializable;

/**
 * @ClassName: Constant
 * @Author: zhuyidian
 * @Description:
 */
public class TimeConstant implements Serializable {
    public static final int CMD_CANCEL = -1;
    public static final int CMD_REAL = 0;
    public static final int CMD_DELAY = 1;
    public static final int CMD_DAY = 2;
    public static final int CMD_WEEK = 5;
    public static final int CMD_MONTH = 6;

    public static final int CMD_TYPE_SHUTDOWN = 0;
    public static final int CMD_TYPE_REBOOT = 2;
    public static final int CMD_TYPE_VOLUME = 3;

}
