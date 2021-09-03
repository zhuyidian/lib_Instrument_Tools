package com.coocaa.remoteplatform.baseability.abilities.time.filter;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: ICommandFilter
 * @Author: zhuyidian
 * @Description:
 */
public interface ITimeTaskFilter {
    boolean filter(RemoteCommand command);

    String filterName();
}
