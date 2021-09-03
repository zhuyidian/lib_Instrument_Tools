package com.coocaa.remoteplatform.baseability.abilities.shutdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.rtc.Alarm;
import com.coocaa.remoteplatform.baseability.abilities.rtc.AlarmReceiver;
import com.coocaa.remoteplatform.baseability.abilities.time.TimeManager;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;
import com.remoteplatform.commom.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: Alarm
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 5:13 PM
 * @Description:
 */
public class ShutdownImpl extends AbsAbility {
    private static final String TAG = "ShutdownImpl";

    @Override
    public void handleMessage(RemoteCommand command) {
        boolean isReal = TimeManager.getInstance().onMessage(command, this);
        if (isReal) return;
        LogUtil.i("time", "---handle message--- command=" + command);

        Gson gson = new Gson();
        Shutdown shutdown = gson.fromJson(command.content, Shutdown.class);
        if (shutdown == null) {
            command.replyError(mContext).reply();
            return;
        }

        LogUtil.i("time", "---handle message--- shutdown.planType=" + shutdown.getPlanType());
        if (shutdown.getPlanType() == 0) {
            shutdown();
            command.replyFinish(mContext).reply();
        } else {
            command.replyError(mContext).reply();
        }
    }

    @Override
    public void realMessage(RemoteCommand command) {
        LogUtil.i("time", "---real message--- command=" + command);
        shutdown();
        command.replyFinish(mContext).reply();
    }

    @Override
    public String getName() {
        return null;
    }

    public boolean shutdown() {
        LogUtil.i("time", "---shutdown--- sdk version=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  //>=8.0
            try {
                LogUtil.i("time", "---shutdown--- shutdown is exe Android8xxx");
                Class<?> serviceManager = Class.forName("android.os.ServiceManager");
                Method getService = serviceManager.getMethod("getService", String.class);
                Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
                Method asInterface = stub.getMethod("asInterface", IBinder.class);
                Object powerManager = asInterface.invoke(null, remoteService);
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                shutdown.invoke(powerManager, false, "", true);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("time", "---shutdown--- e=" + e);
                shutdownForExec();
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //>=7.0
            try {
                LogUtil.i("time", "---shutdown--- shutdown is exe Android7");
                Class<?> serviceManager = Class.forName("android.os.ServiceManager");
                Method getService = serviceManager.getMethod("getService", String.class);
                Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
                Method asInterface = stub.getMethod("asInterface", IBinder.class);
                Object powerManager = asInterface.invoke(null, remoteService);
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                shutdown.invoke(powerManager, false, "", true);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("time", "---shutdown--- e=" + e);
                shutdownForExec();
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  //>=6.0
            try {
                LogUtil.i("time", "---shutdown--- shutdown is exe Android6");
                Class<?> serviceManager = Class.forName("android.os.ServiceManager");
                Method getService = serviceManager.getMethod("getService", String.class);
                Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
                Method asInterface = stub.getMethod("asInterface", IBinder.class);
                Object powerManager = asInterface.invoke(null, remoteService);
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, boolean.class);
                shutdown.invoke(powerManager, false, true);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("time", "---shutdown--- e=" + e);
                shutdownForExec();
                return false;
            }
        } else{   //6.0以下
            //没有 shutdown 方法
            shutdownForExec();
        }

        return false;
    }

    private void shutdownForExec() {
        try {
            LogUtil.i("time", "---shutdown exec--- reboot -p");
            Process process = Runtime.getRuntime().exec("reboot -p");
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
            LogUtil.e("time", "---shutdown exec--- e=" + e);
        }
    }
}
