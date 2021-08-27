package com.coocaa.remoteplatform.core.connection;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IChannelCallback
 * @Author: XuZeXiao
 * @CreateDate: 3/17/21 5:05 PM
 * @Description:
 */
public interface IChannelReceiveCallback {
    void onReceiveCommand(RemoteCommand command);
}
