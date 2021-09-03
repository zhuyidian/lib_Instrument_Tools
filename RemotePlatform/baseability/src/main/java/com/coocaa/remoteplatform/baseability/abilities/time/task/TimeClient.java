package com.coocaa.remoteplatform.baseability.abilities.time.task;


import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeTaskBean;

public class TimeClient {
    final Dispatcher dispatcher;

    private TimeClient(Builder builder) {
        dispatcher = builder.dispatcher;
    }

    public TimeClient() {
        this(new Builder());
    }

    public Call newCall(TimeTaskBean bean) {
        return RealCall.newCall(bean, this);
    }

    public static class Builder {
        Dispatcher dispatcher;

        public Builder() {
            dispatcher = new Dispatcher();
        }

        public TimeClient build() {
            return new TimeClient(this);
        }
    }
}
