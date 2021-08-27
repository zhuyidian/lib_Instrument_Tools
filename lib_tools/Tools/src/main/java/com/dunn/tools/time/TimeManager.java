package com.dunn.tools.time;

import android.os.Handler;
import android.os.HandlerThread;

import com.dunn.tools.time.bean.TimeBean;
import com.dunn.tools.time.constant.TimeConstant;
import com.dunn.tools.time.queue.TimeMessageQueue;
import com.dunn.tools.time.temp.RemoteCommand;

/**
 * Author:zhuyidian
 * Date:2021/8/26 14:55
 * Description:TimeManager
 */
public class TimeManager {
    private static final String TAG = "TimeManager";
    private enum CMD_TYPE {
        shutdown(0),
        reboot(2),
        volume(3);

        private final int cmdType;

        CMD_TYPE(int value) {
            this.cmdType = value;
        }

        public int getValue() {
            return cmdType;
        }
    }
    private TimeMessageQueue timeQueue;
    private final HandlerThread handlerThread;
    private final Handler delayHandler;

    private TimeManager(){
        timeQueue = new TimeMessageQueue();
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        delayHandler = new Handler(handlerThread.getLooper());
    }

    public static class InstanceClass {
        public static TimeManager instance = new TimeManager();
    }

    public static TimeManager getInstance() {
        return InstanceClass.instance;
    }

    /**
     * 预处理一条命令
     * @param command
     * @return true:即使实时命令  false:定时/取消命令
     */
    public boolean onMessage(RemoteCommand command){
        return onMessage(command.cmdType,command.content,command);
    }

    /**
     * 预处理一条命令
     * @param cmdType: 命令类型
     * @param content
     * @return true:即使实时命令  false:定时/取消命令
     */
    public boolean onMessage(int cmdType, String content, RemoteCommand command){
        TimeBean bean = TimeAnalysis.parseContent(content);

        if(bean==null) return true;

        int planType = bean.getPlanType();
        if(planType == TimeConstant.CMD_REAL){
            return true;
        }

        //定时命令
        checkTimeQueue();
        if(timeQueue.addTimeTask(cmdType,bean,command)){
            enqueue();
        }

        return false;
    }

    private boolean enqueue(){
        timeQueue.getNearestTimeTask();

        return false;
    }

    private void checkTimeQueue(){
        if(timeQueue==null){
            timeQueue = new TimeMessageQueue();
        }
    }
}
