package com.coocaa.remoteplatform;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.remoteplatform.commom.Utils;


/**
 * @ClassName: MyApplication
 * @Author: XuZeXiao
 * @CreateDate: 4/1/21 4:38 PM
 * @Description:
 */
public class MyApplication extends Application {
    private static final String TAG = "Application";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = Utils.getProcessName(this);
        Log.i(TAG, "onCreate processName: " + processName);
        if (Constant.HOST_PACKAGE_NAME.equalsIgnoreCase(processName)) {
            startMainService();
        }
    }

    private void startMainService() {
        Log.i(TAG, "startMainService: ");
        Intent i = new Intent(Constant.PLATFORM_SERVICE_ACTION);
        i.setPackage(getPackageName());
        com.coocaa.remoteplatform.core.common.Utils.startService(this, i);
    }
}
