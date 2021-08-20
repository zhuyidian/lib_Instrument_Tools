package com.dunn.tools;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by lu on 17-2-18.
 */

public class Android {

    public static final String INTENT_EXTRA_PKG_NAME = "packagename";
    public static final String INTENT_EXTRA_VERSION_CODE = "versioncode";
    public static final String INTENT_EXTRA_START_ACTION = "startaction";

    public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
    public static final int INSTALL_LOCATION_AUTO = 0;
    public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
    public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;

    public static int getConfigInstallLocation(Context context) {
        int ret = Settings.Secure.getInt(context.getContentResolver(), "default_install_location", 0);
//        if(ret == 0)
//            return 2;    //应酷开系统需求，如果location为自动（0），则当成外部（2）来处理。
        return ret;
        // 0：自动
        // 1：内部
        // 2：外部
    }


    public static <T> T getMetaData(Context context, String packageName, String key) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                Object value = null;
                if (applicationInfo.metaData != null) {
                    value = applicationInfo.metaData.get(key);
                }
                if (value == null) {
                    return null;
                }
                return (T) value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T createClassObject(String className) {
        if (!TextUtils.isEmpty(className))
            try {
                return (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    public static boolean isSystemUID(Context context, String apkPath) {
        PackageInfo pkgInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            if (pkgInfo.sharedUserId != null && pkgInfo.sharedUserId.equals("android.uid.system")) {
                return true;
            }
            return isInstalledInSystemPartition(context, pkgInfo.packageName);
        }
        return false;
    }

    public static boolean isInstalledInSystemPartition(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            @SuppressLint("WrongConstant") ApplicationInfo info = pm.getApplicationInfo(pkg, PackageManager.GET_DISABLED_COMPONENTS);
            if (info.sourceDir.startsWith("/system") || info.sourceDir.startsWith("system"))
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static float mDiv = 1;
    private static float mDpi = 1;

    public static synchronized void initResolution(Context context) {
        int height = Android.getDisplayHeight(context);
        int width = Android.getDisplayWidth(context);
        float density = Android.getDisplayDensity(context);
        mDiv = (float) width / 1920.0f;
        mDpi = mDiv / Android.getDisplayDensity(context);
        Log.i("Android", String.format("initResolution %dx%d density:%f div:%f dpi:%f", width, height, density, mDiv, mDpi));
    }

    public static int Div(int x) {
        return (int) (x * mDiv);
    }

    public static int Dpi(int x) {
        return (int) (x * mDpi);
    }

    /**
     * 概述：得到当前density<br/>
     *
     * @return float
     * @date 2013-12-20
     */
    public static float getDisplayDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    /**
     * 概述：得到屏幕的宽度<br/>
     *
     * @return int
     * @date 2013-10-22
     */
    public static int getDisplayWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        if (display == null) {
            return 1920;
        }
        return display.getWidth();
    }

    /**
     * 概述：得到屏幕的高度<br/>
     *
     * @return int
     * @date 2013-10-22
     */
    public static int getDisplayHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        return display.getHeight();
    }

    public static String md5s(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getFreeSpace(String path) {
        try {
            StatFs sf = new StatFs(path);
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            return blockSize * availCount;
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getTotalSpace(String path) {
        try {
            StatFs sf = new StatFs(path);
            long blockSize = sf.getBlockSize();
            long count = sf.getBlockCount();
            return blockSize * count;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void chmodArchive(String archive, Context context) {
        try {
            Log.i("FII", "chmod: " + archive);
            String pkg = context.getPackageName();
            if (new File(archive).exists()) {
                new File(archive).setReadable(true, false);
                new File(archive).setExecutable(true, false);
            } else {
                return;
            }

            File f = new File(archive).getParentFile();

            while (f != null && f.exists()) {
                Log.d("FII", f.getAbsolutePath());
                //防止出现极端情况时把根目录权限修改
                if (f.getAbsolutePath().equals(File.separator)) {
                    break;
                }
                f.setExecutable(true, false);
                //如果修改到路径以"包名"结尾 或者"包名/"结尾时退出
                if (f.getAbsolutePath().endsWith(pkg)
                        || (f.getAbsolutePath().endsWith(File.separator) && f.getAbsolutePath().endsWith(pkg + File.separator))) {
                    Log.d("FII", "path end of: " + pkg);
                    break;
                }
                f = f.getParentFile();
            }
        } catch (Exception e) {
            Log.i("FII", e.toString());
        }

    }

    /**
     * 判断网络是否链接
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {// 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {//当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据包名判断应用是否安装
     * 未安装返回null，已安装返回packinfo
     *
     * @param packageName
     * @return
     */
    public static PackageInfo isInstalled(Context mContext, String packageName) {
        if (mContext == null || packageName == null) {
            Log.e("app", "Launcer: isInstalled param err");
            return null;
        }
        try {
            PackageManager pm = mContext.getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
            for (PackageInfo info : packageInfos) {
                if (info.packageName.equals(packageName)) {
                    return info;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String getTopPackageName(Context context) {
        String pkgName = null;
        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
                ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
                if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if(topAppProcess.pkgList != null && topAppProcess.pkgList.length > 0) {
                        return topAppProcess.pkgList[0];
                    } else {
                        pkgName = topAppProcess.processName;
                    }
                }
            } else {
                //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
                List localList = manager.getRunningTasks(1);
                ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
                pkgName = localRunningTaskInfo.topActivity.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkgName;
    }

    public static ComponentName getLauncherActivity(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, PackageManager.GET_DISABLED_COMPONENTS);
        if (resolveInfo != null && resolveInfo.size() > 0) {
            ResolveInfo info = resolveInfo.get(0);
            return new ComponentName(packageName, info.activityInfo.name);
        }
        return null;
    }

    public static int getVersionCode(Context context, String pkg) {
        PackageManager mPackageManager = context.getPackageManager();
        try {
            return mPackageManager.getPackageInfo(pkg, 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }
}
