package com.coocaa.remoteplatform.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.coocaa.remoteplatform.core.R;
import com.coocaa.remoteplatform.core.common.Utils;
import com.coocaa.remoteplatform.core.request.HomeHeader;
import com.coocaa.remoteplatform.core.request.TimeHttpMethod;
import com.remoteplatform.commom.LogUtil;

/**
 * @ClassName: RemotePlatformService
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 5:29 PM
 * @Description:
 */
public class RemotePlatformService extends Service {
    private static final String TAG = "RemotePlatformService";
    private static final String CHANNEL_ID = "RemotePlatformServiceID";
    private static final String CHANNEL_NAME = "RemotePlatformServiceName";
    private static final String ACTIVE_ID = "activeId";
    private static final String DEVICE_ID = "deviceId";

    private IMain main = null;
    private ServiceStubImpl stub = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        main = Main.getInstance(getApplicationContext());
        stub = new ServiceStubImpl(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        Utils.startForeground(CHANNEL_ID, CHANNEL_NAME, R.drawable.ic_launcher, this);
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        Utils.startForeground(CHANNEL_ID, CHANNEL_NAME, R.drawable.ic_launcher, this);
        handleIntent(intent);
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {
        Log.i(TAG, "handleIntent: ");
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(ACTIVE_ID)) {
            String activeId = intent.getStringExtra(ACTIVE_ID);
            Log.i(TAG, "handleIntent: activeId=" + activeId);
            main.getAttachInfo().setActiveId(activeId);
        }
        if (intent.hasExtra(DEVICE_ID)) {
            String deviceId = intent.getStringExtra(DEVICE_ID);
            Log.i(TAG, "handleIntent: deviceId=" + deviceId);
            main.getAttachInfo().setDeviceId(deviceId);

            LogUtil.i("time-api", "get time data init deviceId=" + deviceId);
            main.requestTimeData();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "RemotePlatformService destroy!!!");
        stub.destroy();
        super.onDestroy();
    }
}
