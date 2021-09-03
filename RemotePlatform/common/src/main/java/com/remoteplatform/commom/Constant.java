package com.remoteplatform.commom;

import android.content.Context;

/**
 * @ClassName: Constant
 * @Author: XuZeXiao
 * @CreateDate: 4/7/21 4:04 PM
 * @Description:
 */
public class Constant {
    public static final int CMD_TYPE_CLOSE_SOCKET_CHANNEL = 9;
    public static final int CMD_TYPE_OPEN_SOCKET_CHANNEL = 10;
    public static final int CMD_TYPE_HEART_BEAT = 21;

    public static final String COMMAND_SOURCE_PUSH = "push";
    public static final String COMMAND_SOURCE_SOCKET = "socket";

    public static final String SOCKET_ADDRESS = "SOCKET_ADDRESS";
    public static final String HTTP_ADDRESS = "HTTP_ADDRESS";
    public static final String FILE_ADDRESS = "FILE_ADDRESS";
    public static final String PUSH_ADDRESS = "PUSH_ADDRESS";
    public static final String PUSH_PORT = "PUSH_PORT";
    public static final String REQUEST_ADDRESS = "REQUEST_ADDRESS";
    public static final String SOCKET_SALT = "SOCKET_SALT";

    public static String getSocketAddress(Context context) {
        return Utils.valueFromManifest(context, SOCKET_ADDRESS);
    }

    public static String getFileAddress(Context context) {
        return Utils.valueFromManifest(context, FILE_ADDRESS);
    }

    public static String getPushAddress(Context context) {
        return Utils.valueFromManifest(context, PUSH_ADDRESS);
    }

    public static int getPushPort(Context context) {
        return Utils.valueFromManifest(context, PUSH_PORT);
    }

    public static String getRequestAddress(Context context) {
        return Utils.valueFromManifest(context, REQUEST_ADDRESS);
    }

    public static String getSocketSalt(Context context) {
        return Utils.valueFromManifest(context, SOCKET_SALT);
    }

    public static String getHttpAddress(Context context) {
        return Utils.valueFromManifest(context, HTTP_ADDRESS);
    }
}
