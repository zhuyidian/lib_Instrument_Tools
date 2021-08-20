package com.dunn.tools.sample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.sample.R;
import com.dunn.tools.ActivityManager;
import com.dunn.tools.TimeUtil.TimeConversion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().attach(this);

        setContentView(R.layout.activity_main);

        getN_day_Pamams(this,2,"test");
        Date mDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        getHourBAPamams(this,cal.getTimeInMillis(),3,"test");
        getHourBPamams(this,cal.getTimeInMillis(),3,"test");
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().detach(this);
        super.onDestroy();
    }

    /*************************************************************************/
    /**
     * 说明：根据天数获取天的所有小时段
     * 输入：N_days 天数
     * 返回：ArrayList 天数对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getN_day_Pamams(final Context context, int N_days, final String UID) {
        ArrayList<String> dayList =  TimeConversion.getDayHoursList(N_days);
        ArrayList<String> params = new ArrayList<>();

        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;
        for (int i = 0; i < dayList.size(); i++) {
            for (int n = 0; n < 24; n++) {
                //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
                long time = TimeConversion.localToGtm(TimeConversion.string2long(dayList.get(i) + " " + TimeConversion.timeAdd0(n) + ":00:00"));
//                if (time > insertTime) {
                String s_time = TimeConversion.long2string(time);
                String arr_item[] = s_time.split(" ");
                params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
            }
        }

        Iterator<String> iterator = params.iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            Log.e("test","根据指定天数得到需要获取数据的日期(年-月-日:时，格林时间)="+next);
        }
        return params;
    }
    /**
     * 说明：根据时间戳获取前后hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     *       hourNum 前后多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBAPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeAdd,timeDesc;
        timeAdd=timeDesc= TimeConversion.setIntegerHour(timeMillis);
        temp.add(timeAdd);
        for(int i=0;i<hourNum;i++) {
            timeAdd = TimeConversion.addHour(timeAdd);
            temp.add(timeAdd);
            timeDesc = TimeConversion.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for(int n=0;n<temp.size();n++){
            long time = TimeConversion.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeConversion.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeConversion.sortA(params);

        Iterator<String> iterator = params.iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            Log.e("test","根据指定时间戳得到前后几小时的日期(年-月-日:时，格林时间)="+next);
        }
        return params;
    }
    /**
     * 说明：根据时间戳获取timeMillis前hourNum小时的小时段，包含时间戳所在的小时段
     * 输入：timeMillis 时间戳
     *       hourNum 前多少小时
     * 返回：ArrayList 对应的具体日期,格林时间(yyyy-MM-dd:hh)
     */
    public static ArrayList<String> getHourBPamams(final Context context, long timeMillis, int hourNum, final String UID) {
        ArrayList<String> params = new ArrayList<>();
        ArrayList<Long> temp = new ArrayList<>();
        //将设备插入时间提前一小时，能够得到插入时间所在的小时段内的数据
//        final long insertTime = MyDBUtils.getDbUtils(context).getInsertTime(UID)-CTimeHelper.ins().GreemknTime()-3600000;

        //先将时间字符串转化为long 2018-03-03 +" "+n+":00:00" --->再减去时间偏移--->得到格林时间
        long timeDesc;
        timeDesc= TimeConversion.setIntegerHour(timeMillis);
        temp.add(timeDesc);
        for(int i=0;i<hourNum;i++) {
            timeDesc = TimeConversion.descHour(timeDesc);
            temp.add(timeDesc);
        }
        for(int n=0;n<temp.size();n++){
            long time = TimeConversion.localToGtm(temp.get(n));
//                if (time > insertTime) {
            String s_time = TimeConversion.long2string(time);
            String arr_item[] = s_time.split(" ");
            params.add(arr_item[0] + ":" + arr_item[1].split(":")[0]);
//                }
        }
        params = TimeConversion.sortA(params);

        Iterator<String> iterator = params.iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            Log.e("test","根据指定时间戳得到前几小时的日期(年-月-日:时，格林时间)="+next);
        }
        return params;
    }
}
