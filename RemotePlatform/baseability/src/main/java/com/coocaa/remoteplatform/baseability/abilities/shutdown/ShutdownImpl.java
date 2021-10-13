package com.coocaa.remoteplatform.baseability.abilities.shutdown;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;
import com.remoteplatform.commom.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

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

        LogUtil.i("time", "---handle message--- shutdown.planType=" + shutdown.getPlanType());
//        if (shutdown.getPlanType() == 0) {
//            shutdown();
//            command.replyFinish(mContext).reply();
//        } else {
//            command.replyError(mContext).reply();
//        }
        shutdown();
        command.replyFinish(mContext).reply();
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

    private void shutdown() {
        LogUtil.i("time", "shutdown sdk version=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  //>=6.0
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //>=5.0
            shutdownForPM();
        } else {   //6.0以下
            //没有 shutdown 方法
            shutdownForExec();
        }
    }

    private void shutdownForPM() {
        try {
            LogUtil.i("time", "shutdown >= Android6");
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getMethod("getService", String.class);
            Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
            Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
            Method asInterface = stub.getMethod("asInterface", IBinder.class);
            Object powerManager = asInterface.invoke(null, remoteService);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //>=7.0
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                shutdown.invoke(powerManager, false, "", true);
            } else {
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, boolean.class);
                shutdown.invoke(powerManager, false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("time", "shutdown e=" + e);
            shutdownForExec();
        }
    }

    private void shutdownForExec() {
        try {
            LogUtil.i("time", "shutdown exec reboot -p");
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
            LogUtil.e("time", "shutdown exec e=" + e);
        }
    }
}
