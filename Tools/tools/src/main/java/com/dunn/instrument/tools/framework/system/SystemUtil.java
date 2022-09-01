package com.dunn.instrument.tools.framework.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static boolean isHighPerformance() {
        long KB = 1024;
        long GB = 1024 * 1024 * 1024;
        boolean highPerformance = false;
        long mem = getmem_TOLAL() * KB;
        if (mem >= 1.5f * GB){
            highPerformance = true;
        }else{
            highPerformance = false;
        }
        return highPerformance;
    }

    public static long getmem_TOLAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
}
