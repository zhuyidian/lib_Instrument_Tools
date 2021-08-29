package com.dunn.tools.time.task;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.bean.TimeTaskBean;
import java.io.IOException;

public class RealCall implements Call{
    private final TimeTaskBean orignalBean;
    private final TimeClient client;
    public RealCall(TimeTaskBean bean, TimeClient client) {
        orignalBean = bean;
        this.client = client;
    }

    public static Call newCall(TimeTaskBean bean, TimeClient client) {
        return new RealCall(bean,client);
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
        // 异步的
        AsyncCall asyncCall = new AsyncCall(callback);
        // 交给线程
        client.dispatcher.enqueue(asyncCall,delay);
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
                LogUtil.i("time", "execute ---> orignalBean="+orignalBean);
                callback.onSuccess(RealCall.this,null);
            } catch (IOException e) {
                callback.onFailure(RealCall.this,e);
            }
        }
    }
}
