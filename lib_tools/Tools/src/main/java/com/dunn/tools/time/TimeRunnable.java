package com.dunn.tools.time;

/**
 * Created by hcDarren on 2017/11/18.
 */

public abstract class TimeRunnable implements Runnable{
    @Override
    public void run() {
        execute();
    }

    protected abstract void execute();
}
