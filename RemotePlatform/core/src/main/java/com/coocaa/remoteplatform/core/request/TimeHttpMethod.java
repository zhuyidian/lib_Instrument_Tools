package com.coocaa.remoteplatform.core.request;

import android.content.Context;

import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.service.Main;
import com.coocaa.remoteplatform.core.time.bean.request.TimeRequestAnalysis;
import com.coocaa.remoteplatform.core.time.bean.request.TimeRequestDataInfo;
import com.coocaa.remoteplatform.core.time.bean.request.TimeRequestInfo;
import com.remoteplatform.commom.LogUtil;
import com.remoteplatform.commom.thread.HomeIOThread;

import java.util.List;
import java.util.Map;

import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_PUSH;
import static com.remoteplatform.commom.Constant.getRequestAddress;

public class TimeHttpMethod extends HttpMethod<TimeHttpService> {
    private static final String TIME_RELEASE_URL = "https://fusionmedia.skysrt.com/ccdevice/control/";
    private static final String TIME_RELEASE_URL_TEST = "https://beta-fusionmedia.skysrt.com/ccdevice/control/";
    public static final String APP_CODE = "639bae9ac6b3e1a84cebb7b403297b79";
    public static final String SALT = "99754106633f94d350db34d548d6091a";

    public TimeHttpMethod(Context context) {
        super(context);
    }

    /**
     * 单独请求自己放在子线程
     *
     * @return
     */
    public String getToken() {
        HttpResult<String> mTokenRet = null;
        try {
            mTokenRet = getService().getToken(APP_CODE, SALT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map(mTokenRet);
    }

    @Deprecated
    public void getTimeData(final Context context, final String deviceId) {
        LogUtil.i("time-api", "get time data deviceId=" + deviceId);
        HomeIOThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResult<List<TimeRequestInfo>> mTimeData = null;
                    try {
                        mTimeData = getService().getTimeData(deviceId);
                    } catch (Exception e) {
                        LogUtil.e("time-api", "get time data e=" + e);
                        e.printStackTrace();
                    }
                    if (mTimeData == null) return;

                    List<TimeRequestInfo> timeInfo = map(mTimeData);
                    LogUtil.i("time-api", "code=" + mTimeData.code + ", msg=" + mTimeData.msg);

                    if (timeInfo == null) return;
                    LogUtil.i("time-api", "timeInfo.size=" + timeInfo.size());
                    for (TimeRequestInfo info : timeInfo) {
                        LogUtil.i("time-api", "info=" + info + ", isOpen=" + info.isOpen);
                        if (info.isOpen == 1) {
                            int cmdType = TimeRequestAnalysis.convertSwtich(info.command);
                            String json = TimeRequestAnalysis.parseContent(info);
                            LogUtil.i("time-api", "cmdType=" + cmdType + ", json=" + json);
                            sendCommand(context, cmdType, json);
                        }
                    }

                    //TEST
//                String json = "{\"volume\":50,\"planType\":1,\"delay\":\"2021-08-31 15:32:39\",\"time\":[\"11:32:39\"],\"switch\":3}";
//                sendCommand(context, 3, json);
//                String json1 = "{\"planType\":2,\"switch\":3,\"time\":[\"15:55:30\",\"16:10:30\"],\"volume\":50}";  //音量-每天
//                sendCommand(context, 3, json1);
//
//                String json2 = "{\"volume\":50,\"planType\":1,\"delay\":\"2021-08-31 15:56:39\",\"time\":[\"11:32:39\"],\"switch\":0}";  //关机-一次性
//                sendCommand(context, 0, json2);
//
//                String json3 = "{\"planType\":6,\"month\":[{\"number\":\"31\",\"time\":[\"15:57:40\",\"18:30:40\"]}],\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":2}";  //重启-每月
//                sendCommand(context, 2, json3);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.i("time-api", "e=" + e);
                }
            }
        });
    }

    public void getTimeDataInfo(final Context context, final String deviceId) {
        LogUtil.i("time-api", "get time data info deviceId=" + deviceId);
        HomeIOThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResult<List<TimeRequestDataInfo>> mTimeData = null;
                    try {
                        mTimeData = getService().getTimeDataInfo(deviceId);
                    } catch (Exception e) {
                        LogUtil.e("time-api", "[new] get time data e=" + e);
                        e.printStackTrace();
                    }
                    if (mTimeData == null) return;

                    List<TimeRequestDataInfo> timeInfo = map(mTimeData);
                    LogUtil.i("time-api", "[new] code=" + mTimeData.code + ", msg=" + mTimeData.msg);

                    if (timeInfo == null) return;
                    LogUtil.i("time-api", "[new] timeInfo.size=" + timeInfo.size());
                    for (TimeRequestDataInfo info : timeInfo) {
                        LogUtil.i("time-api", "[new] info=" + info);
                        if (info.isOpen == 1) {
                            sendCommand(context, info);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.i("time-api", "[new] e=" + e);
                }
            }
        });
    }

    private void sendCommand(Context mContext, int cmdType, String json) {
        RemoteCommand command = null;
        try {
            command = new RemoteCommand();
            command.content = json;
            command.timestamp = System.currentTimeMillis();
            command.clientId = "com.coocaa.remoteplatform.baseability";
            command.cmdType = /*TimeRequestAnalysis.convertSwtich(info.command)*/cmdType;
            command.msgOrigin = COMMAND_SOURCE_PUSH;
            if (command != null) {
                //Main.getInstance(mContext).getPushChannel().onReceiveMessage(command);
                Main.getInstance(mContext).getMessageDispatcher().dispatchMessage(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time-api", "e=" + e);
        }
    }

    private void sendCommand(Context mContext, TimeRequestDataInfo info) {
        RemoteCommand command = null;
        try {
            command = new RemoteCommand();
            command.content = info.content;
            command.timestamp = info.timestamp;
            command.clientId = info.clientId;
            command.cmdType = info.cmdType;
            command.deviceId = info.deviceId;
            //这里不能确定是Push还是Socket
            command.msgOrigin = COMMAND_SOURCE_PUSH;
            if (command != null) {
                //Main.getInstance(mContext).getPushChannel().onReceiveMessage(command);
                Main.getInstance(mContext).getMessageDispatcher().dispatchMessage(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time-api", "e=" + e);
        }
    }

    @Override
    public String getBaseUrl() {
        String requestAddress = null;
        if (mContext != null) requestAddress = getRequestAddress(mContext);
        LogUtil.i("time-api", "url=" + requestAddress);
        if (requestAddress == null) requestAddress = TIME_RELEASE_URL_TEST;
        return requestAddress;
    }

    @Override
    public int getTimeOut() {
        return 10;
    }

    @Override
    public Class getServiceClazz() {
        return TimeHttpService.class;
    }

    @Override
    public Map<String, String> getHeaders() {
        LogUtil.i("time-api", "header=" + HomeHeader.getHeadersForTime());
        return HomeHeader.getHeadersForTime();
    }

}
