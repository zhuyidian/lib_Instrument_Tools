package com.dunn.tools.time.task;

import com.dunn.tools.time.bean.TimeTaskBean;
import com.dunn.tools.time.temp.Request;

public class TimeClient {
    final Dispatcher dispatcher;
    private TimeClient(Builder builder) {
        dispatcher = builder.dispatcher;
    }

    public TimeClient() {
        this(new Builder());
    }

    public Call newCall(TimeTaskBean bean) {
        return RealCall.newCall(bean,this);
    }

    public static class Builder{
        Dispatcher dispatcher;
        // 链接超时
        // https 证书的一些参数
        // 拦截器
        // 等等
        public Builder(){
            dispatcher = new Dispatcher();
        }

        public TimeClient build(){
            return new TimeClient(this);
        }
    }
}
