package com.dunn.instrument.tools.framework.reboot;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.dunn.instrument.tools.log.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Author:zhuyidian
 * Date:2021/9/7 17:42
 * Description:RebootUtil
 */
public class RebootUtil {

    /**
     *
     * @param mContext
     * 说明：
     *  1，需要<uses-permission android:name="android.permission.REBOOT" />权限
     */
    public static void rebootForBroadcast(Context mContext){
        try {
            Intent reboot = new Intent(Intent.ACTION_REBOOT);
            reboot.putExtra("nowait", 1);
            reboot.putExtra("interval", 1);
            reboot.putExtra("window", 0);
            mContext.sendBroadcast(reboot);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("reboot","e="+e);
        }
    }

    /**
     *
     * @param mContext
     * 说明：
     *  1，需要<uses-permission android:name="android.permission.REBOOT" />权限
     */
    public static void rebootForPowerManager(Context mContext){
        try {
            PowerManager pManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            pManager.reboot(null); //重启,需要系统权限
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("reboot","e="+e);
        }
    }

    public static void rebootForExec() {
        try {
            LogUtil.i("reboot", "reboot exec");
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
            LogUtil.i("reboot", "reboot exec e="+e);
        }
    }
}
