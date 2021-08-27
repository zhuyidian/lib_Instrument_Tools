package com.dunn.tools.time.constant;

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

    public static final String MAP_KEY_CMDTYPE = "cmdtype";
    public static final String MAP_KEY_BEAN = "bean";
    public static final String MAP_KEY_COMMAND = "command";
    public static final String MAP_KEY_TIME = "time";

}
