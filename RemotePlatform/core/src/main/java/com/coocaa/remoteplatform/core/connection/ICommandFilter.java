package com.coocaa.remoteplatform.core.connection;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: ICommandFilter
 * @Author: XuZeXiao
 * @CreateDate: 3/25/21 3:44 PM
 * @Description:
 */
public interface ICommandFilter {
    boolean filter(RemoteCommand command);

    String filterName();
}
