package com.coocaa.remoteplatform.core.request;

public interface HttpCallback<T> {
    public void callback(T t);
    public void error(Throwable e);
}
