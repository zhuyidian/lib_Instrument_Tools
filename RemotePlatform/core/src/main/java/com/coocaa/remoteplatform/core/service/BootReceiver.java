package com.coocaa.remoteplatform.core.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;

/**
 * @ClassName: BootReceiver
 * @Author: XuZeXiao
 * @CreateDate: 4/13/21 9:20 PM
 * @Description:
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        startMainService(context);
    }

    private void startMainService(Context context) {
        Log.i(TAG, "startMainService: ");
        Intent i = new Intent(Constant.PLATFORM_SERVICE_ACTION);
        i.setPackage(Constant.HOST_PACKAGE_NAME);
        com.coocaa.remoteplatform.core.common.Utils.startService(context, i);
    }
}
