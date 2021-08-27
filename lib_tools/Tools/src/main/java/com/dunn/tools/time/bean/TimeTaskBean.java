package com.dunn.tools.time.bean;

import com.dunn.tools.time.temp.RemoteCommand;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: TimeTaskBean
 * @Author: zhuyidian
 * @Description:
 */
public class TimeTaskBean implements Serializable {
    private int cmdType;
    private TimeBean bean;
    private RemoteCommand command;
    private List<Long> time;

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
    }

    public TimeTaskBean(int cmdType, TimeBean bean, RemoteCommand command, List<Long> time) {
        this.cmdType = cmdType;
        this.bean = bean;
        this.command = command;
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

    public List<Long> getTime() {
        return time;
    }

    public void setTime(List<Long> time) {
        this.time = time;
    }
}
