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

    private TimeManager(){
        timeTaskQueue = new TimeTaskQueue();
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
        if(timeTaskQueue.addTimeTask(cmdType,bean,command)){
            enqueue();
        }

        return false;
    }

    private boolean enqueue(){
        TimeTaskBean timeTaskBean = timeTaskQueue.getNearestTimeTask();

        if(timeTaskBean!=null){
            for(int i=0;i<5;i++){
                long delayMs = timeTaskBean.getTime() - System.currentTimeMillis();
                LogUtil.i("time", "delayMs ---> delayMs="+delayMs+", timeStr="+TimeUtil.long2string(timeTaskBean.getTime()));
                TimeClient client = new TimeClient();
                Call call = client.newCall(timeTaskBean);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.i("time", "callback <--- onFailure");
                    }

                    @Override
                    public void onSuccess(Call call, String msg) throws IOException {
                        LogUtil.i("time", "callback <--- onSuccess");
                    }
                }, 2000);
            }
        }
        return false;
    }

    private void checkTimeQueue(){
        if(timeTaskQueue==null){
            timeTaskQueue = new TimeTaskQueue();
        }
    }
}
