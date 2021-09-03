package com.coocaa.remoteplatform.baseability.abilities.time.task;


public interface Call {
    void enqueue(Callback callback);

    void enqueue(Callback callback, long delay);

    String execute();
}
