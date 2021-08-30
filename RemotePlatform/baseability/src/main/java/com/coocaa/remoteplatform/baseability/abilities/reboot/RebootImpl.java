package com.coocaa.remoteplatform.baseability.abilities.reboot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;

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
        // I/RebootImpl: handleMessage reboot.planType0, reboot.delay=null
        Log.i(TAG, "handleMessage reboot.planType=" + reboot.getPlanType()+", reboot.delay="+reboot.getDelay());
        if (reboot.getPlanType() == 0) {
            PowerManager pManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            pManager.reboot(null); //重启,需要系统权限
            command.replyFinish(mContext).reply();
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(reboot.getDelay());
        } catch (ParseException e) {
            e.printStackTrace();
            command.replyError(mContext).reply();
            return;
        }
        if (date.getTime() <= System.currentTimeMillis()) {
            command.replyError(mContext).reply();
            return;
        }
        Intent intent = new Intent(mContext, RebootAlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        }
        command.replyFinish(mContext).reply();
    }

    @Override
    public String getName() {
        return null;
    }
}
