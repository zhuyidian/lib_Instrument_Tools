package com.coocaa.remoteplatform.core.common;

import java.io.Serializable;

/**
 * @ClassName: Constant
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 5:00 PM
 * @Description:
 */
public class Constant implements Serializable {
    public static final String PLATFORM_SERVICE_ACTION = "coocaa.intent.action.remote.platform.service";
    public static final String REPLY_SERVICE_ACTION = "coocaa.intent.action.remote.platform.reply";
    public static final String CLIENT_SERVICE_ACTION = "coocaa.intent.action.remote.platform.client";
    public static final String CLIENT_RECEIVER_ACTION = "coocaa.intent.action.remote.platform.client.receiver";
    public static final String INTENT_COMMAND_KEY = "command";
    public static final String HOST_PACKAGE_NAME = "com.coocaa.remoteplatform";

    public static final String COMMAND_STATUS_RECEIVE = "100";
    public static final String COMMAND_STATUS_PROCESSING = "200";
    public static final String COMMAND_STATUS_FINISH = "700";
    public static final String COMMAND_STATUS_EXCEPTION = "900";

    public static final String ATTACH_INFO_CHANGE_ACTION = "coocaa.intent.action.attach.info.change";
    public static final String ATTACH_INFO_CHANGE_WHICH = "which";
    public static final String ATTACH_INFO_CHANGE_NEW_VALUE = "newValue";

    public static final String REMOTE_METHOD_TYPE_KEY = "TYPE";
    public static final String REMOTE_METHOD_RESULT_KEY = "RESULT";
    public static final String REMOTE_METHOD = "METHOD";
    public static final String REMOTE_METHOD_RESULT_NO_SUCH_METHOD = "NO_SUCH_METHOD";
    public static final String REMOTE_METHOD_IS_CONNECT = "REMOTE_METHOD_IS_CONNECT";
    public static final String REMOTE_METHOD_SUBSCRIBE_TOPIC = "REMOTE_METHOD_SUBSCRIBE_TOPIC";
    public static final String REMOTE_METHOD_UN_SUBSCRIBE_TOPIC = "REMOTE_METHOD_UN_SUBSCRIBE_TOPIC";
    public static final String REMOTE_METHOD_PACKAGE_METHOD = "REMOTE_METHOD_PACKAGE_METHOD";
    public static final String REMOTE_METHOD_PARAMS_TOPIC = "REMOTE_METHOD_PARAMS_TOPIC";

    public static final String SP_FILE_NAME = "remote_platform_config";
}
