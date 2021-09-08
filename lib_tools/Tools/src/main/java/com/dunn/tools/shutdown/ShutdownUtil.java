package com.dunn.tools.shutdown;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.dunn.tools.log.LogUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class ShutdownUtil {

    /**
     *
     * @param mContext
     * 说明：
     *  1，需要<uses-permission android:name="android.permission.SHUTDOWN" />权限
     */
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

    /**
     *
     * @param mContext
     * 说明：
     *  1，需要在AndroidManifest.xml里添加 android:sharedUserId="android.uid.system"和<uses-permission android:name="android.permission.SHUTDOWN/>"权限。
     *  2，android:sharedUserId="android.uid.system"是将自己的程序加入到了系统的进程中，同时也将获得系统的权限。
     *  3，源码中"android.intent.extra.KEY_CONFIRM"是 Intent.EXTRA_KEY_CONFIRM 方法。
     *  4，源码中"android.intent.action.ACTION_REQUEST_SHUTDOWN“ 是 Intent.ACTION_REQUEST_SHUTDOWN 方法
     */
    private void shutdownForActivity1(Context mContext){
        try {
            LogUtil.e("shutdown", "shutdown activity");
            Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("shutdown", "shutdown activity e=" + e);
        }
    }

    /**
     * 说明：
     *  1，>=6.0  PowerManager有shutdown方法  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
     *  2，需要<uses-permission android:name="android.permission.SHUTDOWN" />权限
     */
    public static void shutdownForPM(){
        try {
            LogUtil.i("shutdown", ">= Android6");
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getMethod("getService", String.class);
            Object remoteService = getService.invoke(null, Context.POWER_SERVICE);
            Class<?> stub = Class.forName("android.os.IPowerManager$Stub");
            Method asInterface = stub.getMethod("asInterface", IBinder.class);
            Object powerManager = asInterface.invoke(null, remoteService);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){ //>=7.0
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                shutdown.invoke(powerManager, false, "", true);
            }else{
                Method shutdown = powerManager.getClass().getDeclaredMethod("shutdown",
                        boolean.class, boolean.class);
                shutdown.invoke(powerManager, false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("shutdown","e="+e);
        }
    }

    /**
     * 说明：
     *  1，>=6.0  PowerManager有shutdown方法  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
     *  2，需要<uses-permission android:name="android.permission.SHUTDOWN" />权限
     */
    public static void shutdownForPM1() {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //>=7.0
                //获得shutdown()方法
                Method shutdown = oIPowerManager.getClass().getMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                //调用shutdown()方法
                shutdown.invoke(oIPowerManager,false, "", true);
            }else{
                //获得shutdown()方法
                Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
                //调用shutdown()方法
                shutdown.invoke(oIPowerManager,false,true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //nothing to do
            LogUtil.e("shutdown","e="+e);
        }
    }

    public static void shutdownForExec(){
        try{
            //Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","shutdown"}); //关机
            Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"}); //关机
            proc.waitFor();
        }catch(Exception e){
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
    public static boolean shutdownForExec1(String command) {
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

    public static void shutdownForExec2() {
        try {
            LogUtil.i("shutdown", "shutdown exec reboot -p");
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
            LogUtil.e("shutdown", "shutdown exec e=" + e);
        }
    }

    /**
     * 说明：
     *  1，系统root过，利用系统管理员root身份来行使关机命令
     */
    public static void shutdownForExec3(){
        try {
            cmd("reboot -p").waitFor(); //关机命令
            //createSuProcess("reboot").waitFor(); //这个部分代码是用来重启的
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Process su() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if(rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    private static Process cmd(String cmd) throws IOException {
        DataOutputStream os = null;
        Process process = su();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }
}
