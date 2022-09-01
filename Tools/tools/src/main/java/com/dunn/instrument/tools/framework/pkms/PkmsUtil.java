package com.dunn.instrument.tools.framework.pkms;

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
     * 根据Intent之action查询ResolveInfo信息
     * @param context
     * @param implicitIntent
     * @return
     */
    public static Intent createExplicitFromImplicitIntent1(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() <=0) {
            return null;
        }

        Intent explicitIntent = new Intent(implicitIntent);
        boolean result = false;
        //过滤本地
        for(ResolveInfo info : resolveInfo){
            if(!info.serviceInfo.packageName.equals(context.getPackageName())){
                // Get component info and create ComponentName
//                ResolveInfo serviceInfo = resolveInfo.get(0);
                ResolveInfo serviceInfo = info;
                String packageName = serviceInfo.serviceInfo.packageName;
                String className = serviceInfo.serviceInfo.name;
                String action = implicitIntent.getAction();

                ComponentName component = new ComponentName(packageName, className);

                // Set the component to be explicit
                explicitIntent.setComponent(component);
                explicitIntent.setAction(action);
                result = true;
                break;
            }
        }



        // Create a new intent. Use the old one for extras and such reuse
//        Intent explicitIntent = new Intent(implicitIntent.getAction());
//        explicitIntent.setAction(implicitIntent.getAction());
//        // Set the component to be explicit
//        explicitIntent.setComponent(component);
//        explicitIntent.setPackage(packageName);

        if(result){
            return explicitIntent;
        }else{
            return null;
        }
    }

    private static Intent explicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() <= 0) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
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

    /**
     * 判断本地是否已经安装好了指定的应用程序包
     *
     * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
     * @return 已安装时返回 true,不存在时返回 false
     */
    public static boolean appIsExist(Context context, String packageNameTarget) {
        if (packageNameTarget == null || packageNameTarget.isEmpty()) {
            return false;
        }

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        for (PackageInfo packageInfo : packageInfoList) {
            if (packageInfo.packageName.equals(packageNameTarget)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断应用是否安装
     * @param context
     * @param pkg
     * @return
     */
    public static boolean isAppExist2(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo mPackageInfo = pm.getPackageInfo(pkg, 0);
            return mPackageInfo != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppExist3(Context context, String packageName) {
        if(TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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

    /**
     * 应用是否使能
     * @param context
     * @param packagename
     * @return
     */
    public static int getApplicationEnabledSetting1(Context context, String packagename) {
        try {
            return context.getPackageManager().getApplicationEnabledSetting(packagename);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取应用版本
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName) {
        try {
            PackageInfo info = context.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            return info == null ? -1 : info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 判断systemUID
     * @param context
     * @param apkPath
     * @return
     */
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

    /**
     * 获取meta data中的xml文件
     * @param context
     * @return
     */
//    protected List<TileServiceInfo> findInternal(Context context) {
//        Log.i(TAG, "findInternal: ");
//        List<TileServiceInfo> result = new ArrayList<>();
//        List<ApplicationInfo> applications = context.getPackageManager().getInstalledApplications(GET_META_DATA);
//        if (applications == null || applications.isEmpty()) {
//            Log.w(TAG, "find: applications empty");
//            return result;
//        }
//        Log.i(TAG, "findInternal: application size: " + applications.size());
//        for (ApplicationInfo app : applications) {
//            Bundle metaData = app.metaData;
//            if (metaData == null) {
//                continue;
//            }
//            if (!metaData.containsKey(ACTION)) {
//                continue;
//            }
//            int xmlRes = metaData.getInt(ACTION);
//            if (xmlRes == 0) {
//                Log.w(TAG, "find: xml resources not config! " + app.packageName);
//                continue;
//            }
//            TileServiceInfo info = new TileServiceInfo(new ComponentName(app.packageName, ""), ACTION);
//            info.mConfigRes = xmlRes;
//            result.add(info);
//            Log.i(TAG, "find: " + app.packageName + " xml: " + xmlRes);
//        }
//        Log.i(TAG, "findInternal: result: " + result.size());
//        return result;
//    }

//    protected List<TileServiceInfo> findInternal(Context context) {
//        List<TileServiceInfo> result = new ArrayList<>();
//        Intent i = new Intent(ACTION);
//        List<ResolveInfo> resolve = context.getPackageManager().queryIntentServices(i, PackageManager.GET_META_DATA);
//        if (resolve == null || resolve.isEmpty()) {
//            Log.i(TAG, "findInternal: resolve info empty");
//            return result;
//        }
//        for (ResolveInfo resolveInfo : resolve) {
//            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
//            if (serviceInfo == null) {
//                continue;
//            }
//            TileServiceInfo info = new TileServiceInfo(new ComponentName(serviceInfo.packageName, serviceInfo.name), ACTION);
//            result.add(info);
//            Log.i(TAG, "findInternal: package: " + info.mCn);
//        }
//        return result;
//    }

    /**
     *
     */
    public void getOtherMetaData(){
//        //在Activity应用<meta-data>元素。
//        ActivityInfo info = this.getPackageManager()
//                .getActivityInfo(getComponentName(),PackageManager.GET_META_DATA);
//        info.metaData.getString("meta_name");
//
//        //在application应用<meta-data>元素。
//        ApplicationInfo appInfo = this.getPackageManager()
//                .getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
//        appInfo.metaData.getString("meta_name");
//
//        //在service应用<meta-data>元素。
//        ComponentName cn = new ComponentName(this, MetaDataService.class);
//        ServiceInfo info = this.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
//        info.metaData.getString("meta_name");
//
//        //在receiver应用<meta-data>元素。
//        ComponentName cn = new ComponentName(context, MetaDataReceiver.class);
//        ActivityInfo info = context.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
//        info.metaData.getString("meta_name");
    }

    /**
     * 获取Application节点下的meta data
     * @param context
     * @return
     */
    public boolean isInstallSdk(Context context) {
        boolean result = false;

        try {
            String localPackageName = context.getPackageName();

            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : list) {
                if (info.metaData != null && !localPackageName.equals(info.packageName)) {
                    boolean value = info.metaData.getBoolean("com.coocaa.appbus.get", false);

                    if (value == true) {
                        result = true;
                        break;
                    }
                }
            }
//            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            String value = appInfo.metaData.getString("api_key");
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<String> packageNameList = new ArrayList<String>();

    /**
     * 获得当前系统所有桌面应用的包名，则可以通过符合桌面应用的属性进行检索
     * @param context
     * @return
     */
    private List getLauncherPackageName(Context context) {
        PackageManager pm = context.getPackageManager();
        //设置指定Intent，当前设置的是符合桌面应用设定的Activity，即：
        // <action android:name="android.intent.action.MAIN" />
        // <category android:name="android.intent.category.LAUNCHER" />
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        //queryIntentActivities()方法检索可以针对给定意图执行的所有活动并返回ResolveInfo。
        //若第二个参数设置为PackageManager.MATCH_DEFAULT_ONLY,则只检索显示添加"android.intent.category.DEFAULT"属性的Activity
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,PackageManager.MATCH_ALL);
        for (ResolveInfo info : resolveInfos) {
            String pkgName = info.activityInfo.packageName;
        }
        return packageNameList;
    }
}
