package com.coocaa.remoteplatform.core.time.task;


public abstract class TimeRunnable implements Runnable {
    @Override
    public void run() {
        execute();
    }

    protected abstract void execute();
}
