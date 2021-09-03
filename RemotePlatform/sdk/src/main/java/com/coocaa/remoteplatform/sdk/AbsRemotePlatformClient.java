package com.coocaa.remoteplatform.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.Utils;

/**
 * @ClassName: ClientReceiver
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 4:19 PM
 * @Description:
 */
public abstract class AbsRemotePlatformClient extends IntentService {
    private static final String TAG = "RemotePlatformClient";
    private static final String channelId = "RemotePlatformClientId";
    private static final String channelName = "RemotePlatformClientName";

    public AbsRemotePlatformClient() {
        super("RemotePlatformClient");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.startForeground(channelId, channelName, R.drawable.remoteplatform_notification_icon, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RemoteCommand command = intent.getParcelableExtra(Constant.INTENT_COMMAND_KEY);
        Log.i(TAG, "onHandleIntent: " + command);
        onNewMessage(command);
    }

    protected abstract void onNewMessage(RemoteCommand command);
}
