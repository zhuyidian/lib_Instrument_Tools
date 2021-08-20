package com.dunn.tools.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;


public class AndroidUtil {

    /***
     * 绑定service检查
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
        LogUtil.d("check action","createExplicitFromImplicitIntent: action="+implicitIntent.getAction());
        LogUtil.d("check action","createExplicitFromImplicitIntent: localpackageName="+context.getPackageName());
        LogUtil.d("check action","createExplicitFromImplicitIntent: resolveInfo.size="+(resolveInfo!=null?resolveInfo.size():"null"));
        for(ResolveInfo info : resolveInfo){
            LogUtil.d("check action","createExplicitFromImplicitIntent: packageName="+info.serviceInfo.packageName+
                    ", className="+info.serviceInfo.name);
        }

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
                LogUtil.d("check action","createExplicitFromImplicitIntent: !!!go to bind!!! packageName="+packageName+", className="+className+", action="+action);
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
}
