package com.dunn.tools.shutdown;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.dunn.tools.log.LogUtil;

import java.io.DataOutputStream;
import java.lang.reflect.Method;

public class ShutdownUtil {

    public static void shutdownForActivity(Context mContext){
        try {
            Intent shutdown = new Intent(Intent.ACTION_SHUTDOWN);
            shutdown.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(shutdown);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("shutdown","e="+e);
        }
    }

    public static void rebootForBroadcast(Context mContext){
        try {
            Intent reboot = new Intent(Intent.ACTION_REBOOT);
            reboot.putExtra("nowait", 1);
            reboot.putExtra("interval", 1);
            reboot.putExtra("window", 0);
            mContext.sendBroadcast(reboot);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("shutdown","e="+e);
        }
    }

    /**
     * 执行命令
     *
     * @param command 1、获取root权限 "chmod 777 "+getPackageCodePath()
     *                2、关机 reboot -p
     *                3、重启 reboot
     */
    public static boolean execCmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            LogUtil.i("shutdown","command="+command);
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            LogUtil.e("shutdown","e="+e);
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("shutdown","e1="+e);
            }
        }
        return true;
    }

    public static void shutdownForPowerManager() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
//            try {
//                Class<?> serviceManager = Class.forName("android.os.ServiceManager");
//                Method getService = serviceManager.getMethod("getService", String.class);
//                Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
//                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
//                Method asInterface = stub.getMethod("asInterface", IBinder.class);
//                Object powerManager = asInterface.invoke(null, remoteService);
//                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
//                        boolean.class, String.class, boolean.class);
//                shutdown.invoke(powerManager, false, "", true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                //nothing to do
//                LogUtil.e("shutdown","e="+e);
//            }

            try {
                //获得ServiceManager类
                Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
                //获得ServiceManager的getService方法
                Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
                //调用getService获取RemoteService
                Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
                //获得IPowerManager.Stub类
                Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
                //获得asInterface方法
                Method asInterface = stub.getMethod("asInterface", android.os.IBinder.class);
                //调用asInterface方法获取IPowerManager对象
                Object oIPowerManager = asInterface.invoke(null, oRemoteService);
                //获得shutdown()方法
                Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
                //调用shutdown()方法
                shutdown.invoke(oIPowerManager,false,true);
            } catch (Exception e) {
                e.printStackTrace();
                //nothing to do
                LogUtil.e("shutdown","e="+e);
            }
        }
    }

    public static void rebootForPowerManager(Context mContext){
        try {
            PowerManager pManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            pManager.reboot(null); //重启,需要系统权限
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("shutdown","e="+e);
        }
    }

    public static void shutdownForexe(){
        try{
            //Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","shutdown"}); //关机
            Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"}); //关机
            proc.waitFor();
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.e("shutdown","e="+e);
        }
    }
}
