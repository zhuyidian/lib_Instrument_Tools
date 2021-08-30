package com.coocaa.remoteplatform.baseability.abilities.shutdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.rtc.Alarm;
import com.coocaa.remoteplatform.baseability.abilities.rtc.AlarmReceiver;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;

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
        Gson gson = new Gson();
        Shutdown shutdown = gson.fromJson(command.content, Shutdown.class);
        if (shutdown == null) {
            command.replyError(mContext).reply();
            return;
        }

        Log.i(TAG, "handleMessage shutdown.planType=" + shutdown.getPlanType());
        if (shutdown.getPlanType() == 0) {
            shutdown();
            command.replyFinish(mContext).reply();
        }else{
            command.replyError(mContext).reply();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    public static boolean shutdown() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            try {
                Log.i(TAG, "handleMessage shutdown is exe");
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
                //nothing to do
                return false;
            }
        }

        return false;
    }
}
