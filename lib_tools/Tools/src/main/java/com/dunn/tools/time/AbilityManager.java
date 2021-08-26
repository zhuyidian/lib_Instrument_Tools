package com.dunn.tools.time;

import android.content.Context;

import com.dunn.tools.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Author:zhuyidian
 * Date:2021/8/26 14:55
 * Description:AbilityManager
 */
public class AbilityManager {
    private static final String TAG = "AbilityManager";
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
    private CMD_TYPE cmdType = CMD_TYPE.volume;

    public static class InstanceClass {
        public static AbilityManager instance = new AbilityManager();
    }

    public static AbilityManager getInstance() {
        return InstanceClass.instance;
    }

//    public void onNewMessage(Context context, int cmdType, String json) {
//        if(cmdType == CMD_TYPE.shutdown.getValue() ||
//                cmdType == CMD_TYPE.reboot.getValue() ||
//                cmdType == CMD_TYPE.volume.getValue()){
//            LogUtil.i(TAG,"cmdType="+cmdType);
//
//            Gson gson = new Gson();
//            final Gson gsonBuilder = new GsonBuilder().create();
//            java.lang.reflect.Type type = new TypeToken<TimeBean>() {}.getType();
//            try {
//                //TimePlan plan = gson.fromJson(json, TimePlan.class);
//                final TimeBean plan = gsonBuilder.fromJson(json, type);
//                LogUtil.i(TAG, "plan=" + plan);
//                LogUtil.i(TAG, "planType=" + plan.getPlanType());
//                LogUtil.i(TAG, "switch=" + plan.getSwitchX());
//                LogUtil.i(TAG, "volume=" + plan.getVolume());
//                LogUtil.i(TAG, "delay=" + plan.getDelay());
//                LogUtil.i(TAG, "time=" + plan.getTime());
//                LogUtil.i(TAG, "week=" + plan.getWeek());
//                if (plan.getWeek() != null) {
//                    List<TimeBean.Day> listWeek = plan.getWeek();
//                    int size = listWeek.size();
//                    LogUtil.i(TAG, "week.size=" + size);
//                    for (int i = 0; i < size; i++) {
//                        LogUtil.i(TAG, "week.number=" + ((TimeBean.Day)listWeek.get(i)).getNumber() + ", week.time=" + ((TimeBean.Day)listWeek.get(i)).getTimeX());
//                    }
//                }
//                LogUtil.i(TAG, "month=" + plan.getMonth());
//
//                if (plan.getMonth() != null) {
//                    List<TimeBean.Day> listMonth = plan.getMonth();
//                    int size = listMonth.size();
//                    LogUtil.i(TAG, "month.size=" + size);
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(0)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(0)).getTimeX());
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(1)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(1)).getTimeX());
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(2)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(2)).getTimeX());
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(3)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(3)).getTimeX());
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(4)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(4)).getTimeX());
//                    LogUtil.i(TAG, "month.number=" + ((TimeBean.Day)listMonth.get(5)).getNumber() + ", month.time=" + ((TimeBean.Day)listMonth.get(5)).getTimeX());
////                    for (int i = 0; i < size; i++) {
////                        LogUtil.i(TAG, "month.number=" + ((TimePlan.Day)listMonth.get(i)).getNumber() + ", month.time=" + ((TimePlan.Day)listMonth.get(i)).getTimeX());
////                    }
//                }
//                if (plan == null) {
//                    return;
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                LogUtil.i(TAG, "e=" + e);
//            }
//        }
//    }
}
