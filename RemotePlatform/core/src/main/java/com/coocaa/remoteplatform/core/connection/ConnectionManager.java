package com.coocaa.remoteplatform.core.connection;

import android.content.Context;
import android.util.Log;

import com.coocaa.remoteplatform.core.connection.push.PushChannel;
import com.coocaa.remoteplatform.core.connection.socket.SocketChannel;
import com.coocaa.remoteplatform.core.dispatcher.IMessageDispatcher;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

import java.util.ArrayList;
import java.util.List;

import static com.coocaa.remoteplatform.core.common.Constant.HOST_PACKAGE_NAME;
import static com.remoteplatform.commom.Constant.CMD_TYPE_CLOSE_SOCKET_CHANNEL;
import static com.remoteplatform.commom.Constant.CMD_TYPE_OPEN_SOCKET_CHANNEL;
import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_PUSH;
import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_SOCKET;

/**
 * @ClassName: ConnectionManager
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:39 PM
 * @Description:
 */
public class ConnectionManager implements IConnectionManager {
    private static final String TAG = "ConnectionManger";
    private IMessageDispatcher mMessageDispatcher;
    private PushChannel mPushChannel = null;
    private SocketChannel mSocketChannel = null;
    private final List<ICommandFilter> mCommandFilters = new ArrayList<>();
    private Context mContext = null;

    public ConnectionManager(Context context, IMessageDispatcher mMessageDispatcher, PushChannel mPushChannel, SocketChannel mSocketChannel) {
        this.mContext = context;
        this.mMessageDispatcher = mMessageDispatcher;
        this.mPushChannel = mPushChannel;
        this.mSocketChannel = mSocketChannel;
        setMessageReceiveCallback(mPushChannel, mSocketChannel);
        mCommandFilters.add(overdueCheckFilter);
        mCommandFilters.add(connectionCommandFilter);
    }

    private final ICommandFilter overdueCheckFilter = new ICommandFilter() {
        private static final int COMMAND_OVERDUE_MS = 30 * 1000;

        @Override
        public boolean filter(RemoteCommand command) {
            boolean result = System.currentTimeMillis() - command.timestamp > COMMAND_OVERDUE_MS;
            if (result) {
                command.replyError(mContext).withMessage("command timestamp > 30s").reply();
            }
            return result;
        }

        @Override
        public String filterName() {
            return "overdue checker filter";
        }
    };

    private final ICommandFilter connectionCommandFilter = new ICommandFilter() {
        @Override
        public boolean filter(RemoteCommand command) {
            if (HOST_PACKAGE_NAME.equalsIgnoreCase(command.clientId)) {
                if (CMD_TYPE_OPEN_SOCKET_CHANNEL == command.cmdType || CMD_TYPE_CLOSE_SOCKET_CHANNEL == command.cmdType) {
                    mSocketChannel.handleMessage(command);
                    return true;
                }
            }
            return false;
        }

        @Override
        public String filterName() {
            return "connection command filter";
        }
    };

    private void setMessageReceiveCallback(IConnectChannel... channel) {
        for (IConnectChannel connectChannel : channel) {
            connectChannel.setChannelListener(channelReceiveCallback);
        }
    }

    private final IChannelReceiveCallback channelReceiveCallback = new IChannelReceiveCallback() {
        @Override
        public void onReceiveCommand(RemoteCommand command) {
            if (command == null) {
                return;
            }
            command.replyReceived(mContext).reply();
            if (filterCommand(command)) {
                return;
            }
            mMessageDispatcher.dispatchMessage(command);

        }
    };

    private boolean filterCommand(RemoteCommand command) {
        for (ICommandFilter filter : mCommandFilters) {
            if (filter.filter(command)) {
                Log.i(TAG, "message handle by filter: " + filter.filterName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean replyMessage(RemoteCommand command) {
        if (COMMAND_SOURCE_PUSH.equalsIgnoreCase(command.msgOrigin)) {
            mPushChannel.replyMessage(command);
            return true;
        } else if (COMMAND_SOURCE_SOCKET.equalsIgnoreCase(command.msgOrigin)) {
            mSocketChannel.replyMessage(command);
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (mPushChannel != null) {
            mPushChannel.destroy();
        }
        if (mSocketChannel != null) {
            mSocketChannel.destroy();
        }
    }
}
