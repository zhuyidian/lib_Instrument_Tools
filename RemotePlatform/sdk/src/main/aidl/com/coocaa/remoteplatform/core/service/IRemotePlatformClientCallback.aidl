// IRemotePlatformClientCallback.aidl
package com.coocaa.remoteplatform.core.service;

// Declare any non-default types here with import statements

interface IRemotePlatformClientCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Map invokeCallBackMethod(String method, in Map params);
}