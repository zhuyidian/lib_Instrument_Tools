package com.coocaa.remoteplatform.sdk;

import java.util.Map;

/**
 * @ClassName: IRemoteCallback
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 11:38 AM
 * @Description:
 */
public interface IRemoteCallback {
    void onConnect();

    void onDisconnect();

    Map<String, String> invokeClientMethod(String method, Map<String, String> params);
}
