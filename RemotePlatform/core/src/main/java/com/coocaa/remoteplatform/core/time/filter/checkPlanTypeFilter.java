package com.coocaa.remoteplatform.core.time.filter;

import com.coocaa.remoteplatform.core.time.bean.TimeAnalysis;
import com.coocaa.remoteplatform.core.time.bean.TimeBean;
import com.coocaa.remoteplatform.core.time.constant.TimeConstant;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * Author:zhuyidian
 * Date:2021/8/31 14:37
 * Description:checkPlanTypeFilter
 */
public class checkPlanTypeFilter implements ITimeTaskFilter {
    @Override
    public boolean filter(RemoteCommand command) {
        String content = command.content;
        if (content == null) return true;

        TimeBean bean = TimeAnalysis.parseContent(content);
        if (bean == null) return true;

        if (bean.getPlanType() == TimeConstant.CMD_REAL) {
            return true;
        }

        return false;
    }

    @Override
    public String filterName() {
        return "check planType filter";
    }
}
