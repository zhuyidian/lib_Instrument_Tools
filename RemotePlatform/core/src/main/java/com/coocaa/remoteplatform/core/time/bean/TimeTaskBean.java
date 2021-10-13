package com.coocaa.remoteplatform.core.time.bean;

import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.dispatcher.IMessageDispatcher;
import com.coocaa.remoteplatform.core.dispatcher.MessageDispatcher;

import java.io.Serializable;

/**
 * @ClassName: TimeTaskBean
 * @Author: zhuyidian
 * @Description:
 */
public class TimeTaskBean implements Serializable {
    private int cmdType;
    private TimeBean bean;
    private RemoteCommand command;
    private long time;
    private IMessageDispatcher dispatcher;

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command, IMessageDispatcher dispatcher) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
        this.dispatcher = dispatcher;
    }

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command, IMessageDispatcher dispatcher, long time) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
        this.dispatcher = dispatcher;
        this.time = time;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public TimeBean getBean() {
        return bean;
    }

    public void setBean(TimeBean bean) {
        this.bean = bean;
    }

    public RemoteCommand getCommand() {
        return command;
    }

    public void setCommand(RemoteCommand command) {
        this.command = command;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public IMessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(IMessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public String toString() {
        return "TimeTaskBean{" +
                "cmdType=" + cmdType +
                ", bean=" + bean +
                ", command=" + command +
                ", time=" + time +
                ", dispatcher=" + dispatcher +
                '}';
    }
}
