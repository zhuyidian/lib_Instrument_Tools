package com.coocaa.remoteplatform.core.connection;

import android.content.Context;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IConnectChannel
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:41 PM
 * @Description:
 */
public interface IConnectChannel {
    void init(Context context);

    void setChannelListener(IChannelReceiveCallback callback);

    void handleMessage(RemoteCommand command);

    String getType();

    boolean replyMessage(RemoteCommand command);

    void destroy();
}
