package com.coocaa.remoteplatform.baseability.abilities.time.bean;


import com.coocaa.remoteplatform.baseability.abilities.IAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

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
    private IAbility ability;

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command, IAbility ability) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
        this.ability = ability;
    }

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command, IAbility ability, long time) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
        this.ability = ability;
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

    public IAbility getAbility() {
        return ability;
    }

    public void setAbility(IAbility ability) {
        this.ability = ability;
    }

    @Override
    public String toString() {
        return "TimeTaskBean{" +
                "cmdType=" + cmdType +
                ", bean=" + bean +
                ", command=" + command +
                ", time=" + time +
                ", ability=" + ability +
                '}';
    }
}
