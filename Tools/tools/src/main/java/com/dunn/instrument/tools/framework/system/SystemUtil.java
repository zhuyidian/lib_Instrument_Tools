package com.dunn.instrument.tools.framework.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:CmsUtil
 */
public class SystemUtil {

    public static double getBootUptime() {
        String ret = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                    "/proc/uptime")));
            String line = reader.readLine();
            String[] results = line.split("\\s+");
            for (int i = 0; i < results.length; i++) {
                if (!results[i].equals("")) {
                    ret = results[i];
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double uptime = 0d;
        try {
            uptime = Double.parseDouble(ret);
        } catch (Exception e) {
            uptime = 0d;
            e.printStackTrace();
        }
        return uptime;
    }

    public static String getSystemVersions() {
        // 系统API版本号-数字格式，例如：29，亦即表示Android API level 29
        //int version = Build.VERSION.SDK_INT;
        // 系统API版本号-字符串格式，例如：29，亦即表示Android API level 29
        //String strVersion = Build.VERSION.SDK_INT;
        // 系统版本号，例如：10，亦即表示Android 10
        String strRelease = Build.VERSION.RELEASE;
        return "Android "+strRelease;
    }
}
