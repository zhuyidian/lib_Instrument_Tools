package com.dunn.tools.time.filter;

import com.dunn.tools.time.bean.RemoteCommand;

/**
 * @ClassName: ICommandFilter
 * @Author: zhuyidian
 * @Description:
 */
public interface ITimeTaskFilter {
    boolean filter(RemoteCommand command);

    String filterName();
}
