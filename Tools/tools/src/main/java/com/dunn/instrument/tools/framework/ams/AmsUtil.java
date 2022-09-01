package com.dunn.instrument.tools.framework.ams;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:AmsUtil
 */
public class AmsUtil {
    private static String processName = null;

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static synchronized String getMyProcessName(Context c) {
        if (processName != null)
            return processName;
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                return processName;
            }
        }
        return "";
    }

    /**
     * 获取当前展示 的Activity名称
     * @return
     */
    public static String getCurrentActivityName(Context context){
        ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    /**
     * 获取top包名
     * @param context
     * @return
     */
    public static String getTopPackageName(Context context) {
        String pkgName = null;
        ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
                ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
                if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (topAppProcess.pkgList != null && topAppProcess.pkgList.length > 0) {
                        return topAppProcess.pkgList[0];
                    } else {
                        pkgName = topAppProcess.processName;
                    }
                }
            } else {
                //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
                List localList = manager.getRunningTasks(1);
                ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList.get(0);
                pkgName = localRunningTaskInfo.topActivity.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkgName;
    }

    public static boolean isAppRunningAtFront(Context c, String pkgname) {
        try {
            ActivityManager am = (ActivityManager) c.getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return componentInfo.getPackageName().equals(pkgname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 获取栈顶activity
     * @return
     */
    public static ComponentName getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(am != null) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0) == null ? null : runningTaskInfos.get(0).topActivity;
            }
        }
        return null;
    }

    /**
     * 杀死apk，需要获得系统支持
     */
    public static void CloseAllApp(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> recentTaskInfos = am.getRunningTasks(100);
        String str = null;
        for (ActivityManager.RunningTaskInfo running : recentTaskInfos) {
            System.out.println(running.baseActivity.getPackageName());
            str = running.baseActivity.getPackageName();
            if (
                    str.equals("com.android.systemui")
                            || str.equals("com.xygala.canbus")
                            || str.equals("com.android.contacts")
                            || str.equals("com.autonavi.xmgd.navigator")// 高德
                            || str.equals("com.baidu.BaiduMap")// 百度
                            || str.equals("cld.navi.c2739.mainframe")// 凯立德
                            || str.equals("com.autonavi.amapauto")// 客户高德地图
                            || str.equals("com.pve.onekeynavi")
                            || str.equals("com.pve.navsetting")
                            || str.equals("com.pve.logoselector")
                            || str.equals("com.xy.brightsetting")
                            || str.equals("com.xygala.pvcanset")
                            || str.equals("com.pve.xysecurity")
                            || str.equals("com.pve.wifi")
                            || str.equals("com.pve.gpsinfo")
                            || str.equals("com.pve.time")
                            || str.equals("com.pve.wallpaper")
                            || str.equals("com.pve.sysrestore")
                            || str.equals("com.pve.language")
                            || str.equals("com.pve.aps")
                            || str.equals("com.pve.steering")
                            || str.equals("com.android.xy.volumesetting")
                            || str.equals("com.android.xysysteminfo")
                            || str.equals("com.acloud.stub.calendar")
                            || str.equals("com.acloud.stub.calculator")
                            || str.equals("com.autochips.filebrowser")
                            || str.equals("com.acloud.stub.onekeyclean")
                            || str.equals("com.android.settings")// 设置
                            || str.equals("android")
                            || str.equals("com.estrongs.android.pop")// ES文件管理
                            || str.equals("com.tencent.mm")// 微信
                            || str.equals("com.tencent.mobileqq")// QQ
                            || str.equals("com.ac83xx.android")// USB Switch
            ) {
                //如果当前运行的是这些apk，那么就不需要杀死
            }
            else {   //否则就杀死apk
                Method method;

                try {
                    //这个方法必须获得系统的支持，才可以正常杀死apk
                    //例如：如果在eclipse工程中使用这个方法，那么需要在AndroidManifest.xml中加上android:sharedUserId="android.uid.system"
                    //或者例如：如果直接在android系统项目中使用这个方法，那么可以直接使用，因为项目是由系统编译的，已经获得了系统支持
                    method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
                    method.invoke(am, str);
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 杀死apk，需要被杀死的apk做相应的处理
     * @param mContext
     */
    public static void CloseAllApp1(Context mContext) {
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        Method forceStopPackage;
        List<ActivityManager.RunningTaskInfo> rti = am.getRunningTasks(100);
        int rtiSize = rti.size();
        int i = 0;
        for (i=0; i<rtiSize; i++)
        {
            String  str =rti.get(i).baseActivity.getPackageName();

            if(    str.equals("com.acloud.stub.cdplay")
                    || str.equals("com.acloud.stub.localmusic")
                    || str.equals("com.acloud.stub.music")
                    || str.equals("com.acloud.stub.newonlinemusic")
                    || str.equals("com.acloud.stub.newonlineradio")
                    || str.equals("com.acloud.stub.news")
                    || str.equals("com.acloud.stub.video")
            ){
                Intent back_intent = new Intent("com.acloud.intent.android.MAINUI_UPDATE");
                //这里的getTopActivityPackageName要自己处理
                //String[] infos = {"KillAppSync", "", "?" + getTopActivityPackageName() + "?"};
                //back_intent.putExtra("extras", infos);
                mContext.sendBroadcast(back_intent);
            }
        }
    }

    /**
     * 判断服务是否运行
     * @param context
     * @param serviceName
     * @return
     */
    private boolean serviceAlive(Context context, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
