package com.dunn.tools.time;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.bean.TimeBean;
import com.dunn.tools.time.bean.TimeTaskBean;
import com.dunn.tools.time.constant.TimeConstant;
import com.dunn.tools.time.task.Call;
import com.dunn.tools.time.task.Callback;
import com.dunn.tools.time.task.TimeClient;
import com.dunn.tools.time.task.TimeMessageQueue;
import com.dunn.tools.time.task.TimeTaskQueue;
import com.dunn.tools.time.temp.RemoteCommand;

import java.io.IOException;

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
    private TimeTaskQueue timeTaskQueue;
    private TimeClient client;

    private TimeManager(){
        checkTimeParams();
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

        //判断即时指令
        if(bean.getPlanType() == TimeConstant.CMD_REAL){
            return true;
        }

        //定时命令
        checkTimeParams();
        if(bean.getPlanType() == TimeConstant.CMD_CANCEL){
            timeTaskQueue.removeTimeTask(cmdType);
        }else{
            timeTaskQueue.addTimeTask(cmdType,bean,command);
        }

        enqueue();

        return false;
    }

    private boolean enqueue(){
        try {
            final TimeTaskBean timeTaskBean = timeTaskQueue.getNearestTimeTask();
            long delayMs = 0l;
            if (timeTaskBean != null) {
                delayMs = timeTaskBean.getTime() - System.currentTimeMillis();
                //LogUtil.i("time", "delayMs ---> delayMs=" + delayMs + ", timeStr=" + TimeUtil.long2string(timeTaskBean.getTime()) + ", cmdType=" + timeTaskBean.getCmdType() + ", planType=" + timeTaskBean.getBean().getPlanType());
            }

            Call call = client.newCall(timeTaskBean);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i("time", "callback <--- onFailure");
                    if (timeTaskQueue != null && timeTaskQueue.isTimeTask()) {
                        enqueue();
                    }
                }

                @Override
                public void onSuccess(Call call, String msg) throws IOException {
                    LogUtil.i("time", "callback <--- onSuccess");
                    if (timeTaskQueue != null && timeTaskQueue.isTimeTask()) {
                        enqueue();
                    }
                }
            }, delayMs);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.i("time", "callback <--- e="+e);
        }
        return false;
    }

    private void checkTimeParams(){
        if(timeTaskQueue==null){
            timeTaskQueue = new TimeTaskQueue();
        }

        if(client==null){
            client = new TimeClient.Builder()
                    .build();
        }
    }
}
