package com.coocaa.remoteplatform.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.Utils;
import com.coocaa.remoteplatform.core.service.AttachInfo;
import com.coocaa.remoteplatform.core.service.IRemotePlatformClientCallback;
import com.coocaa.remoteplatform.core.service.IRemotePlatformService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @ClassName: RemotePlatform
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 11:39 AM
 * @Description:
 */
public class RemotePlatform implements IRemotePlatform {
    private static final String TAG = "RemotePlatform";
    private IRemoteCallback mCallBack = null;
    private IRemotePlatformService mService = null;
    private Context mContext = null;
    private CountDownLatch countDownLatch = null;
    private RemotePlatformReceiver mReceiver = null;
    private final Map<String, IPCSocketClient> mClients = new HashMap<>();

    static class InstanceClass {
        static RemotePlatform instance = new RemotePlatform();
    }

    public static RemotePlatform getInstance() {
        return InstanceClass.instance;
    }

    private IRemotePlatformClientCallback callback = new IRemotePlatformClientCallback.Stub() {
        @Override
        public Map invokeCallBackMethod(String method, Map params) throws RemoteException {
            Log.i(TAG, "onCallBack: " + method);
            if (mCallBack != null) {
                return mCallBack.invokeClientMethod(method, params);
            } else {
                return null;
            }
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            mService = IRemotePlatformService.Stub.asInterface(service);
            try {
                mService.setClientCallback(mContext.getPackageName(), callback);
                service.linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (countDownLatch != null) {
                countDownLatch.countDown();
                Log.i(TAG, "onServiceConnected: countDown");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: ");
            mService = null;
        }
    };

    private final IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i(TAG, "binderDied: ");
            mService = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectService();
                }
            }).start();
        }
    };

    @Override
    public void init(Context context) {
        Log.i(TAG, "init: ");
        mContext = context;
        registerReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectService();
            }
        }).start();
    }

    private void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new RemotePlatformReceiver(this);
        }
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.CLIENT_RECEIVER_ACTION);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void registerEventHandler(IRemoteCallback callback) {
        Log.i(TAG, "registerEventHandler: ");
        mCallBack = callback;
    }

    @Override
    public void unRegisterEventHandler(IRemoteCallback callback) {
        Log.i(TAG, "unRegisterEventHandler: ");
        mCallBack = null;
    }

    public IRemoteCallback getCallBack() {
        return mCallBack;
    }

    @Override
    public void sendMessage(Context context, RemoteCommand command) {
        Log.i(TAG, "sendMessage: ");
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_COMMAND_KEY, command);
        intent.setAction(Constant.REPLY_SERVICE_ACTION);
        intent.setPackage(Constant.HOST_PACKAGE_NAME);
        Utils.startService(context, intent);
    }

    @Override
    public boolean isConnect() {
        Log.i(TAG, "isConnect: ");
        if (!checkService()) {
            return false;
        }
        Map<String, String> result = null;
        try {
            result = mService.invokeMethod(Constant.REMOTE_METHOD_IS_CONNECT, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return false;
        }
        String value = result.get(Constant.REMOTE_METHOD_RESULT_KEY);
        return Boolean.parseBoolean(value);
    }

    @Override
    public void subScribeTopic(String topic, IIPCSocketReceiver callback, int messageQueueLength) {
        IPCSocketClient client = getSocketClient(topic, messageQueueLength);
        client.connectToServer(topic, callback);
    }

    @Override
    public void unSubScribeTopic(String topic) {
        if (!checkService()) {
            return;
        }
        IPCSocketClient client = mClients.get(topic);
        if (client == null) {
            return;
        }
        client.disConnectInitiative(topic);
        mClients.remove(topic);
    }

    private IPCSocketClient getSocketClient(String topic, int messageQueueLength) {
        if (mClients.containsKey(topic)) {
            return mClients.get(topic);
        }
        IPCSocketClient client = new IPCSocketClient(messageQueueLength);
        mClients.put(topic, client);
        return client;
    }

    @Override
    public void sendTopicMessage(String method, String topic, String message) {
        IPCSocketClient client = mClients.get(topic);
        if (client != null) {
            client.enqueueMessage(method, topic, message);
        }
    }

    @Override
    public AttachInfo getAttachInfo() {
        Log.i(TAG, "getAttachInfo: ");
        if (!checkService()) {
            return null;
        }
        AttachInfo result = null;
        try {
            result = mService.getAttachInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, String> packageMethod(String targetPackageName, Map<String, String> params) {
        Log.i(TAG, "packageMethod: ");
        if (!checkService()) {
            return null;
        }
        params.put("package", targetPackageName);
        Map<String, String> result = null;
        try {
            result = mService.invokeMethod(Constant.REMOTE_METHOD_PACKAGE_METHOD, params);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean checkService() {
        if (!isServiceAlive()) {
            Log.w(TAG, "checkService: binder not alive");
            return connectService();
        }
        return true;
    }

    private boolean isServiceAlive() {
        return mService != null && mService.asBinder().isBinderAlive();
    }

    private synchronized boolean connectService() {
        Log.i(TAG, "connectService: ");
        if (isServiceAlive()) {
            return true;
        }
        if (mContext == null) {
            Log.e(TAG, "connectService failed: context == null");
            return false;
        }
        countDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setPackage(Constant.HOST_PACKAGE_NAME);
        intent.setAction(Constant.PLATFORM_SERVICE_ACTION);
        mContext.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
            Log.i(TAG, "connectService: countDownLatch finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mService != null;
    }

    @Override
    public void destroy() {
        if (mContext != null) {
            mContext.unregisterReceiver(mReceiver);
            mContext.unbindService(serviceConnection);
        }
    }
}
