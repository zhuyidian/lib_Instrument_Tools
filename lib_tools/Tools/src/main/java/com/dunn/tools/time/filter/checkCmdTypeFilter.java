package com.dunn.tools.time.filter;

import com.dunn.tools.time.bean.RemoteCommand;
import com.dunn.tools.time.constant.TimeConstant;

/**
 * Author:zhuyidian
 * Date:2021/8/31 14:37
 * Description:checkCmdTypeFilter
 */
public class checkCmdTypeFilter implements ITimeTaskFilter {
    @Override
    public boolean filter(RemoteCommand command) {
        int cmdType = command.cmdType;
        if (cmdType == TimeConstant.CMD_TYPE_SHUTDOWN ||
                cmdType == TimeConstant.CMD_TYPE_REBOOT ||
                cmdType == TimeConstant.CMD_TYPE_VOLUME) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String filterName() {
        return "check cmdType filter";
    }
}
