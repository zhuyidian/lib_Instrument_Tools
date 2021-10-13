package com.coocaa.remoteplatform.core.time.task;


public interface Call {
    void enqueue(Callback callback);

    void enqueue(Callback callback, long delay);

    String execute();
}
