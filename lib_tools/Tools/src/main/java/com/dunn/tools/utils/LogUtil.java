package com.dunn.tools.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Author:zhuyidan
 * Date:2021/7/29 16:30
 * Description:LogUtil
 */
public class LogUtil {
    public static final boolean isLog = true;

    public static void v(String module,String msg) {
        if (isLog) {
            Log.v("AppBus[", getPrefix(module)+" "+msg);
        }
    }

    public static void d(String module,String msg) {
        if (isLog) {
            Log.d("AppBus[", getPrefix(module)+" "+msg);
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
