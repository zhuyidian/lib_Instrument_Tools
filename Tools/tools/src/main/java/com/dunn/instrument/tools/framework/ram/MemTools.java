package com.dunn.instrument.tools.framework.ram;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dunn.instrument.tools.log.LogUtil;
import com.dunn.instrument.tools.reflect.Reflector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemTools {
    private static final String TAG = "MemTools";
    private static Reflector memReflector;
    private static final String MEMORY_FILE_PATH = "/proc/meminfo";
    private static final int INVALID = 0;

    public static MemInfo getSystemMemInfo() {
        long start = System.currentTimeMillis();
        MemInfo memInfo = new MemInfo();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(MEMORY_FILE_PATH), "UTF-8"));
            String line = bufferedReader.readLine();
            while (null != line) {
                String[] args = line.split("\\s+");
                if ("MemTotal:".equals(args[0])) {
                    memInfo.memTotal = Long.parseLong(args[1]);
                } else if ("MemFree:".equals(args[0])) {
                    memInfo.memFree = Long.parseLong(args[1]);
                } else if ("MemAvailable:".equals(args[0])) {
                    memInfo.memAvailable = Long.parseLong(args[1]);
                } else if ("Buffers:".equals(args[0])) {
                    memInfo.buffers = Long.parseLong(args[1]);
                } else if ("Cached:".equals(args[0])) {
                    memInfo.cached = Long.parseLong(args[1]);
                    //break;
                } else if ("SwapTotal:".equals(args[0])) {
                    memInfo.swapTotal = Long.parseLong(args[1]);
                    //break;
                } else if ("SwapFree:".equals(args[0])) {
                    memInfo.swapFree = Long.parseLong(args[1]);
                    break;
                }
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getSystemMemInfo e: " + e);
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "getSystemMemInfo close reader e: " + e);
            }
        }
        LogUtil.i(TAG, "getSystemMemInfo cost: " + (System.currentTimeMillis() - start));
        return memInfo;
    }

    //安卓Q开始获取的不是实时数据
    //Return total private dirty memory usage in kB
    //在dongle上面发现Debug的getMemoryInfo获取内存不准确，但getProcessMemoryInfo通过getMemoryInfo获取却是准确的
    public static Debug.MemoryInfo[] getProcessMemoryInfo(Context context, int[] pids) {
        if (pids == null || pids.length < 1) {
            return new Debug.MemoryInfo[0];
        }
        long start = System.currentTimeMillis();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(pids);
        LogUtil.i(TAG, "getProcessMemoryInfo cost: " + (System.currentTimeMillis() - start));
        return memoryInfos;
    }

    public static Debug.MemoryInfo[] getMemoryInfos(int[] pids) {
        long start = System.currentTimeMillis();
        Debug.MemoryInfo[] memoryInfos = new Debug.MemoryInfo[pids.length];
        try {
            if (memReflector == null) {
                memReflector = Reflector.on(Debug.class).method("getMemoryInfo", int.class, Debug.MemoryInfo.class);
            }
            for (int i = 0; i < pids.length; i++) {
                memoryInfos[i] = new Debug.MemoryInfo();
                memReflector.call(pids[i], memoryInfos[i]);
                // Log.e(TAG, "getProcessMemoryInfo: " + i +" " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "getMemoryInfos cost: " + (System.currentTimeMillis() - start));
        return memoryInfos;
    }

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    //系统签名可以获取其他应用实时内存,安卓10开始使用这个
    public static Debug.MemoryInfo getMemoryInfo(int pid) {
        long start = System.currentTimeMillis();
        final Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        try {
            Reflector.on(Debug.class).method("getMemoryInfo", int.class, Debug.MemoryInfo.class).call(pid, memInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e(TAG, "onCreate: " + memInfo.getTotalPss());
        LogUtil.i(TAG, "getMemoryInfo cost: " + (System.currentTimeMillis() - start));
        return memInfo;
    }

    public static int getMemByDumpsys(int pid) {
        String cmd1 = "dumpsys meminfo " + pid;
        int mem_result = 0;
        Process p = null;// 通过runtime类执行cmd命令
        try {
            p = Runtime.getRuntime().exec(cmd1);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        InputStream input = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        InputStream errorInput = null;
        InputStreamReader errorInputStreamReader = null;
        BufferedReader errorReader = null;
        try {
            input = p.getInputStream();
            inputStreamReader = new InputStreamReader(input);
            reader = new BufferedReader(inputStreamReader);
            errorInput = p.getErrorStream();
            errorInputStreamReader = new InputStreamReader(errorInput);
            errorReader = new BufferedReader(errorInputStreamReader);

            String line = "";
            while ((line = reader.readLine()) != null) {// 循环读取
                if (line.startsWith("        TOTAL")) {
                    mem_result = getMemInfo(line);
                    break;
                }
            }

            String eline = "";
            while ((eline = errorReader.readLine()) != null) {// 循环读取
                System.out.println(eline);// 输出
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (input != null) input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (errorReader != null) errorReader.close();
                if (errorInputStreamReader != null) errorInputStreamReader.close();
                if (errorInput != null) errorInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mem_result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<Integer> getMemByDumpsys() {
        long start = System.currentTimeMillis();
        String cmd1 = "dumpsys meminfo -c";
        List<Integer> mem_result = new ArrayList<>();
        Process p = null;// 通过runtime类执行cmd命令
        try {
            p = Runtime.getRuntime().exec(cmd1);
        } catch (IOException e) {
            e.printStackTrace();
            return mem_result;
        }

        try (InputStream input = p.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(input);
             BufferedReader reader = new BufferedReader(inputStreamReader);

             InputStream errorInput = p.getErrorStream();
             InputStreamReader errorInputStreamReader = new InputStreamReader(errorInput);
             BufferedReader errorReader = new BufferedReader(errorInputStreamReader)
        ) {
            String line = "";
            while ((line = reader.readLine()) != null) {// 循环读取
                if (line.startsWith("ram,")) {
                    mem_result = getNum(line);
                    break;
                }
            }

            String eline = "";
            while ((eline = errorReader.readLine()) != null) {// 循环读取
                System.out.println(eline);// 输出
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "getMemByDumpsys cost:" + (System.currentTimeMillis() - start) + " result:" + (mem_result.size() > 2 ? mem_result.get(1) : 0));
        return mem_result;
    }

    public static int getMemInfo(String info) {
        int result = 0;
        Pattern r = Pattern.compile(" [0-9]+ ");
        Matcher m = r.matcher(info);
        if (m.find()) {
            // output(m.group());
            result = changeStringToInt(m.group().trim());
        }
        return result;
    }

    public static List<Integer> getNum(String str) {
        List<Integer> result = new ArrayList<>();
        String regex = "(\\d+)";
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(str);
        while (m.find()) {
            result.add(changeStringToInt(m.group()));
        }
        return result;
    }

    // 把string类型转化为double
    public static Double changeStringToDouble(String text) {
        // return Integer.parseInt(text);
        return Double.valueOf(text);
    }

    // 把string类型转化为int
    public static int changeStringToInt(String text) {
        // return Integer.parseInt(text);
        return Integer.valueOf(text);
    }

    public static class MemInfo {
        public long memTotal;
        public long memFree;
        public long memAvailable;
        public long buffers;
        public long cached;
        public long swapTotal;
        public long swapFree;
    }
}
