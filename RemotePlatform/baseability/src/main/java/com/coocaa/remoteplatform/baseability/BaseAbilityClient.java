package com.coocaa.remoteplatform.baseability;

import android.util.Log;

import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.sdk.AbsRemotePlatformClient;
import com.remoteplatform.commom.LogUtil;
import com.skyworth.framework.skysdk.ipc.SkyApplication;

/**
 * @ClassName: BaseAbilityClient
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 4:57 PM
 * @Description:
 */
public class BaseAbilityClient extends AbsRemotePlatformClient {
    private static final String TAG = "RemoteBaseAbility";

    @Override
    public void onCreate() {
        super.onCreate();
        BaseAbilityImpl.getInstance().init(getApplicationContext());
        SkyApplication.init(getApplicationContext());
        LogUtil.i("time", "SkyApplication.init");
    }

    @Override
    protected void onNewMessage(RemoteCommand command) {
        Log.i(TAG, "onNewMessage: " + command);
        BaseAbilityImpl.getInstance().handleMessage(getApplicationContext(), command);
    }
}
