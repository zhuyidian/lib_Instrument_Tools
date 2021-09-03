package com.coocaa.remoteplatform.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;

import java.util.List;

/**
 * @ClassName: BroadcastReceiver
 * @Author: XuZeXiao
 * @CreateDate: 4/9/21 2:26 PM
 * @Description:
 */
public class RemotePlatformReceiver extends BroadcastReceiver {
    private static final String TAG = "RemotePlatformReceiver";
    RemotePlatform platform = null;

    public RemotePlatformReceiver(RemotePlatform platform) {
        this.platform = platform;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || platform == null) {
            return;
        }
        String type = intent.getStringExtra(Constant.REMOTE_METHOD_TYPE_KEY);
        String value = intent.getStringExtra(Constant.REMOTE_METHOD_RESULT_KEY);
        Log.i(TAG, "onReceive: " + type + " : " + value);
        IRemoteCallback callbacks = platform.getCallBack();
        switch (type) {
            case Constant.REMOTE_METHOD_IS_CONNECT:
                handleIsConnect(callbacks, value);
                break;
        }
    }

    private void handleIsConnect(IRemoteCallback callback, String value) {
        if (callback == null) {
            Log.i(TAG, "handleIsConnect: callbacks null");
            return;
        }
        boolean result = Boolean.parseBoolean(value);
        Log.i(TAG, "handleIsConnect: " + result);
        if (result) {
            callback.onConnect();
        } else {
            callback.onDisconnect();
        }
    }
}
