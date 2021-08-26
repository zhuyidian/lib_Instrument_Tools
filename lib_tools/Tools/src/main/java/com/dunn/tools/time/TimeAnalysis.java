package com.dunn.tools.time;

import com.dunn.tools.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class TimeAnalysis {

    public static void onMessage(String content){
        final Gson gsonBuilder = new GsonBuilder().create();
        java.lang.reflect.Type type = new TypeToken<TimeBean>() {}.getType();
        try {
            //TimeBean plan = gson.fromJson(content, TimeBean.class);
            final TimeBean bean = gsonBuilder.fromJson(content, type);
            if(bean==null) return;

            LogUtil.i("time", "bean=" + bean);
            LogUtil.i("time", "planType=" + bean.getPlanType());
            LogUtil.i("time", "switch=" + bean.getSwitchX());
            LogUtil.i("time", "volume=" + bean.getVolume());
            LogUtil.i("time", "delay=" + bean.getDelay());

            if(bean.getTime()!=null){
                List<String> listTime = bean.getTime();
                int size = listTime.size();
                LogUtil.i("time", "time.size=" + size);
                for (int i = 0; i < size; i++) {
                    LogUtil.i("time", "time.str=" + listTime.get(i));
                }
            }else{
                LogUtil.i("time", "time=null");
            }

            if (bean.getWeek() != null) {
                List<TimeBean.DayBean> listWeek = bean.getWeek();
                int size = listWeek.size();
                LogUtil.i("time", "week.size=" + size);
                for (int i = 0; i < size; i++) {
                    LogUtil.i("time", "week.number=" + listWeek.get(i).getNumber() + ", week.time=" + listWeek.get(i).getTimeX());
                }
            }else{
                LogUtil.i("time", "week=null");
            }

            if (bean.getMonth() != null) {
                List<TimeBean.DayBean> listMonth = bean.getMonth();
                int size = listMonth.size();
                LogUtil.i("time", "month.size=" + size);
                for (int i = 0; i < size; i++) {
                    LogUtil.i("time", "month.number=" + listMonth.get(i).getNumber() + ", month.time=" + listMonth.get(i).getTimeX());
                }
            }else{
                LogUtil.i("time", "month=null");
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.i("time", "e=" + e);
        }
    }
}
