package com.coocaa.remoteplatform.core.connection.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.coocaa.pushlib.CCPushSDK;
import com.coocaa.pushlib.impl.PushStateHolder;
import com.coocaa.remoteplatform.core.BuildConfig;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.connection.IChannelReceiveCallback;
import com.coocaa.remoteplatform.core.connection.IConnectChannel;
import com.coocaa.remoteplatform.core.connection.Utils;
import com.coocaa.remoteplatform.core.service.AttachInfo;

import static com.coocaa.remoteplatform.core.common.Constant.CLIENT_RECEIVER_ACTION;
import static com.coocaa.remoteplatform.core.common.Constant.REMOTE_METHOD_IS_CONNECT;
import static com.coocaa.remoteplatform.core.common.Constant.REMOTE_METHOD_RESULT_KEY;
import static com.coocaa.remoteplatform.core.common.Constant.REMOTE_METHOD_TYPE_KEY;
import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_PUSH;

/**
 * @ClassName: PushChannel
 * @Author: XuZeXiao
 * @CreateDate: 3/17/21 4:52 PM
 * @Description:
 */
public class PushChannel implements IConnectChannel {
    private static final String TAG = "PushChannel";
    private IChannelReceiveCallback mCallback = null;
    private Context mContext = null;
    private AttachInfo mAttachInfo = null;

    public PushChannel(AttachInfo attachInfo) {
        mAttachInfo = attachInfo;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        initPushSDK();
        registerReceiver();
    }

    private void initPushSDK() {
        Log.i(TAG, "initPushSDK: ");
        String activeId = mAttachInfo.getActiveId();
        String deviceId = mAttachInfo.getDeviceId();
        String address = com.remoteplatform.commom.Constant.getPushAddress(mContext);
        int port = com.remoteplatform.commom.Constant.getPushPort(mContext);
        Log.i(TAG, "initPushSDK: activeId=" + activeId+", deviceId="+deviceId+", address="+address+
                ", port="+port);
        if (TextUtils.isEmpty(activeId) && TextUtils.isEmpty(deviceId)) {
            Log.w(TAG, "initPushSDK: activeId and deviceId empty init push sdk later");
            return;
        }
        CCPushSDK.init(mContext, new CCPushSDK.Config(BuildConfig.DEBUG, activeId, deviceId, address, port), pushStateCallBack);
    }

    private CCPushSDK.StateCallback pushStateCallBack = new CCPushSDK.StateCallback() {
        @Override
        public void onStateChanged(int i) {
            Log.i(TAG, "onStateChanged: " + i);
            String isConnect = Boolean.toString(PushStateHolder.STATE_PUSH_CONNECT == i);
            Log.i(TAG, "onStateChanged: isConnect=" + isConnect);
            Intent intent = new Intent();
            intent.setAction(CLIENT_RECEIVER_ACTION);
            intent.putExtra(REMOTE_METHOD_TYPE_KEY, REMOTE_METHOD_IS_CONNECT);
            intent.putExtra(REMOTE_METHOD_RESULT_KEY, isConnect);
            mContext.sendBroadcast(intent);
        }
    };

    private final BroadcastReceiver attachInfoChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String which = intent.getStringExtra(Constant.ATTACH_INFO_CHANGE_WHICH);
            String newActiveId = intent.getStringExtra(Constant.ATTACH_INFO_CHANGE_NEW_VALUE);
            if ("activeId".equalsIgnoreCase(which)) {
                Log.i(TAG, "onReceive: newActiveId:" + newActiveId);
                initPushSDK();
            }
        }
    };

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ATTACH_INFO_CHANGE_ACTION);
        mContext.registerReceiver(attachInfoChangeReceiver, intentFilter);
    }

    void onReceiveMessage(PushCommand pushCommand) {
        if (mCallback != null) {
            mCallback.onReceiveCommand(convertPushCommandToCommand(pushCommand));
        }
    }

    RemoteCommand convertPushCommandToCommand(PushCommand pushCommand) {
        RemoteCommand result = null;
        try {
            result = Utils.createRemoteCommandFromJson(pushCommand.msg);
            result.msgOrigin = COMMAND_SOURCE_PUSH;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void setChannelListener(IChannelReceiveCallback callback) {
        mCallback = callback;
    }

    @Override
    public void handleMessage(RemoteCommand command) {

    }

    @Override
    public String getType() {
        return COMMAND_SOURCE_PUSH;
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean disConnect() {
        return false;
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public boolean replyMessage(RemoteCommand command) {
        return false;
    }

    @Override
    public void destroy() {
        if (mContext != null) {
            mContext.unregisterReceiver(attachInfoChangeReceiver);
        }
    }
}
