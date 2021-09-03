package com.coocaa.remoteplatform.baseability.abilities.time;

import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbilityFactory;
import com.coocaa.remoteplatform.baseability.abilities.IAbility;
import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeAnalysis;
import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeBean;
import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeTaskBean;
import com.coocaa.remoteplatform.baseability.abilities.time.constant.TimeConstant;
import com.coocaa.remoteplatform.baseability.abilities.time.filter.ITimeTaskFilter;
import com.coocaa.remoteplatform.baseability.abilities.time.filter.checkCmdTypeFilter;
import com.coocaa.remoteplatform.baseability.abilities.time.filter.checkPlanTypeFilter;
import com.coocaa.remoteplatform.baseability.abilities.time.task.Call;
import com.coocaa.remoteplatform.baseability.abilities.time.task.Callback;
import com.coocaa.remoteplatform.baseability.abilities.time.task.TimeClient;
import com.coocaa.remoteplatform.baseability.abilities.time.task.TimeTaskQueue;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.remoteplatform.commom.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:zhuyidian
 * Date:2021/8/26 14:55
 * Description:TimeManager
 */
public class TimeManager {
    private TimeTaskQueue timeTaskQueue;
    private TimeClient client;
    private final List<ITimeTaskFilter> mTimeTaskFilters = new ArrayList<>();

    private TimeManager() {
        checkTimeParams();
    }

    public static class InstanceClass {
        public static TimeManager instance = new TimeManager();
    }

    public static TimeManager getInstance() {
        return InstanceClass.instance;
    }

    /**
     * 预处理一条定时命令
     *
     * @param command
     * @return false:即使实时命令  true:定时/取消命令
     */
    public boolean onMessage(RemoteCommand command, IAbility ability) {
        LogUtil.i("time", "on message command=" + command + ", ability=" + ability);
        if (command == null || ability == null) return false;

        if (filterTimeTask(command)) return false;

        return onMessage(command.cmdType, command.content, command, ability);
    }

    /**
     * 预处理一条命令
     *
     * @param cmdType: 命令类型
     * @param content
     * @return false:即使实时命令  true:定时/取消命令
     */
    public boolean onMessage(int cmdType, String content, RemoteCommand command, IAbility ability) {
        TimeBean bean = TimeAnalysis.parseContent(content);
        if (bean == null) return false;

        //定时命令
        checkTimeParams();
        if (bean.getPlanType() == TimeConstant.CMD_CANCEL) {
            timeTaskQueue.removeTimeTask(cmdType);
        } else {
            timeTaskQueue.addTimeTask(cmdType, bean, command, ability);
        }

        enqueue();

        return true;
    }

    private boolean enqueue() {
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
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "callback <--- e=" + e);
        }
        return false;
    }

    private void checkTimeParams() {
        if (timeTaskQueue == null) {
            timeTaskQueue = new TimeTaskQueue();
        }

        if (client == null) {
            client = new TimeClient.Builder()
                    .build();
        }

        if (mTimeTaskFilters != null && mTimeTaskFilters.isEmpty()) {
            mTimeTaskFilters.add(new checkPlanTypeFilter());
            mTimeTaskFilters.add(new checkCmdTypeFilter());
        }
    }

    /**
     * @param command
     * @return true:过滤  false:不过滤
     */
    private boolean filterTimeTask(RemoteCommand command) {
        if (mTimeTaskFilters == null) return false;

        for (ITimeTaskFilter filter : mTimeTaskFilters) {
            if (filter.filter(command)) {
                LogUtil.i("time", "message handle by filter: " + filter.filterName());
                return true;
            }
        }

        return false;
    }
}
