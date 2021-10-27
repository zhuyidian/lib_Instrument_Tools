package com.dunn.tools.framework.pkms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

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
    public static Drawable getAppIcon(Context context, String packname){
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
     * 利用Intent查询service
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
}
