package com.coocaa.remoteplatform.core.clientmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @ClassName: PackageStateReceiver
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 4:02 PM
 * @Description:
 */
public class PackageStateReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageStateReceiver";
    private IClientManager mClientManager;

    public PackageStateReceiver(IClientManager mClientManager) {
        this.mClientManager = mClientManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + intent.getAction());
        mClientManager.findClients();
    }
}
