package com.dunn.tools.time.task;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Dispatcher {
    private @Nullable ExecutorService executorService;
    private @Nullable HandlerThread handlerThread;
    private @Nullable Handler delayHandler;

    public synchronized ExecutorService executor() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r,"TimeTask-io");
                    thread.setDaemon(false);
                    return thread;
                }
            });
        }

        return executorService;
    }

    public synchronized Handler executorDelay() {
        if(delayHandler == null){
            handlerThread = new HandlerThread("TimeTask-handler");
            handlerThread.start();
            delayHandler = new Handler(handlerThread.getLooper());
        }

        return delayHandler;
    }

    public void enqueue(RealCall.AsyncCall call) {
        executor().execute(call);
    }

    public void enqueue(RealCall.AsyncCall call, long delay) {
        executorDelay().postDelayed(call, delay);
    }
}
