package com.coocaa.remoteplatform.core.service;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.coocaa.pushlib.CCPushSDK;
import com.coocaa.pushlib.impl.PushStateHolder;
import com.coocaa.remoteplatform.core.common.Constant;

import java.util.HashMap;
import java.util.Map;

import static com.coocaa.remoteplatform.core.common.Constant.REMOTE_METHOD_PARAMS_TOPIC;

/**
 * @ClassName: ServiceStubImpl
 * @Author: XuZeXiao
 * @CreateDate: 4/7/21 10:04 PM
 * @Description:
 */
public class ServiceStubImpl extends IRemotePlatformService.Stub {
    private static final String TAG = "ServiceStubImpl";
    private Context mContext;
    private RemoteCallbackList<IRemotePlatformClientCallback> clientCallbacks;

    public ServiceStubImpl(Context context) {
        mContext = null;
        clientCallbacks = new RemoteCallbackList<>();
    }

    @Override
    public AttachInfo getAttachInfo() throws RemoteException {
        Log.i(TAG, "getAttachInfo: ");
        return Main.getInstance(mContext).getAttachInfo();
    }

    @Override
    public Map invokeMethod(String method, Map params) throws RemoteException {
        Log.i(TAG, "remoteMethod: " + method);
        if (TextUtils.isEmpty(method)) {
            return null;
        }
        switch (method) {
            case Constant.REMOTE_METHOD_IS_CONNECT:
                return handleIsConnect(method, params);
            case Constant.REMOTE_METHOD_SUBSCRIBE_TOPIC:
                return handleSubscribe(method, params);
            case Constant.REMOTE_METHOD_UN_SUBSCRIBE_TOPIC:
                return handleUnSubscribe(method, params);
            case Constant.REMOTE_METHOD_PACKAGE_METHOD:
                return handlePackageMethod(method, params);
            default:
                return handleNoSuchMethod(method, params);
        }
    }

    private Map<String, String> handleIsConnect(String method, Map<String, String> params) {
        Map<String, String> result = new HashMap<>(1);
        int state = CCPushSDK.getState();
        String value = Boolean.toString(state == PushStateHolder.STATE_PUSH_CONNECT);
        Log.i(TAG, "handleIsConnect: result: " + value);
        result.put(Constant.REMOTE_METHOD_RESULT_KEY, value);
        return result;
    }

    private Map<String, String> handleNoSuchMethod(String method, Map<String, String> params) {
        Log.w(TAG, "handleNoSuchMethod: " + method);
        Map<String, String> result = new HashMap<>(1);
        result.put(Constant.REMOTE_METHOD_RESULT_KEY, Constant.REMOTE_METHOD_RESULT_NO_SUCH_METHOD);
        return result;
    }

    private Map<String, String> handleSubscribe(String method, Map<String, String> params) {
        Log.i(TAG, "handleSubscribe: ");
        if (params == null) {
            Log.e(TAG, "handleSubscribe: params null");
            return null;
        }
        String topic = params.get(REMOTE_METHOD_PARAMS_TOPIC);
        Main.getInstance(mContext).getSocketChannel().subscribeTopic(topic);
        return null;
    }

    private Map<String, String> handleUnSubscribe(String method, Map<String, String> params) {
        Log.i(TAG, "handleUnSubscribe: ");
        if (params == null) {
            Log.e(TAG, "handleSubscribe: params null");
            return null;
        }
        String topic = params.get(REMOTE_METHOD_PARAMS_TOPIC);
        Main.getInstance(mContext).getSocketChannel().unSubscribeTopic(topic);
        return null;
    }

    private Map<String, String> handlePackageMethod(String method, Map<String, String> params) {
        Log.i(TAG, "handlePackageMethod: ");
        if (params == null) {
            return null;
        }
        String packageName = params.get("package");
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        int count = clientCallbacks.beginBroadcast();
        Map result = null;
        try {
            for (int i = 0; i < count; i++) {
                String cookie = (String) clientCallbacks.getBroadcastCookie(i);
                if (packageName.equalsIgnoreCase(cookie)) {
                    result = clientCallbacks.getBroadcastItem(i).invokeCallBackMethod(params.get(Constant.REMOTE_METHOD), params);
                    break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        clientCallbacks.finishBroadcast();
        Log.w(TAG, "handlePackageMethod: not found target package: " + packageName);
        return result;
    }

    @Override
    public void setClientCallback(String packageName, IRemotePlatformClientCallback callback) throws RemoteException {
        Log.i(TAG, "setClientCallback: " + packageName);
        clientCallbacks.register(callback, packageName);
    }

    @Override
    public void clearClientCallback(IRemotePlatformClientCallback callback) throws RemoteException {
        Log.i(TAG, "clearClientCallback: ");
        clientCallbacks.unregister(callback);
    }

    public void destroy() {
        Log.i(TAG, "destroy: ");
        if (clientCallbacks != null) {
            clientCallbacks.kill();
        }
    }
}
