package com.coocaa.remoteplatform.baseability;

import android.content.Context;

import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: IBaseAbility
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 5:08 PM
 * @Description:
 */
public interface IBaseAbility {
    void init(Context context);

    void handleMessage(Context context, RemoteCommand command);
}
