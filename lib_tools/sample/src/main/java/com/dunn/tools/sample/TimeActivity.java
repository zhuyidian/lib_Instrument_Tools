package com.dunn.tools.sample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeAnalysis;
import com.dunn.tools.time.TimeManager;
import com.dunn.tools.time.TimeTest;
import com.dunn.tools.time.TimeUtil;
import com.dunn.tools.time.temp.RemoteCommand;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class TimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getN_day_Pamams(this, 2, "test");
        Date mDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        getHourBAPamams(this, cal.getTimeInMillis(), 3, "test");
        getHourBPamams(this, cal.getTimeInMillis(), 3, "test");


        //TimeDemo.init();
        //TimeUtil.setRtc(TimeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //即使实时
        boolean shutdown_isReal = TimeManager.getInstance().onMessage(0,TimeTest.json_shutdown,new RemoteCommand());
        LogUtil.i("time", "----shutdown_isReal----=" + shutdown_isReal);
        boolean reboot_isReal = TimeManager.getInstance().onMessage(2,TimeTest.json_reboot,new RemoteCommand());
        LogUtil.i("time", "----reboot_isReal----=" + reboot_isReal);
        boolean volume_isReal = TimeManager.getInstance().onMessage(3,TimeTest.json_volume,new RemoteCommand());
        LogUtil.i("time", "----volume_isReal----=" + volume_isReal);

        //定时关机
        //TimeManager.getInstance().onMessage(0,TimeTest.jsonDelay_shutDown,new RemoteCommand());
        //TimeManager.getInstance().onMessage(0,TimeTest.jsonDay_shutDown,new RemoteCommand());
        //TimeManager.getInstance().onMessage(0,TimeTest.jsonWeek_shutDown,new RemoteCommand());
        TimeManager.getInstance().onMessage(0,TimeTest.jsonMonth_shutDown,new RemoteCommand());

        //定时重启
//        TimeManager.getInstance().onMessage(2,TimeTest.jsonDelay_reboot,new RemoteCommand());
//        TimeManager.getInstance().onMessage(2,TimeTest.jsonDay_reboot,new RemoteCommand());
        TimeManager.getInstance().onMessage(2,TimeTest.jsonWeek_reboot,new RemoteCommand());
//        TimeManager.getInstance().onMessage(2,TimeTest.jsonMonth_reboot,new RemoteCommand());

        //定时音量
//        TimeManager.getInstance().onMessage(3,TimeTest.jsonDelay_volume,new RemoteCommand());
        TimeManager.getInstance().onMessage(3,TimeTest.jsonDay_volume,new RemoteCommand());
//        TimeManager.getInstance().onMessage(3,TimeTest.jsonWeek_volume,new RemoteCommand());
//        TimeManager.getInstance().onMessage(3,TimeTest.jsonMonth_volume,new RemoteCommand());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************************************************************/
    /**
     * 说明：根据天数获取天的所有小时段
     * 输入：N_days 天数
     * 返回：ArrayList 天数对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getN_day_Pamams(final Context context, int N_days, final String UID) {
        ArrayList<String> dayList = TimeUtil.getDayHoursList(N_days);
        ArrayList<String> params = new ArrayList<>();

        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;
        for (int i = 0; i < dayList.size(); i++) {
            for (int n = 0; n < 24; n++) {
                //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
                long time = TimeUtil.localToGtm(TimeUtil.string2long(dayList.get(i) + " " + TimeUtil.timeAdd0(n) + ":00:00"));
//                if (time > insertTime) {
                String s_time = TimeUtil.long2string(time);
                String arr_item[] = s_time.split(" ");
                params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
            }
        }

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定天数得到需要获取数据的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }

    /**
     * 说明：根据时间戳获取前后hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     * hourNum 前后多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBAPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeAdd, timeDesc;
        timeAdd = timeDesc = TimeUtil.setIntegerHour(timeMillis);
        temp.add(timeAdd);
        for (int i = 0; i < hourNum; i++) {
            timeAdd = TimeUtil.addHour(timeAdd);
            temp.add(timeAdd);
            timeDesc = TimeUtil.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for (int n = 0; n < temp.size(); n++) {
            long time = TimeUtil.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeUtil.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeUtil.sortA(params);

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定时间戳得到前后几小时的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }

    /**
     * 说明：根据时间戳获取timeMillis前hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     * hourNum 前多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeDesc;
        timeDesc = TimeUtil.setIntegerHour(timeMillis);
        temp.add(timeDesc);
        for (int i = 0; i < hourNum; i++) {
            timeDesc = TimeUtil.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for (int n = 0; n < temp.size(); n++) {
            long time = TimeUtil.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeUtil.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeUtil.sortA(params);

        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.e("test", "根据指定时间戳得到前几小时的日期(年-月-日:时，格林时间)=" + next);
        }
        return params;
    }
}
