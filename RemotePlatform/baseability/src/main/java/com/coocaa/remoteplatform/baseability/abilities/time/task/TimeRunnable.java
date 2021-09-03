package com.coocaa.remoteplatform.baseability.abilities.time.task;


public abstract class TimeRunnable implements Runnable {
    @Override
    public void run() {
        execute();
    }

    protected abstract void execute();
}
