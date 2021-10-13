package com.coocaa.remoteplatform.core.connection.push;

/**
 * @ClassName: PushConstant
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:33 PM
 * @Description:
 */
public class PushConstant {
    public static final String THRID_APP_MSG_TYPE = "thrid_app_msg_type"; //回馈给push的消息类型
    public static final String THRID_APP_RECEIVE = "thrid_app_receive_msg"; //标识第三方应用收到消息
    public static final String THRID_APP_HANDLE = "thrid_app_handle_msg";//标识第三方应用处理完消息
    public static final String THRID_APP_HANDLE_RESULT = "handle_result"; //消息处理是否完成
    public final static String MSG_RESULT_KEY = "MSG_RESULT_KEY"; //消息内容
    public final static String MSG_ID_KEY = "MSG_ID_KEY"; //msgID
    public final static String REGID_RESULT_KEY = "REGID_RESULT_KEY";//就是push ID,但是老的push就叫这个鸟值
    public final static String THIRD_PKGNAME = "third_pkg";
    public final static String CURRENT_TIME = "current_time";
    public final static String ACTION_PUSH = "com.tianci.push.action";
    public final static String PUSH_APP_PKG = "com.tianci.push";
    public static final String MAP_EKY = "map";
}
