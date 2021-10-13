package com.coocaa.remoteplatform.baseability.abilities.reboot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;
import com.remoteplatform.commom.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: RebootImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:28 PM
 * @Description:
 */
public class RebootImpl extends AbsAbility {
    private static final String TAG = "RebootImpl";

    @Override
    public void handleMessage(RemoteCommand command) {
        Gson gson = new Gson();
        Reboot reboot = gson.fromJson(command.content, Reboot.class);
        if (reboot == null) {
            command.replyError(mContext).reply();
            return;
        }

        LogUtil.i("time", "---handle message--- reboot.planType=" + reboot.getPlanType() + ", reboot.delay=" + reboot.getDelay());
        //if (reboot.getPlanType() == 0) {
        try {
            rebootForPM(mContext);
            command.replyFinish(mContext).reply();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "---handle message--- e=" + e);
        }
        return;
        //}

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        try {
//            date = simpleDateFormat.parse(reboot.getDelay());
//        } catch (ParseException e) {
//            e.printStackTrace();
//            command.replyError(mContext).reply();
//            return;
//        }
//        if (date.getTime() <= System.currentTimeMillis()) {
//            command.replyError(mContext).reply();
//            return;
//        }
//        Intent intent = new Intent(mContext, RebootAlarmBroadcastReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
//        }
//        command.replyFinish(mContext).reply();
    }

    @Override
    public void realMessage(RemoteCommand command) {
        LogUtil.i("time", "---real message--- command=" + command);
        try {
            rebootForPM(mContext);
            command.replyFinish(mContext).reply();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "---real message--- e=" + e);
        }
    }

    private void rebootForPM(Context mContext) {
         /*
            pManager=android.os.PowerManager@286dd109
            e=java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference
         */
        try {
            PowerManager pManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            LogUtil.i("time", "reboot pManager=" + pManager);
            pManager.reboot(null); //重启,需要系统权限
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "reboot e=" + e);
            rebootForExec();
        }
    }

    private void rebootForExec() {
        try {
            LogUtil.i("time", "reboot exec");
            Process process = Runtime.getRuntime().exec("reboot");
            String data = null;
            BufferedReader errorLine = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader inputLine = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String error = null;
            while ((error = errorLine.readLine()) != null && !error.equals("null")) {
                data += error + "\n";
            }
            String input = null;
            while ((input = inputLine.readLine()) != null && !input.equals("null")) {
                data += input + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("time", "reboot exec e=" + e);
        }
    }

    @Override
    public String getName() {
        return null;
    }
}
