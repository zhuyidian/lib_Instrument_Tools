package com.coocaa.remoteplatform.baseability.abilities.time.filter;

import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeAnalysis;
import com.coocaa.remoteplatform.baseability.abilities.time.bean.TimeBean;
import com.coocaa.remoteplatform.baseability.abilities.time.constant.TimeConstant;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

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
