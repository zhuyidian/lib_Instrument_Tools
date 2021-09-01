package com.dunn.tools.time.task;


import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.bean.TimeAnalysis;
import com.dunn.tools.time.bean.TimeTaskBean;

import java.io.IOException;

public class RealCall implements Call {
    private final TimeTaskBean orignalBean;
    private final TimeClient client;

    public RealCall(TimeTaskBean bean, TimeClient client) {
        orignalBean = bean;
        this.client = client;
    }

    public static Call newCall(TimeTaskBean bean, TimeClient client) {
        return new RealCall(bean, client);
    }

    @Override
    public void enqueue(Callback callback) {
        // 异步的
        AsyncCall asyncCall = new AsyncCall(callback);
        // 交给线程
        client.dispatcher.enqueue(asyncCall);
    }

    @Override
    public void enqueue(Callback callback, long delay) {
        //清除队列定时任务，重新调整
        LogUtil.i("time", "call clear");
        client.dispatcher.clearDelayCall();
        // 交给线程
        if (orignalBean != null) {
            LogUtil.i("time", "call enqueue add task delay=" + delay + ", timeStr=" + TimeAnalysis.long2string(orignalBean.getTime()) + ", orignalBean=" + orignalBean);
        } else {
            LogUtil.e("time", "call enqueue add task delay=" + delay + ", orignalBean is null !!!!!!");
        }

        if (orignalBean != null && delay >= -2000) {
            // 异步的
            AsyncCall asyncCall = new AsyncCall(callback);
            client.dispatcher.enqueue(asyncCall, delay);
        }
    }

    @Override
    public String execute() {
        return null;
    }

    final class AsyncCall extends TimeRunnable {
        Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected void execute() {
            try {
                if (orignalBean != null) {
                    LogUtil.i("time", "call execute ---> timeStr=" + TimeAnalysis.long2string(orignalBean.getTime()) + ", orignalBean=" + orignalBean);
                } else {
                    LogUtil.e("time", "call execute ---> orignalBean is null !!!!!!");
                }
                if (orignalBean.getAbility() != null) {
                    orignalBean.getAbility().realMessage(orignalBean.getCommand());
                }
                callback.onSuccess(RealCall.this, null);
            } catch (IOException e) {
                callback.onFailure(RealCall.this, e);
            }
        }
    }
}
