// IRemotePlatformService.aidl
package com.coocaa.remoteplatform.core.service;

// Declare any non-default types here with import statements
import com.coocaa.remoteplatform.core.service.AttachInfo;
import com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback;

interface IRemotePlatformService {
    AttachInfo getAttachInfo();
    Map invokeMethod(String method, in Map params);
    void setClientCallback(String packageName, IRemotePlatformClientCallback callback);
    void clearClientCallback(IRemotePlatformClientCallback callback);
}