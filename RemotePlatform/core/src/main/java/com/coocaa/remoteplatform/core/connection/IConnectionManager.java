package com.coocaa.remoteplatform.core.connection;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IConnectionManager
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:38 PM
 * @Description:
 */
public interface IConnectionManager {
    boolean isConnected();

    boolean replyMessage(RemoteCommand command);

    void destroy();
}
