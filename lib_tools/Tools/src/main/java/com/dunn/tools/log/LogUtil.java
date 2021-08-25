package com.dunn.tools.log;

import android.util.Log;

import java.util.Locale;

/**
 * Author:zhuyidan
 * Date:2021/7/29 16:30
 * Description:LogUtil
 */
public class LogUtil {
    public static final boolean isLog = true;

    public static void v(String tag,String module,String msg) {
        if (isLog) {
            Log.v(tag, getPrefix(module)+" "+msg);
        }
    }

    public static void v(String module,String msg) {
        if (isLog) {
            Log.v("tools[", getPrefix(module)+" "+msg);
        }
    }

    public static void i(String tag,String module,String msg) {
        if (isLog) {
            Log.i(tag, getPrefix(module)+" "+msg);
        }
    }

    public static void i(String module,String msg) {
        if (isLog) {
            Log.i("tools[", getPrefix(module)+" "+msg);
        }
    }

    public static void d(String tag,String module,String msg) {
        if (isLog) {
            Log.d(tag, getPrefix(module)+" "+msg);
        }
    }

    public static void d(String module,String msg) {
        if (isLog) {
            Log.d("tools[", getPrefix(module)+" "+msg);
        }
    }

    public static void e(String tag,String module,String msg) {
        if (isLog) {
            Log.e(tag, getPrefix(module)+" "+msg);
        }
    }

    public static void e(String module,String msg) {
        if (isLog) {
            Log.e("tools[", getPrefix(module)+" "+msg);
        }
    }

    private static String getPrefix(String module){
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String className = stackTraceElement.getClassName();
            int classNameStartIndex = className.lastIndexOf(".") + 1;
            className = className.substring(classNameStartIndex);
            String methodName = stackTraceElement.getMethodName();
            int methodLine = stackTraceElement.getLineNumber();
            String format = "%s[(%s:%s:%d)]";
            return String.format(Locale.CANADA, format, module, className, methodName, methodLine);
        }catch (Exception e){
            e.printStackTrace();
            return module;
        }
    }
}
