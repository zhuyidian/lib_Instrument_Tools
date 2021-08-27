package com.coocaa.remoteplatform.sdk;

import android.content.Context;

import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.service.AttachInfo;

import java.util.Map;

/**
 * @ClassName: IRemotePlatform
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 11:36 AM
 * @Description:
 */
public interface IRemotePlatform {
    void init(Context context);

    void registerEventHandler(IRemoteCallback callback);

    void unRegisterEventHandler(IRemoteCallback callback);

    void sendMessage(Context context, RemoteCommand command);

    boolean isConnect();

    void subScribeTopic(String topic, IIPCSocketReceiver callback);

    void unSubScribeTopic(String topic);

    void sendTopicMessage(String method, String topic, String message);

    AttachInfo getAttachInfo();

    Map<String, String> packageMethod(String targetPackageName, Map<String, String> params);

    void destroy();
}
