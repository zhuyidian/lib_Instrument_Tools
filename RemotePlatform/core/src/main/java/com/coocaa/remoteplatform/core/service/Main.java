package com.coocaa.remoteplatform.core.service;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.core.clientmanager.ClientFinder;
import com.coocaa.remoteplatform.core.clientmanager.ClientManager;
import com.coocaa.remoteplatform.core.clientmanager.IClientManager;
import com.coocaa.remoteplatform.core.connection.ConnectionManager;
import com.coocaa.remoteplatform.core.connection.IConnectionManager;
import com.coocaa.remoteplatform.core.connection.push.PushChannel;
import com.coocaa.remoteplatform.core.connection.socket.SocketChannel;
import com.coocaa.remoteplatform.core.dispatcher.IMessageDispatcher;
import com.coocaa.remoteplatform.core.dispatcher.MessageDispatcher;
import com.coocaa.remoteplatform.core.reply.IMessageReply;
import com.coocaa.remoteplatform.core.reply.MessageReply;
import com.coocaa.remoteplatform.core.request.HomeHeader;
import com.coocaa.remoteplatform.core.request.TimeHttpMethod;
import com.remoteplatform.commom.LogUtil;

import swaiotos.sal.SalModule;
import swaiotos.sal.platform.IDeviceInfo;

/**
 * @ClassName: Entry
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:19 PM
 * @Description:
 */
public class Main implements IMain {
    private final IClientManager mClientManager;
    private final IConnectionManager mConnectionManager;
    private final IMessageDispatcher mMessageDispatcher;
    private final IMessageReply mMessageReply;
    private final PushChannel mPushChannel;
    private final SocketChannel mSocketChannel;
    private final AttachInfo mAttachInfo;
    private Context mContext = null;
    private static IMain instance = null;
    private IDeviceInfo mIDeviceInfo = null;

    public static IMain getInstance(Context context) {
        if (instance == null) {
            synchronized (IMain.class) {
                if (instance == null) {
                    instance = new Main(context);
                    instance.init();
                }
            }
        }
        return instance;
    }

    public Main(Context context) {
        this.mContext = context;
        HomeHeader.init(mContext);
        mAttachInfo = new AttachInfo(context);
        initAttachInfo();
        mClientManager = new ClientManager(new ClientFinder(), mContext);
        mMessageDispatcher = new MessageDispatcher(mClientManager, mContext);
        mPushChannel = new PushChannel(mAttachInfo);
        mPushChannel.init(mContext);
        requestTimeData();
        mSocketChannel = new SocketChannel(mAttachInfo);
        mSocketChannel.init(mContext);
        mConnectionManager = new ConnectionManager(mContext, mMessageDispatcher, mPushChannel, mSocketChannel);
        mMessageReply = new MessageReply(mConnectionManager);
    }

    private void initAttachInfo() {
        mAttachInfo.recoverFromSp();
        mIDeviceInfo = swaiotos.sal.SAL.getModule(mContext, SalModule.DEVICE_INFO);
        try {
            mAttachInfo.setMac(mIDeviceInfo.getMac());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        mClientManager.findClients();
    }

    @Override
    public void requestTimeData() {
        TimeHttpMethod mTimeHttpMethod = new TimeHttpMethod(mContext);
        LogUtil.i("time-api", "get time data init deviceId=" + mAttachInfo.getDeviceId());
        mTimeHttpMethod.getTimeData(mContext, mAttachInfo.getDeviceId());
    }

    @Override
    public IClientManager getClientManager() {
        return mClientManager;
    }

    @Override
    public IConnectionManager getConnectionManager() {
        return mConnectionManager;
    }

    @Override
    public IMessageDispatcher getMessageDispatcher() {
        return mMessageDispatcher;
    }

    @Override
    public IMessageReply getMessageReply() {
        return mMessageReply;
    }

    @Override
    public PushChannel getPushChannel() {
        return mPushChannel;
    }

    @Override
    public SocketChannel getSocketChannel() {
        return mSocketChannel;
    }

    @Override
    public AttachInfo getAttachInfo() {
        return mAttachInfo;
    }

    @Override
    public void destroy() {
        mClientManager.destroy();
    }
}
