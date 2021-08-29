package com.dunn.tools.time.task;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeAnalysis;
import com.dunn.tools.time.constant.TimeConstant;
import com.dunn.tools.time.bean.TimeBean;
import com.dunn.tools.time.temp.RemoteCommand;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author:zhuyidian
 * Date:2021/8/27 14:25
 * Description:TimeMessageQueue
 */
public class TimeMessageQueue {
    private CopyOnWriteArrayList<ConcurrentHashMap<String,Object>> timeTaskList = new CopyOnWriteArrayList<>();

    public void TimeMessageQueue(){}

    public boolean addTimeTask(int cmdType, TimeBean bean, RemoteCommand command){
        if(bean==null) return false;
        if(command==null) return false;
        if(timeTaskList==null) timeTaskList = new CopyOnWriteArrayList<>();

        if(timeTaskList.isEmpty()) {
            ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<>();
            map.put(TimeConstant.MAP_KEY_CMDTYPE,cmdType);
            map.put(TimeConstant.MAP_KEY_BEAN,bean);
            map.put(TimeConstant.MAP_KEY_COMMAND,command);
            switch (bean.getPlanType()){
                case TimeConstant.CMD_DELAY:
                    map.put(TimeConstant.MAP_KEY_TIME, TimeAnalysis.getDelayTime(bean.getDelay()));
                    break;
                case TimeConstant.CMD_DAY:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_WEEK:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_MONTH:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                    break;
                default:
                    break;
            }
            timeTaskList.add(map);
            return true;
        }

        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map!=null && map.containsKey(TimeConstant.MAP_KEY_CMDTYPE)){
                int cmd = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
                if(cmd == cmdType) {
                    LogUtil.i("time", "add Time MessageQueue is have !!!!!! cmdType="+cmdType);
                    return false;
                }
            }
        }

        ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<>();
        map.put(TimeConstant.MAP_KEY_CMDTYPE,cmdType);
        map.put(TimeConstant.MAP_KEY_BEAN,bean);
        map.put(TimeConstant.MAP_KEY_COMMAND,command);
        switch (bean.getPlanType()){
            case TimeConstant.CMD_DELAY:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDelayTime(bean.getDelay()));
                break;
            case TimeConstant.CMD_DAY:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                break;
            case TimeConstant.CMD_WEEK:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                break;
            case TimeConstant.CMD_MONTH:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                break;
            default:
                break;
        }
        timeTaskList.add(map);
        LogUtil.i("time", "add Time MessageQueue success cmdType="+cmdType);
        return true;
    }

    public boolean removeTimeTask(int cmdType){
        if(timeTaskList==null) return false;
        if(timeTaskList.isEmpty()) return false;

        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map!=null && map.containsKey(TimeConstant.MAP_KEY_CMDTYPE)){
                int cmd = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
                if(cmd == cmdType) {
                    //iter.remove();
                    timeTaskList.remove(map);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean clearTimeTask(){
        if(timeTaskList==null) return false;
        if(timeTaskList.isEmpty()) return false;

        timeTaskList.clear();
        return true;
    }

    public void getNearestTimeTask(){
        if(timeTaskList==null) return;
        if(timeTaskList.isEmpty()) return;

        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map==null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_BEAN)) continue;
            TimeBean bean = (TimeBean) map.get(TimeConstant.MAP_KEY_BEAN);
            if(bean==null) continue;
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);

            switch (bean.getPlanType()){
                case TimeConstant.CMD_DELAY:
                    long delayTime = TimeAnalysis.getDelayTime(bean.getDelay());
                    if(delayTime<System.currentTimeMillis()){
                        //iter.remove();
                        timeTaskList.remove(map);
                    }else{
                        map.put(TimeConstant.MAP_KEY_TIME,delayTime);
                    }
                    break;
                case TimeConstant.CMD_DAY:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_WEEK:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_MONTH:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                    break;
                default:
                    break;
            }
        }

        Iterator<ConcurrentHashMap<String,Object>> iter1 = timeTaskList.iterator();
        while (iter1.hasNext()) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) iter1.next();
            if (map == null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_TIME)) continue;
            long timeLong = (Long) map.get(TimeConstant.MAP_KEY_TIME);
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
        }
        Collections.sort(timeTaskList, new Comparator<ConcurrentHashMap<String,Object>>() {
            @Override
            public int compare(ConcurrentHashMap<String,Object> t1, ConcurrentHashMap<String,Object> t2) {
                if(t1==null || t2==null) return 0;
                if(!t1.containsKey(TimeConstant.MAP_KEY_TIME) || !t2.containsKey(TimeConstant.MAP_KEY_TIME)) return 0;

                long t2Time = (long)t2.get(TimeConstant.MAP_KEY_TIME);
                long t1Time = (long)t1.get(TimeConstant.MAP_KEY_TIME);
                return t2Time > t1Time ? -1 : 1;
            }
        });
        Iterator<ConcurrentHashMap<String,Object>> iter2 = timeTaskList.iterator();
        while (iter2.hasNext()) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) iter2.next();
            if (map == null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_TIME)) continue;
            long timeLong = (Long) map.get(TimeConstant.MAP_KEY_TIME);
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
        }
    }
}
