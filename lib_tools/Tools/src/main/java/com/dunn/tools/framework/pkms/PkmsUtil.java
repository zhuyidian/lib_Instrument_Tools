package com.dunn.tools.framework.pkms;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Author:Administrator
 * Date:2021/10/27 14:37
 * Description:PkmsUtils
 */
public class PkmsUtil {

    /**
     * 查询所有的安装包
     * @param mContext
     */
    public static void isInstalled(Context mContext) {
        if (mContext != null) {
            try {
                PackageManager pm = mContext.getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                Iterator var4 = packageInfos.iterator();
                PackageInfo info;
                while (var4.hasNext()){
                    info = (PackageInfo) var4.next();
                    //Log.i(TAG, "isInstalled: packageName=" + info.packageName);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }
    }

    /**
     * 获取程序 图标
     * @param context
     * @param packname 应用包名
     * @return
     */
    public static Drawable getAppIcon1(Context context, String packname){
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            //获取到应用信息
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取程序的版本号
     * @param context
     * @param packname
     * @return
     */
    public static String getAppVersion(Context context,String packname){
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packname;
    }

    /**
     * 获取程序的名字
     * @param context
     * @param packname
     * @return
     */
    public static String getAppName(Context context,String packname){
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packname;
    }

    /**
     *
     * @param context
     * @param pkg
     * @return
     */
    public static String getAppName1(Context context, String pkg) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg, 0);
            return pm.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable getAppIcon(Context context, String pkg) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg, 0);
            return pm.getApplicationIcon(pkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getActivityName(Context context, ComponentName cn) {
        try {
            PackageManager pm = context.getPackageManager();
            ActivityInfo ai = pm.getActivityInfo(cn, 0);
            return ai.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable getActivityIcon(Context context, ComponentName cn) {
        try {
            PackageManager pm = context.getPackageManager();
            ActivityInfo ai = pm.getActivityInfo(cn, 0);
            return ai.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ComponentName getLauncherActivity_1(Context context, String packageName) {
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

    /*
     * 获取程序的权限
     */
    public static String[] getAllPermissions(Context context,String packname){
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            PackageInfo packinfo =  pm.getPackageInfo(packname, PackageManager.GET_PERMISSIONS);
            //获取到所有的权限
            return packinfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取程序的签名
     * @param context
     * @param packname
     * @return
     */
    public static String getAppSignature(Context context,String packname){
        try {
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            PackageInfo packinfo = pm.getPackageInfo(packname, PackageManager.GET_SIGNATURES);
            //获取当前应用签名
            return packinfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packname;
    }

    /**
     * 获取Launcher类名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static ComponentName getLauncherActivity(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
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

    /***
     * 利用Intent的action查询service
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        Intent explicitIntent = new Intent(implicitIntent);
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * 根据action查询禁用的组件
     */
    public static List<ComponentName> getComponentNames(Context context, String action) {
        List<ComponentName> ret = new ArrayList<ComponentName>();
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);
        List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
        for (ResolveInfo i : list)
            ret.add(new ComponentName(i.activityInfo.packageName, i.activityInfo.name));

        list = pm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
        for (ResolveInfo i : list)
            ret.add(new ComponentName(i.activityInfo.packageName, i.activityInfo.name));
        Collections.sort(ret, new Comparator<ComponentName>() {
            public int compare(ComponentName arg0, ComponentName arg1) {

                return arg0.getPackageName().compareTo(arg1.getPackageName());
            }
        });
        return ret;
    }

    /**
     * 根据包名查询禁用的组件
     */
    public static List<ComponentName> getCOMPLETEDComponentNamesByPkg(Context context, String pkg) {
        List<ComponentName> ret = new ArrayList<ComponentName>();
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        intent.setPackage(pkg);
        List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
        if (list != null && list.size() > 0) {
            for (ResolveInfo i : list) {
                Log.d("completed", "getCOMPLETEDComponentNamesByPkg    pkg:" + i.activityInfo.packageName + " activity:" + i.activityInfo.name);
                ret.add(new ComponentName(i.activityInfo.packageName, i.activityInfo.name));
            }
        }
        return ret;
    }

    /**
     * 设置ComponentName组件使能状态
     * @param context
     * @param comptName
     * @param state
     */
    public static void setComponentEnabledSetting(Context context, ComponentName comptName,
                                                  int state) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(comptName, state, PackageManager.DONT_KILL_APP);//PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据包名获取应用使能状态
     * @param context
     * @param packagename
     * @return
     */
    public static int getApplicationEnabledSetting(Context context, String packagename) {
        try {
            return context.getPackageManager().getApplicationEnabledSetting(packagename);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据ComponentName获取组件使能状态
     * @param context
     * @param comptName
     * @return
     */
    public static int getComponentEnabledSetting(Context context, ComponentName comptName) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getComponentEnabledSetting(comptName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Activity是否配置到AndroidManifest.xml检查
     * @param mContext
     * @param targetPackage
     * @param targetClass
     * @return
     */
    public static boolean checkModelActivityInfo(Context mContext,String targetPackage,String targetClass){
        PackageManager pm = mContext.getPackageManager();
        Intent resolveIntent = new Intent();
        resolveIntent.setClassName(targetPackage,targetClass);
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(resolveIntent, 0);
        if (resolveInfo!=null&&resolveInfo.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 获取安装应用的resources
     *
     * @param context      上下文
     * @param mPackageName 包名
     * @return 装应用的resources
     */
    public static Resources getInstalledResources(Context context, String mPackageName) {
        String resPackage = mPackageName;
        if (TextUtils.isEmpty(resPackage)) {
            resPackage = context.getPackageName();
        }
        Resources resources = null;
        if ("android".equals(resPackage)) {
            resources = Resources.getSystem();
        } else {
            final PackageManager pm = context.getPackageManager();
            try {
                ApplicationInfo ai = pm.getApplicationInfo(resPackage, PackageManager.MATCH_UNINSTALLED_PACKAGES);
                if (ai != null) {
                    resources = pm.getResourcesForApplication(ai);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

            }
        }
        return resources;
    }

    /**
     * 获取Application的meta_data
     * @param context
     * @param packageName
     * @param key
     * @return
     */
    public static Object getMetaData(Context context, String packageName, String key) {
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
                return value;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用VersionCode
     * @param context
     * @param pkg
     * @return
     */
    public static int getVersionCode(Context context, String pkg) {
        PackageManager mPackageManager = context.getPackageManager();
        try {
            return mPackageManager.getPackageInfo(pkg, 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取应用VersionName
     * @param context
     * @param pkg
     * @return
     */
    public static String getVersionName(Context context, String pkg) {
        Log.d("TEST", "getVersionName context:" + context + "  pkg:" + pkg);
        PackageManager mPackageManager = context.getPackageManager();
        try {
            return mPackageManager.getPackageInfo(pkg, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInstalledOnSDCard(Context c, String pkg) {
        PackageManager pm = c.getPackageManager();
        try {
            @SuppressLint("WrongConstant")
            ApplicationInfo info = pm.getApplicationInfo(pkg, PackageManager.GET_DISABLED_COMPONENTS);
//            LogUtils.d("install", " isInstalledOnSDCard:" + info.packageName);
            if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAppNameByPkg(Context context, String pkg) {
        String name = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(pkg, 0);
            name = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getNameByActivity(Context context, String packageName, String activityName) {
        try {
            PackageManager pm = context.getPackageManager();
            ActivityInfo ai = pm.getActivityInfo(new ComponentName(packageName,
                    activityName), 0);
            return ai.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getAppNameByPkg(context, packageName);
        }
    }

    public static List<String> getInstalledPackages(Context c) {
        PackageManager pm = c.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        List<String> packages = new ArrayList<>();
        for (PackageInfo pi : list)
            packages.add(pi.packageName);
        return packages;
    }
}
