package com.dunn.tools.time.task;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeAnalysis;
import com.dunn.tools.time.TimeUtil;
import com.dunn.tools.time.bean.TimeBean;
import com.dunn.tools.time.bean.TimeTaskBean;
import com.dunn.tools.time.constant.TimeConstant;
import com.dunn.tools.time.temp.RemoteCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:zhuyidian
 * Date:2021/8/27 14:25
 * Description:TimeTaskQueue
 */
public class TimeTaskQueue {
    private ConcurrentHashMap<Integer,TimeTaskBean> timeTaskMap = new ConcurrentHashMap<>();

    public void TimeMessageQueue(){}

    public boolean addTimeTask(int cmdType, TimeBean bean, RemoteCommand command){
        if(bean==null) return false;
        if(command==null) return false;
        if(timeTaskMap==null) timeTaskMap = new ConcurrentHashMap<>();
        if(timeTaskMap.containsKey(cmdType)){
            LogUtil.i("time", "add time task is have !!!!!! cmdType="+cmdType);
            return false;
        }

        TimeTaskBean timeTaskBean = new TimeTaskBean(cmdType,bean,command);
        long timeLong = findNearestTime(timeTaskBean,false);
        timeTaskMap.put(cmdType,timeTaskBean);
        LogUtil.i("time", "add time task success cmdType="+cmdType+", planType="+bean.getPlanType()+
                ", exe time="+TimeUtil.long2string(timeLong));
        return true;
    }

    public boolean removeTimeTask(int cmdType){
        if(timeTaskMap==null) return false;
        if(timeTaskMap.isEmpty()) return false;
        if(!timeTaskMap.containsKey(cmdType)) return false;

        timeTaskMap.remove(cmdType);
        LogUtil.i("time", "remove time task is success cmdType="+cmdType);
        return true;
    }

    public boolean clearTimeTask(){
        if(timeTaskMap==null) return false;
        if(timeTaskMap.isEmpty()) return false;

        timeTaskMap.clear();
        return true;
    }

    public boolean isTimeTask(){
        if(timeTaskMap==null) return false;
        if(timeTaskMap.isEmpty()) return false;

        return true;
    }

    public TimeTaskBean getNearestTimeTask(){
        if(timeTaskMap==null) return null;
        if(timeTaskMap.isEmpty()) return null;

        LogUtil.i("time", "#################get nearest time task start##################queue size="+timeTaskMap.size());
        Iterator<Map.Entry<Integer,TimeTaskBean>> it = timeTaskMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,TimeTaskBean> entry = it.next();
            TimeTaskBean timeTaskBean = (TimeTaskBean)entry.getValue();
            if(timeTaskBean==null) continue;
            if(timeTaskBean.getBean()==null) continue;
            findNearestTime(timeTaskBean,true);
        }

        if(timeTaskMap.isEmpty()) return null;
        List<TimeTaskBean> list = new ArrayList<>();
        Iterator<Map.Entry<Integer,TimeTaskBean>> it1 = timeTaskMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<Integer, TimeTaskBean> entry = it1.next();
            TimeTaskBean timeTaskBean = (TimeTaskBean) entry.getValue();
            if (timeTaskBean == null) continue;
            if (timeTaskBean.getBean() == null) continue;
            list.add(timeTaskBean);
        }

        if(list==null || list.isEmpty()) return null;
        Iterator<TimeTaskBean> iter1 = list.iterator();
        while (iter1.hasNext()) {
            TimeTaskBean timeTaskBean = (TimeTaskBean) iter1.next();
            LogUtil.i("time", "sort before cmdType="+timeTaskBean.getCmdType()+", planType="+timeTaskBean.getBean().getPlanType()+", timeLong="+timeTaskBean.getTime()+", timeStr="+ TimeUtil.long2string(timeTaskBean.getTime()));
        }
        Collections.sort(list, new Comparator<TimeTaskBean>() {
            @Override
            public int compare(TimeTaskBean t1, TimeTaskBean t2) {
                if(t1==null || t2==null) return 0;
                if(t1.getBean()==null || t2.getBean()==null) return 0;

                long t2Time = (long)t2.getTime();
                long t1Time = (long)t1.getTime();
                return t2Time > t1Time ? -1 : 1;
            }
        });
        Iterator<TimeTaskBean> iter2 = list.iterator();
        while (iter2.hasNext()) {
            TimeTaskBean timeTaskBean = (TimeTaskBean) iter2.next();
            LogUtil.i("time", "sort after cmdType="+timeTaskBean.getCmdType()+", planType="+timeTaskBean.getBean().getPlanType()+", timeLong="+timeTaskBean.getTime()+", timeStr="+ TimeUtil.long2string(timeTaskBean.getTime()));
        }
        LogUtil.i("time", "#################get nearest time task end##################queue size="+timeTaskMap.size()+", timeTaskBean="+list.get(0));

        return list.get(0);
    }

    private long findNearestTime(TimeTaskBean timeTaskBean,boolean isRemove){
        if(timeTaskBean==null) return 0l;
        if(timeTaskBean.getBean()==null) return 0l;
        long timeLong = 0l;

        switch (timeTaskBean.getBean().getPlanType()){
            case TimeConstant.CMD_DELAY:
                timeLong = TimeAnalysis.getDelayTime(timeTaskBean.getBean().getDelay());
                if(timeLong<System.currentTimeMillis() && isRemove){
                    removeTimeTask(timeTaskBean.getCmdType());
                }else {
                    timeTaskBean.setTime(timeLong);
                }
                break;
            case TimeConstant.CMD_DAY:
                timeLong = TimeAnalysis.getDayTime(timeTaskBean.getBean().getTime(),System.currentTimeMillis());
                timeTaskBean.setTime(timeLong);
                break;
            case TimeConstant.CMD_WEEK:
                timeLong = TimeAnalysis.getWeekTime(timeTaskBean.getBean().getWeek(),System.currentTimeMillis());
                timeTaskBean.setTime(timeLong);
                break;
            case TimeConstant.CMD_MONTH:
                timeLong = TimeAnalysis.getMonthTime(timeTaskBean.getBean().getMonth(),System.currentTimeMillis());
                timeTaskBean.setTime(timeLong);
                break;
            default:
                break;
        }

        return timeLong;
    }
}
