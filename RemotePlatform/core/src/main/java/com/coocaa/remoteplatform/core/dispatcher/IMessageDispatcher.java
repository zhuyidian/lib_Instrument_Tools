package com.coocaa.remoteplatform.core.dispatcher;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IMessageDispatcher
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:07 PM
 * @Description:
 */
public interface IMessageDispatcher {
    boolean dispatchMessage(RemoteCommand command);

    boolean realDispatchMessage(RemoteCommand command);
}
